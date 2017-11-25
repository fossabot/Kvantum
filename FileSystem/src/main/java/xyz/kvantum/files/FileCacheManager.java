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
package xyz.kvantum.files;

import java.util.Optional;

/**
 * High-level manager for file caching
 */
public interface FileCacheManager
{

    /**
     * Read a file from the file cache if it exists in the cache
     *
     * @param string (Unique) File identifier
     * @return Optional file content
     */
    Optional<String> readCachedFile(String string);

    /**
     * Write a file to the file cache
     * @param string (Unique) File identifier
     * @param content File content
     */
    void writeCachedFile(String string, String content);

}