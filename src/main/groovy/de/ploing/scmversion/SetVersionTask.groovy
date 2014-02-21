package de.ploing.scmversion

import org.gradle.api.tasks.TaskAction

/**
 * @author Stefan Schlott
 */
class SetVersionTask extends SCMVersionTask {
    @TaskAction
    def setVersion() {
        println('Setting version...')
    }
}
