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
package xyz.kvantum.server.api.jtwig;

import xyz.kvantum.server.api.config.CoreConfig;
import xyz.kvantum.server.api.core.ServerImplementation;
import xyz.kvantum.server.api.template.TemplateHandler;

public class JTwigEngine extends TemplateHandler
{

    private static JTwigEngine instance;

    private JTwigEngine()
    {
        super( CoreConfig.TemplatingEngine.JTWIG, "JTwigEngine" );
    }

    public static JTwigEngine getInstance()
    {
        if ( instance == null )
        {
            instance = new JTwigEngine();
        }
        return instance;
    }

    public void onLoad()
    {
        ServerImplementation.getImplementation().getProcedure().addProcedure( "syntax", new SyntaxHandler( this ) );
    }
}