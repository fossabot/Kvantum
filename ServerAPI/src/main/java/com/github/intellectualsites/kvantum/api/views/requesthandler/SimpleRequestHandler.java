/*
 * Kvantum is a web server, written entirely in the Java language.
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
package com.github.intellectualsites.kvantum.api.views.requesthandler;

import com.github.intellectualsites.kvantum.api.core.ServerImplementation;
import com.github.intellectualsites.kvantum.api.matching.Router;
import com.github.intellectualsites.kvantum.api.matching.ViewPattern;
import com.github.intellectualsites.kvantum.api.request.AbstractRequest;
import com.github.intellectualsites.kvantum.api.response.Response;
import com.github.intellectualsites.kvantum.api.views.RequestHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class SimpleRequestHandler extends RequestHandler
{

    private static AtomicInteger identifier = new AtomicInteger( 0 );

    private final String pattern;
    private final BiConsumer<AbstractRequest, Response> generator;
    private final boolean forceHTTPS;
    private String internalName = "simpleRequestHandler::" + identifier.getAndIncrement();
    private ViewPattern compiledPattern;

    protected SimpleRequestHandler(String pattern, BiConsumer<AbstractRequest, Response> generator)
    {
        this( pattern, generator, false );
    }

    protected SimpleRequestHandler(String pattern, BiConsumer<AbstractRequest, Response> generator, boolean forceHTTPS)
    {
        this.pattern = pattern;
        this.generator = generator;
        this.forceHTTPS = forceHTTPS;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public SimpleRequestHandler addToRouter(final Router router)
    {
        return (SimpleRequestHandler) router.add( this );
    }

    public void setInternalName(String internalName)
    {
        this.internalName = internalName;
    }

    protected ViewPattern getPattern()
    {
        if ( compiledPattern == null )
        {
            compiledPattern = new ViewPattern( pattern );
        }
        return compiledPattern;
    }

    @Override
    public String toString()
    {
        return this.pattern;
    }

    @Override
    public boolean matches(final AbstractRequest request)
    {
        final Map<String, String> map = getPattern().matches( request.getQuery().getFullRequest() );
        if ( map != null )
        {
            request.addMeta( "variables", map );
        }
        return map != null;
    }

    @Override
    public final Response generate(final AbstractRequest r)
    {
        final Response response = new Response( this );
        generator.accept( r, response );
        return response;
    }

    @Override
    public String getName()
    {
        return this.internalName;
    }

    @Override
    public boolean forceHTTPS()
    {
        return this.forceHTTPS;
    }

    final public void register()
    {
        ServerImplementation.getImplementation().getRouter().add( this );
    }

    public static final class Builder
    {

        private String pattern;
        private BiConsumer<AbstractRequest, Response> generator;

        private Builder()
        {
        }

        public Builder setPattern(final String pattern)
        {
            this.pattern = pattern;
            return this;
        }

        public Builder setGenerator(final BiConsumer<AbstractRequest, Response> generator)
        {
            this.generator = generator;
            return this;
        }

        public SimpleRequestHandler build()
        {
            return new SimpleRequestHandler( pattern, generator );
        }
    }

}
