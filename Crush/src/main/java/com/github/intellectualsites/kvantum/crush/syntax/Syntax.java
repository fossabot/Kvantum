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
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//     Kvantum is a web server, written entirely in the Java language.                            /
//     Copyright (C) 2015 IntellectualSites                                                                  /
//                                                                                                           /
//     This program is free software; you can redistribute it and/or modify                                  /
//     it under the terms of the GNU General Public License as published by                                  /
//     the Free Software Foundation; either version 2 of the License, or                                     /
//     (at your option) any later version.                                                                   /
//                                                                                                           /
//     This program is distributed in the hope that it will be useful,                                       /
//     but WITHOUT ANY WARRANTY; without even the implied warranty of                                        /
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                         /
//     GNU General Public License for more details.                                                          /
//                                                                                                           /
//     You should have received a copy of the GNU General Public License along                               /
//     with this program; if not, write to the Free Software Foundation, Inc.,                               /
//     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.                                           /
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

package com.github.intellectualsites.kvantum.crush.syntax;

import com.github.intellectualsites.kvantum.api.request.AbstractRequest;
import com.github.intellectualsites.kvantum.api.util.ProviderFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is used to create Crush syntax
 * such as {{variable.name}} or the foreach loop.
 * <p>
 * This has to be registered by the server in order to be used
 *
 * @author Citymonstret
 */
public abstract class Syntax
{

    private final Pattern pattern;

    /**
     * Constructor
     *
     * @param pattern The regex pattern used to match the code
     */
    public Syntax(final Pattern pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Process the input string
     *
     * @param in        Code Input
     * @param matcher   RegEx ViewMatcher
     * @param r         HTML Request
     * @param factories Provider Factories
     * @return Processed string
     * @see #handle(String, AbstractRequest, Map) Simple wrapper
     */
    public abstract String process(String in, Matcher matcher, AbstractRequest r, Map<String, ProviderFactory> factories);

    /**
     * A simple wrapper for the process method
     *
     * @param in        String input
     * @param r         HTML Request
     * @param factories Provider Factories
     * @return Processed Input
     * @see #process(String, Matcher, AbstractRequest, Map) Wraps around this
     */
    public final String handle(final String in, final AbstractRequest r, final Map<String, ProviderFactory> factories)
    {
        return process( in, pattern.matcher( in ), r, factories );
    }

    /**
     * Check if the regex pattern matches
     *
     * @param in Code Input
     * @return True if the regex matches
     */
    public final boolean matches(final String in)
    {
        return this.pattern.matcher( in ).find();
    }

    /**
     * Get the identifier pattern object
     *
     * @return Pattern
     */
    public Pattern getPattern()
    {
        return this.pattern;
    }
}
