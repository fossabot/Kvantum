/*
 *    _  __                     _
 *    | |/ /__   __ __ _  _ __  | |_  _   _  _ __ ___
 *    | ' / \ \ / // _` || '_ \ | __|| | | || '_ ` _ \
 *    | . \  \ V /| (_| || | | || |_ | |_| || | | | | |
 *    |_|\_\  \_/  \__,_||_| |_| \__| \__,_||_| |_| |_|
 *
 *    Copyright (C) 2018 IntellectualSites
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

import javax.annotation.Nullable;
import xyz.kvantum.server.api.session.ISession;
import xyz.kvantum.server.api.session.ISessionDatabase;
import xyz.kvantum.server.api.session.SessionLoad;
import xyz.kvantum.server.api.util.AsciiString;

/**
 * Session database without any functionality
 */
public final class DumbSessionDatabase implements ISessionDatabase
{

	@Override public void setup() throws Exception
	{
	}

	@Nullable @Override public SessionLoad getSessionLoad(final AsciiString sessionID)
	{
		return null; // Nullable
	}

	@Override public void storeSession(final ISession session)
	{
	}

	@Override public void updateSession(final AsciiString session)
	{
	}

	@Override public void deleteSession(final AsciiString session)
	{
	}
}
