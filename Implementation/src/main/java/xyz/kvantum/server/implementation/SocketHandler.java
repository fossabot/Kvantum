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
package xyz.kvantum.server.implementation;

import xyz.kvantum.server.api.config.ConfigurationFile;
import xyz.kvantum.server.api.config.CoreConfig;
import xyz.kvantum.server.api.config.Message;
import xyz.kvantum.server.api.config.YamlConfiguration;
import xyz.kvantum.server.api.core.ServerImplementation;
import xyz.kvantum.server.api.logging.Logger;
import xyz.kvantum.server.api.socket.ISocketHandler;
import xyz.kvantum.server.api.socket.SocketContext;
import xyz.kvantum.server.api.socket.SocketFilter;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({ "unused", "WeakerAccess" })
final class SocketHandler implements ISocketHandler
{

    @SuppressWarnings("ALL")
    public static final SocketFilter SOCKET_FILTER_IS_ACTIVE = socket -> socket.isActive();
    public static final SocketFilter SOCKET_FILTER_ENABLE_SOCKET = socket -> false;

    private static final Map<String, SocketFilter> availableSocketFilters = new HashMap<>();

    static
    {
        availableSocketFilters.put( "1isActive", SOCKET_FILTER_IS_ACTIVE );
        availableSocketFilters.put( "0all", SOCKET_FILTER_ENABLE_SOCKET );
    }

    private final ExecutorService executorService;
    private final List<SocketFilter> socketFilters;
    private final Collection<SocketContext> socketContexts;
    private final KvantumPipeline kvantumPipeline;

    SocketHandler() throws Throwable
    {
        this.executorService = Executors.newFixedThreadPool( CoreConfig.Pools.workers );
        this.socketFilters = new ArrayList<>();
        this.socketContexts = new HashSet<>();
        this.kvantumPipeline = new KvantumPipeline();

        try
        {
            ConfigurationFile configurationFile = new YamlConfiguration( "socketFilters", new File( new File(
                    ServerImplementation
                            .getImplementation().getCoreFolder(), "config" ), "socketFilters.yml" ) );
            configurationFile.loadFile();
            availableSocketFilters.forEach( (key, value) -> configurationFile.setIfNotExists( key.substring( 1 ),
                    key.charAt( 0 ) == '1' ) );
            configurationFile.saveFile();
            availableSocketFilters.entrySet().stream().filter( entry -> configurationFile.get( entry.getKey()
                    .substring( 1 ) ) )
                    .forEach( entry -> socketFilters.add( entry.getValue() ) );
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    static Map<String, SocketFilter> getSocketFilters() throws Exception
    {
        final Map<String, SocketFilter> filters = new HashMap<>();
        for ( final Field field : SocketHandler.class.getDeclaredFields() )
        {
            if ( Modifier.isStatic( field.getModifiers() ) && field.getType().equals( SocketFilter.class ) )
            {
                filters.put( field.getName(), (SocketFilter) field.get( null ) );
            }
        }
        return filters;
    }

    @Override
    public void acceptSocket(final SocketContext s)
    {
        for ( final SocketFilter filter : socketFilters )
        {
            if ( !filter.filter( s ) )
            {
                Logger.debug( "SocketFilter filtered out Socket: " + s );
                breakSocketConnection( s );
                return;
            }
        }
        this.socketContexts.add( s );
        if ( CoreConfig.debug )
        {
            Logger.debug( "Accepting Socket: " + s.toString() );
        }
        this.executorService.execute( () -> kvantumPipeline.accept( this, s ) );
    }

    @Override
    public void breakSocketConnection(final SocketContext s)
    {
        if ( s.isActive() )
        {
            try
            {
                s.getSocket().close();
            } catch ( final Exception e )
            {
                e.printStackTrace();
            }
        }
        try
        {
            if ( s.getTempFileManager() != null )
            {
                s.getTempFileManager().clearTempFiles();
            }
        } catch ( final Exception e )
        {
            e.printStackTrace();
        }
        this.socketContexts.remove( s );
    }

    @Override
    public void handleShutdown()
    {
        //
        // Make sure connections are closed
        //
        socketContexts.forEach( this::breakSocketConnection );
        //
        // Close worker threads
        //
        Message.WAITING_FOR_EXECUTOR_SERVICE.log();
        try
        {
            this.executorService.shutdownNow();
            this.executorService.awaitTermination( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }

}