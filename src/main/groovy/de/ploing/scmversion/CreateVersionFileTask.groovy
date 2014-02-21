package de.ploing.scmversion

import org.gradle.api.tasks.TaskAction

/**
 * @author Stefan Schlott
 */
class CreateVersionFileTask extends SCMVersionTask {
    @TaskAction
    def createVersionFile() {
        println('Creating version file...')
    }
}
