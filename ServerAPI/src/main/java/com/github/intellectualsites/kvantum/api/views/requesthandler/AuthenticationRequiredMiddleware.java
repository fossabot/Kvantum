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

import com.github.intellectualsites.kvantum.api.account.IAccountManager;
import com.github.intellectualsites.kvantum.api.config.CoreConfig;
import com.github.intellectualsites.kvantum.api.core.ServerImplementation;
import com.github.intellectualsites.kvantum.api.request.Request;

public class AuthenticationRequiredMiddleware extends Middleware
{

    @Override
    public void handle(Request request, MiddlewareQueue queue)
    {
        final IAccountManager accountManager = ServerImplementation.getImplementation()
                .getApplicationStructure().getAccountManager();
        if ( accountManager != null && accountManager.getAccount( request.getSession() ).isPresent() )
        {
            queue.handle( request );
        } else
        {
            request.internalRedirect( CoreConfig.Middleware.loginRedirect );
        }
    }

}
