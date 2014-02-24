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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Stefan Schlott
 */
abstract class SCMVersionPlugin implements Plugin<Project> {
    def logger
    static SCMOperations scmOperations = null

    @Override
    void apply(Project project) {
        logger = project.logger
        // Register extension first - avoid gradle errors in case of initialization problems
        project.extensions.create('scmversion', SCMVersionPluginExtension)
        try {
            scmOperations = setupSCM(project)
        } catch (e) {
            logger.warn("Unable to initialize scm version plugin: " + e.message)
        }
        // If plugin successfully initialized: Setup tasks
        if (scmOperations!=null) {
            Task setVersionTask = project.task('setVersion', type: SetVersionTask)
            Task createVersionFileTask = project.task('createVersionFile', type: CreateVersionFileTask)
            autoconfigSetVersionTask(setVersionTask)
            autoconfigCreateVersionFileTask(createVersionFileTask)
        }
    }

    void autoconfigSetVersionTask(Task setVersionTask) {
        setVersionTask.project.gradle.afterProject {
            setVersionTask.setVersion()
        }
    }

    void autoconfigCreateVersionFileTask(Task createVersionFileTask) {
        createVersionFileTask.project.gradle.afterProject {
            createVersionFileTask.project.getTasksByName('processResources', false).each { task ->
                task.dependsOn createVersionFileTask
            }
        }
    }

    /**
     * Set up the scm operations implementation
     * @param project
     * @return null if the initialization failed for some reason, proper instance otherwise
     */
    abstract SCMOperations setupSCM(Project project)
}
