package de.ploing.scmversion

import org.gradle.api.tasks.TaskAction

/**
 * @author Stefan Schlott
 */
class SetVersionTask extends SCMVersionTask {
    @TaskAction
    def setVersion() {
        if (scmOperations.isRepoDirty()) {
            logger.info('Repo is dirty, not checking for release tag')
        }
        def version = getCurrentVersion(false)
        logger.info("Repo version is ${version}")
        project.version = version
    }
}
