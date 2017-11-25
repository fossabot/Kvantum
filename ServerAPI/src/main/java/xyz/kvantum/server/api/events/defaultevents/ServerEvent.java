/*
 *
 *    Copyright (C) 2017 IntellectualSites
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.kvantum.server.api.events.defaultevents;

import xyz.kvantum.server.api.core.Kvantum;
import xyz.kvantum.server.api.events.Event;

/**
 * An event wrapper for events that involved the server
 *
 * @author Citymonstret
 */
public abstract class ServerEvent extends Event
{

    private final Kvantum server;

    /**
     * Constructor
     *
     * @param server The server instance
     * @param name   The event identifier
     */
    ServerEvent(final Kvantum server, final String name)
    {
        super( "is::server::" + name );
        this.server = server;
    }

    /**
     * Get the server instance
     *
     * @return server instance
     */
    public final Kvantum getServer()
    {
        return this.server;
    }
}