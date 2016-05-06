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

package de.jackwhite20.japs.server.command.impl;

import de.jackwhite20.japs.server.JaPS;
import de.jackwhite20.japs.server.command.Command;

/**
 * Created by JackWhite20 on 17.07.2015.
 */
public class HelpCommand extends Command {

    public HelpCommand(String name, String[] aliases, String description) {
        super(name, aliases, description);
    }

    @Override
    public boolean execute(String[] args) {
        JaPS.getLogger().info("Available Commands:");
        for (Command command : JaPS.getCommandManager().getCommands()) {
            JaPS.getLogger().info(command.getName() + " [" + String.join(", ", command.getAliases()) + "] - " + command.getDescription());
        }
        return true;
    }
}