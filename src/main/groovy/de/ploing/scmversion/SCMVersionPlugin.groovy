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

import de.ploing.scmversion.git.GitDetector
import de.ploing.scmversion.task.CreateVersionFileTask
import de.ploing.scmversion.task.InitTask
import de.ploing.scmversion.task.SetVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Stefan Schlott
 */
class SCMVersionPlugin implements Plugin<Project> {
    def project
    def logger
    private boolean scmOpsInitialized = false
    private SCMOperations scmOps = null
    private List<SCMDetector> scmPlugins = [ ]

    @Override
    void apply(Project project) {
        this.project = project
        logger = project.logger
        // Register extension first - avoid gradle errors in case of initialization problems
        project.extensions.create('scmversion', SCMVersionPluginExtension)
        // Initialize scm detectors
        scmPlugins.add(new GitDetector(project.scmversion))
        // Setup tasks
        Task initTask = project.task('scmInit', type: InitTask, {
            plugin = this
        })
        Task setVersionTask = project.task('setVersion', type: SetVersionTask, {
            plugin = this
        })
        Task createVersionFileTask = project.task('createVersionFile', type: CreateVersionFileTask, {
            plugin = this
        })
        project.version = new VersionProxy(setVersionTask)
        autoconfigSetVersionTask(setVersionTask)
        autoconfigCreateVersionFileTask(createVersionFileTask)
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
     * Set up the scm operations implementation. Do autodetection if not configured
     * @param project
     * @return null if the initialization failed for some reason, proper instance otherwise
     */
    SCMOperations getScmOperations() {
        if (scmOpsInitialized) {
            return scmOps
        }

        try {
            def detector
            if (project.scmversion.scmSystem==null) {
                // Do autodetection
                detector = scmPlugins.find { d ->
                    d.isInUse(project.projectDir)
                }
                if (detector==null) {
                    throw new RuntimeException('No scm detected')
                }
            } else {
                // Find selected scm
                detector = scmPlugins.find { d ->
                    d.name==project.scmversion.scmSystem
                }
                if (detector==null) {
                    throw new RuntimeException("Implementation for scm ${project.scmversion.scmSystem} not found")
                }
                if (!detector.isInUse(project.projectDir)) {
                    throw new RuntimeException("scm ${project.scmversion.scmSystem} selected, but not in use")
                }
            }
            project.scmversion.scmSystem = detector.name
            scmOps = detector.getOperations(project.projectDir)
        } catch (e) {
            project.scmversion.scmSystem = null
            scmOps = null
            logger.warn("Unable to initialize scm version plugin: " + e.message)
        }
        scmOpsInitialized = true
        return scmOps
    }
}
