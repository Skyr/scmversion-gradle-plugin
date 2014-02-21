package de.ploing.scmversion

import org.gradle.api.DefaultTask

/**
 * @author Stefan Schlott
 */
abstract class SCMVersionTask extends DefaultTask {
    final SCMOperations scmOperations = SCMVersionPlugin.scmOperations

    String extractVersion(String tag) {
        def pattern = ~ project.scmversion.releaseTagPattern
        def m = pattern.matcher(tag)
        if (m.matches()) {
            return m[0][1]
        } else {
            return null
        }
    }

    String getHeadVersion() {
        def version = null
        scmOperations.headTags.each { tag ->
            def tagVersion = extractVersion(tag)
            if (tagVersion!=null) {
                version = tagVersion
            }
        }
        return version
    }

    List<String> getVersions() {
        def result = []
        scmOperations.tags.each { tag ->
            def tagVersion = extractVersion(tag)
            if (tagVersion!=null) {
                result.add(tagVersion)
            }
        }
        return result
    }

    List<String> getSortedVersions() {
        return versions.sort(project.scmversion.versionComparator)
    }

    String getCurrentVersion(boolean releaseTagOnDirty) {
        def version = null
        if (releaseTagOnDirty || !scmOperations.isRepoDirty()) {
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
        return version
    }
}
