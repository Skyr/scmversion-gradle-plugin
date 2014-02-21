package de.ploing.scmversion

/**
 * @author Stefan Schlott
 */
class SCMVersionPluginExtension {
    String releaseTagPattern = 'version-([0-9.]*)'
    String snapshotSuffix = '-SNAPSHOT'
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
