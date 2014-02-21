package de.ploing.scmversion

import org.gradle.api.tasks.TaskAction

/**
 * @author Stefan Schlott
 */
class CreateVersionFileTask extends SCMVersionTask {
    @TaskAction
    def createVersionFile() {
        if (project.scmversion.propertyFilename!=null) {
            Properties props = new Properties()
            props.setProperty('rev', scmOperations.headVersion)
            props.setProperty('dirty', scmOperations.isRepoDirty().toString())
            props.setProperty('version', getCurrentVersion(false))
            File outFile = new File(project.buildDir, 'resources/main/' + project.scmversion.propertyFilename)
            outFile.parentFile.mkdirs()
            def out = new FileOutputStream(outFile)
            props.store(out, 'SCM version information')
            out.close()
            logger.info("${outFile} created")
        }
    }
}
