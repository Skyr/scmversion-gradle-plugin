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

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Stefan Schlott
 */
class GitVersionTaskSpec extends Specification {
    static File testRepoDir

    def setupSpec() {
        testRepoDir = new File('./build/testrepos').absoluteFile
        if (!testRepoDir.exists()) {
            testRepoDir.mkdirs()
        }
        ['norepo', 'linearrepo', 'snapshotrepo'].each { name ->
            Tools.extractZip(getClass().classLoader.getResourceAsStream("${name}.zip"), testRepoDir)
        }
    }

    def "Missing scm is handled gracefully"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'norepo')).build()
        when:
            project.apply plugin: SCMVersionPlugin
            project.tasks.scmInit.scmInit()
            project.tasks.setVersion.setVersion()
        then:
            project.scmversion.scmSystem == null
    }

    def "Versions are sorted correctly"() {
        setup:
            def v1 = [1,3,5,2]
            def v2 = [1,3,6,0]
            def v3 = [1,4,1]
            def v4 = [1,4,1,0]
        when:
            def compV1V2 = SCMVersionPluginExtension.compareVersions(v1, v2)
            def compV2V1 = SCMVersionPluginExtension.compareVersions(v2, v1)
            def compV2V3 = SCMVersionPluginExtension.compareVersions(v2, v3)
            def compV3V2 = SCMVersionPluginExtension.compareVersions(v3, v2)
            def compV3V4 = SCMVersionPluginExtension.compareVersions(v3, v4)
        then:
            compV1V2 < 0
            compV2V1 > 0
            compV2V3 < 0
            compV3V2 > 0
            compV3V4 == 0
    }

    def "Version of repo on revision tag is set correctly"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'linearrepo')).build()
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
            }
        when:
            project.tasks.setVersion.setVersion()
        then:
            project.version=='1.0'
    }

    def "scm autodetection works properly"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'snapshotrepo')).build()
        when:
            project.apply plugin: SCMVersionPlugin
            project.tasks.scmInit.scmInit()
        then:
            project.scmversion.scmSystem == 'git'
    }

    def "git plugin is properly initialized"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'snapshotrepo')).build()
        when:
            project.apply plugin: SCMVersionPlugin
            project.scmversion.scmSystem = 'git'
            project.tasks.scmInit.scmInit()
        then:
            project.scmversion.scmSystem == 'git'
    }

    def "Unknown scm erases scmSystem info"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'snapshotrepo')).build()
        when:
            project.apply plugin: SCMVersionPlugin
            project.scmversion.scmSystem = 'doesnotexist'
            project.tasks.scmInit.scmInit()
        then:
            project.scmversion.scmSystem == null
    }

    def "Snapshot version is set correctly"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'snapshotrepo')).build()
        when:
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
            }
            project.tasks.setVersion.setVersion()
        then:
            project.version=='1.1-SNAPSHOT'
    }

    def "Property file of Snapshot version is written correctly"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'snapshotrepo')).build()
            File propFile = new File(testRepoDir, 'snapshotrepo/build/resources/main/scminfo.properties')
        when:
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
                propertyFilename = propFile.name
            }
            project.tasks.createVersionFile.createVersionFile()
        then:
            propFile.exists()
            Properties props = new Properties()
            def inStream = new FileInputStream(propFile)
            props.load(inStream)
            inStream.close()
            props.getProperty('version')=='1.1-SNAPSHOT'
            props.getProperty('dirty')=='false'
    }

    def "scm in parent directory is not discovered"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'linearrepo/src')).build()
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
                scmRootSearchDepth = 0
            }
        when:
            project.tasks.setVersion.setVersion()
        then:
            project.scmversion.scmSystem == null
    }

    def "scm in parent directory is discovered if search is enabled"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'linearrepo/src')).build()
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
                scmRootSearchDepth = 1
            }
        when:
            project.tasks.setVersion.setVersion()
        then:
            project.scmversion.scmSystem == 'git'
            project.version == '1.0'
    }

    def "Version of dependency can be read via proxy class"() {
        setup:
            Project project = ProjectBuilder.builder().withProjectDir(new File(testRepoDir, 'linearrepo')).build()
            project.apply plugin: SCMVersionPlugin
            project.scmversion {
                releaseTagPattern = 'rev-([0-9.]*)'
            }
        when:
            def someName = "foo:bar:$project.version"
            project.tasks.setVersion.setVersion()
        then:
            project.version=='1.0'
            someName.endsWith('1.0')
    }
}
