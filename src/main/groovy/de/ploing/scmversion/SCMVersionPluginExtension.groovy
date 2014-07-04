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
class SCMVersionPluginExtension {
    String releaseTagPattern = 'version-([0-9.]*)'
    String snapshotSuffix = '-SNAPSHOT'
    String scmSystem
    String propertyFilename
    Closure versionComparator = { a,b ->
        return compareVersions(
            a.split('\\.').collect { Integer.parseInt(it) },
            b.split('\\.').collect { Integer.parseInt(it) })
    }
    Closure incVersion = { versionString ->
        def versions = versionString.split('\\.').collect { Integer.parseInt(it) }
        versions[-1] += 1
        return versions.join('.')
    }

    static def versionHeadTail(List<Integer> ver) {
        if (ver.size()>0) {
            return [ver.head(), ver.tail()]
        } else {
            return [0, []]
        }
    }

    static int compareVersions(List<Integer> a, List<Integer> b) {
        if (a.size()==0 && b.size()==0) {
            return 0
        }
        def (aHead, aTail) = versionHeadTail(a)
        def (bHead, bTail) = versionHeadTail(b)
        if (aHead<bHead) {
            return -1
        } else if (aHead>bHead) {
            return 1
        } else {
            return compareVersions(aTail, bTail)
        }
    }
}
