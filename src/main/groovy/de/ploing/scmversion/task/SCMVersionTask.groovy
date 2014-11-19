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
package de.ploing.scmversion.task

import de.ploing.scmversion.SCMVersionPlugin
import org.gradle.api.DefaultTask

/**
 * @author Stefan Schlott
 */
abstract class SCMVersionTask extends DefaultTask {
    SCMVersionPlugin plugin

    /**
     * Tries to extract the version information from a tag string
     * @param tag
     * @return the version if found, null if not found
     */
    String extractVersion(String tag) {
        def pattern = ~ project.scmversion.releaseTagPattern
        def m = pattern.matcher(tag)
        if (m.matches()) {
            return m[0][1]
        } else {
            return null
        }
    }

    /**
     * Tries to extract a version information from all tags of the current head
     * @return first version match, null if none found
     */
    String getHeadVersion() {
        def version = null
        if (plugin.scmOperations!=null) {
            plugin.scmOperations.headTags.each { tag ->
                def tagVersion = extractVersion(tag)
                if (tagVersion!=null) {
                    version = tagVersion
                }
            }
        }
        return version
    }

    /**
     * Tries to extract the version information from all tags of the repo
     * @return a list with version strings (never null)
     */
    List<String> getVersions() {
        def result = []
        if (plugin.scmOperations!=null) {
            plugin.scmOperations.tags.each { tag ->
                def tagVersion = extractVersion(tag)
                if (tagVersion != null) {
                    result.add(tagVersion)
                }
            }
        }
        return result
    }

    /**
     * Sorts all versions (from repo tags) according to the sort closure from the configuration
     * @return sorted list (never null)
     */
    List<String> getSortedVersions() {
        return versions.sort(project.scmversion.versionComparator)
    }

    /**
     * Gets (or calculates) the version of the repo. If the current head has a version tag, this version is
     * returned. Otherwise, the highest version number is determined, increased, and concatenated with the
     * snapshot suffix from the configuration.
     * @param releaseTagOnDirty whether a release tag should be set if the repo is dirty (i.e. has modified files)
     * @return a version string
     */
    String getCurrentVersion(boolean releaseTagOnDirty) {
        def version = null
        if (plugin.scmOperations!=null) {
            if (releaseTagOnDirty || !plugin.scmOperations.isRepoDirty()) {
                version = headVersion
            }
            if (version==null) {
                def versions = sortedVersions
                if (versions.size()>0) {
                    def lastVersion = versions.last()
                    version = "${project.scmversion.incVersion(lastVersion)}${project.scmversion.snapshotSuffix}"
                } else {
                    version = "0${project.scmversion.snapshotSuffix}"
                }
            }
        }
        return version
    }
}
