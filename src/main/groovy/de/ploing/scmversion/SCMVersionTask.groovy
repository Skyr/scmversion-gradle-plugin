package de.ploing.scmversion

import org.gradle.api.DefaultTask

/**
 * @author Stefan Schlott
 */
abstract class SCMVersionTask extends DefaultTask {
    final SCMOperations scmOperations = SCMVersionPlugin.scmOperations

    String extractVersion(String tag) {
        return null
    }
}
