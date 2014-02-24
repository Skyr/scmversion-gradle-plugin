/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ploing.scmversion


/**
 * @author Stefan Schlott
 */
public interface SCMOperations {
    /**
     * Determines if a repo is dirty, i.e. there are uncommitted changes
     * @return true if there are uncommitted changes
     */
    abstract boolean isRepoDirty()

    /**
     * Determines the version string of the current head
     * @return the version id of the current head
     */
    abstract String getHeadVersion()

    /**
     * Gets all tags attached to the current head
     * @return a set of tags (may be empty, is never null)
     */
    abstract Set<String> getHeadTags()

    /**
     * Get all tags of the repository
     * @return a set of all tags (may be empty, is never null)
     */
    abstract Set<String> getTags()
}