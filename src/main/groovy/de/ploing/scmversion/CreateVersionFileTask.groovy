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
