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
package com.github.intellectualsites.kvantum.api.views.rest.service;

import com.github.intellectualsites.kvantum.api.account.IAccount;
import com.github.intellectualsites.kvantum.api.core.ServerImplementation;
import com.github.intellectualsites.kvantum.api.orm.KvantumObjectFactory;
import com.github.intellectualsites.kvantum.api.response.Header;
import com.github.intellectualsites.kvantum.api.session.ISession;
import com.github.intellectualsites.kvantum.api.util.ParameterScope;
import com.github.intellectualsites.kvantum.api.util.SearchResultProvider;
import com.github.intellectualsites.kvantum.api.views.RequestHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.Collection;
import java.util.Optional;

/**
 * Create a REST search gateway that will search within a specified datastore for Kvantum
 * objects and automatically serve them via JSON.
 * <p>
 * Example: <pre>{@code
 * Rest.createSearch(
 *    "/search",
 *    Account.class,
 *    ParameterScope.GET,
 *    ServerImplementation.getImplementation().getApplicationStructure().getAccountManager()
 * );}</pre>
 * </p>
 * <p>
 * To limit the API to certain roles/permissions etc, you may just add a Middleware to the request handler.
 * (See {@link RequestHandler#getMiddlewareQueuePopulator()} and
 * {@link com.github.intellectualsites.kvantum.api.views.requesthandler.Middleware}
 * </p>
 * @param <QueryType> Type used to query objects
 * @param <ObjectType> Object type
 */
@Getter
@Builder
final public class SearchService<QueryType, ObjectType>
{

    /**
     * URL Filter ({@link com.github.intellectualsites.kvantum.api.matching.ViewPattern})
     */
    private final String filter;

    /**
     * Class, must comply to KvantumObject specifications (see {@link KvantumObjectFactory})
     */
    private final Class<? extends QueryType> queryObjectType;

    /**
     * Provider of search results, i.e {@link com.github.intellectualsites.kvantum.api.account.IAccountManager}
     */
    private final SearchResultProvider<QueryType, ObjectType> resultProvider;

    /**
     * Whether GET or POST parameters will be used to read the object
     */
    @Builder.Default
    private ParameterScope parameterScope = ParameterScope.GET;

    /**
     * Can be used to set a permission that is required for the service to function.
     * (See {@link com.github.intellectualsites.kvantum.api.account.roles.AccountRole} and
     * {@link com.github.intellectualsites.kvantum.api.account.IAccount#isPermitted(String)})
     */
    @Builder.Default
    private String permissionRequirement = "";

    /**
     * Register the request handler in the server
     * {@link com.github.intellectualsites.kvantum.api.util.RequestManager} implementation
     * @return The created request handler
     */
    public RequestHandler registerHandler()
    {
        return ServerImplementation.getImplementation().createSimpleRequestHandler( filter, ( (request, response) ->
        {
            if ( !getPermissionRequirement().isEmpty() )
            {
                boolean hasPermission;
                final ISession session = request.getSession();
                if ( session == null )
                {
                    hasPermission = false;
                } else
                {
                    final Optional<IAccount> accountOptional = ServerImplementation.getImplementation()
                            .getApplicationStructure().getAccountManager().getAccount( request.getSession() );
                    hasPermission = accountOptional.map( iAccount ->
                            iAccount.isPermitted( getPermissionRequirement() ) ).orElse( false );
                }
                if ( !hasPermission )
                {
                    final JsonObject requestStatus = new JsonObject();
                    requestStatus.add( "status", new JsonPrimitive( "error" ) );
                    requestStatus.add( "message", new JsonPrimitive( "Not permitted" ) );
                    response.setContent( ServerImplementation.getImplementation().getGson().toJson( requestStatus ) );
                    return;
                }
            }

            final val factory = KvantumObjectFactory.from( queryObjectType ).build( parameterScope );
            final val result = factory.parseRequest( request );
            response.getHeader().set( Header.HEADER_CONTENT_TYPE, Header.CONTENT_TYPE_JSON );
            if ( !result.isSuccess() )
            {
                final JsonObject requestStatus = new JsonObject();
                requestStatus.add( "message", new JsonPrimitive( result.getError().getCause() ) );
                response.setContent( ServerImplementation.getImplementation().getGson().toJson( requestStatus ) );
                return;
            }
            final QueryType query = result.getParsedObject();
            final Collection<? extends ObjectType> queryResult = resultProvider.getResults( query );
            if ( queryResult.isEmpty() )
            {
                final JsonObject requestStatus = new JsonObject();
                requestStatus.add( "status", new JsonPrimitive( "error" ) );
                requestStatus.add( "message", new JsonPrimitive( "No such object" ) );
                requestStatus.add( "query", ServerImplementation.getImplementation()
                        .getGson().toJsonTree( query ) );
                response.setContent( ServerImplementation.getImplementation().getGson().toJson( requestStatus ) );
            } else
            {
                final JsonObject requestStatus = new JsonObject();
                requestStatus.add( "status", new JsonPrimitive( "success" ) );
                requestStatus.add( "query", ServerImplementation.getImplementation()
                        .getGson().toJsonTree( query ) );
                final JsonArray resultArray = new JsonArray();
                for ( final ObjectType t : queryResult )
                {
                    resultArray.add( ServerImplementation.getImplementation()
                            .getGson().toJsonTree( t ) );
                }
                requestStatus.add( "result", resultArray );
                response.setContent( ServerImplementation.getImplementation().getGson().toJson( requestStatus ) );
            }
        } ) );
    }

}
