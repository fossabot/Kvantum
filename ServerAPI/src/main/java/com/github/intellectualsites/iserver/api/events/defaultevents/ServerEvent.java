/*
 * IntellectualServer is a web server, written entirely in the Java language.
 * Copyright (C) 2017 IntellectualSites
 *
 * This program is free software; you can redistribute it andor modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.github.intellectualsites.iserver.api.events.defaultevents;

import com.github.intellectualsites.iserver.api.core.IntellectualServer;
import com.github.intellectualsites.iserver.api.events.Event;

/**
 * An event wrapper for events that involved the server
 *
 * @author Citymonstret
 */
public abstract class ServerEvent extends Event
{

    private final IntellectualServer server;

    /**
     * Constructor
     *
     * @param server The server instance
     * @param name   The event identifier
     */
    ServerEvent(final IntellectualServer server, final String name)
    {
        super( "is::server::" + name );
        this.server = server;
    }

    /**
     * Get the server instance
     *
     * @return server instance
     */
    public final IntellectualServer getServer()
    {
        return this.server;
    }
}
