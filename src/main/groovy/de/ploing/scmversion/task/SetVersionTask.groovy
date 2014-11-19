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
package de.ploing.scmversion.task

import org.gradle.api.tasks.TaskAction

/**
 * @author Stefan Schlott
 */
class SetVersionTask extends SCMVersionTask {
    @TaskAction
    def setVersion() {
        if (plugin.scmOperations!=null) {
            if (plugin.scmOperations.isRepoDirty()) {
                logger.info('Repo is dirty, not checking for release tag')
            }
            def version = getCurrentVersion(false)
            logger.info("Repo version is ${version}")
            project.version = version
        }
    }
}
