/*
 * Copyright (c) 2016 "JackWhite20"
 *
 * This file is part of JaPS.
 *
 * JaPS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.jackwhite20.japs.server.cache;

import de.jackwhite20.japs.server.JaPS;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JackWhite20 on 13.06.2016.
 */
public class JaPSCache {

    private Logger logger = JaPS.getLogger();

    private Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private ScheduledExecutorService executorService;

    private int cleanupInterval;

    public JaPSCache(int cleanupInterval) {

        this.cleanupInterval = cleanupInterval;

        if (cleanupInterval > 0) {
            executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(this::cleanup, cleanupInterval, cleanupInterval, TimeUnit.SECONDS);
        }
    }

    private void cleanup() {

        for(Iterator<CacheEntry> it = cache.values().iterator(); it.hasNext(); ) {
            CacheEntry cacheEntry = it.next();

            long timestamp = cacheEntry.expireBy();
            if (timestamp != -1 && System.currentTimeMillis() > timestamp) {
                it.remove();

                logger.log(Level.FINE, "Value '" + cacheEntry.value() + "' has been removed from the cache");
            }
        }
    }

    public void close() {

        if(executorService != null) {
            executorService.shutdown();
        }
    }

    public void put(String key, Object value, int secondsToLive) {

        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        long expireBy = secondsToLive != -1 ? System.currentTimeMillis() + (secondsToLive * 1000) : secondsToLive;

        cache.put(key, new CacheEntry(expireBy, value));
    }

    public void put(String key, Object value) {

        put(key, value, -1);
    }

    public Object get(String key) {

        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        CacheEntry entry = cache.get(key);

        if (entry == null) {
            return null;
        }

        long timestamp = entry.expireBy();
        if (timestamp != -1 && System.currentTimeMillis() > timestamp) {
            remove(key);
            return null;
        }

        return entry.value();
    }

    public Object getAndRemove(String key) {

        if (key == null) {
            return null;
        }

        CacheEntry entry = cache.get(key);

        if (entry != null) {
            return cache.remove(key).value();
        }

        return null;
    }

    public boolean remove(String key) {

        return getAndRemove(key) != null;
    }
}