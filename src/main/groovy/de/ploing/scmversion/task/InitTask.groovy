package de.ploing.scmversion.task

import org.gradle.api.tasks.TaskAction


/**
 * A dummy task for initializing the scm operations.
 *
 * It is not necessary to call this in actual operations, the scm operations
 * are initialized on first use. For testing purposes only.
 *
 * @author Stefan Schlott
 */
class InitTask  extends SCMVersionTask  {
    @TaskAction
    def scmInit() {
        plugin.scmOperations
    }
}
