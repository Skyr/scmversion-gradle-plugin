package de.ploing.scmversion.git

import de.ploing.scmversion.SCMOperations
import de.ploing.scmversion.SCMVersionPlugin
import org.gradle.api.Project

/**
 * @author Stefan Schlott
 */
class GitVersionPlugin extends SCMVersionPlugin {
    @Override
    SCMOperations setupSCM(Project project) {
        try {
            return new GitOperations(project.projectDir)
        } catch (IllegalArgumentException e) {
            logger.warn('No git repository found. git version plugin not initialized.')
            return null
        }
    }
}
