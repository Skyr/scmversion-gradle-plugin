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

import de.ploing.scmversion.git.GitOperations
import spock.lang.Specification

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author Stefan Schlott
 */
class GitOperationsSpec extends Specification {
    static File testRepoDir

    def setupSpec() {
        testRepoDir = new File('./build/testrepos').absoluteFile
        if (!testRepoDir.exists()) {
            testRepoDir.mkdirs()
        }
        ['emptyrepo', 'linearrepo', 'mergedrepo', 'jgittaggedrepo'].each { name ->
            Tools.extractZip(getClass().classLoader.getResourceAsStream("${name}.zip"), testRepoDir)
        }
    }

    def "GitOperations don't work on empty repo"() {
        setup:
        when:
            new GitOperations(new File(testRepoDir, 'emptyrepo'))
        then:
            thrown(IllegalArgumentException)
    }

    def "Test repo is clean"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'linearrepo'))
        when:
            def isDirty = ops.isRepoDirty()
        then:
            isDirty==false
    }

    def "Creating a file renders repo dirty"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'linearrepo'))
            File tmpFile = new File(testRepoDir, 'linearrepo/test.tmp')
        when:
            tmpFile.createNewFile()
            def isDirty = ops.isRepoDirty()
            tmpFile.delete()
        then:
            isDirty==true
    }

    def "Tags gives all tags in linear history"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'linearrepo'))
        when:
            def head = ops.headVersion
            def tags = ops.tags
        then:
            head!=null
            tags.size()==4
    }

    def "HeadTags gives tags of head in linear history"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'linearrepo'))
        when:
            def head = ops.headVersion
            def tags = ops.headTags
        then:
            head!=null
            tags.size()==1
    }

    def "Tags gives all tags in branched history"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'mergedrepo'))
        when:
            def head = ops.headVersion
            def tags = ops.tags
        then:
            head!=null
            tags.size()==4
    }

    def "Head tag detected even if tagged by eclipse"() {
        setup:
            SCMOperations ops = new GitOperations(new File(testRepoDir, 'jgittaggedrepo'))
        when:
            def head = ops.headVersion
            def tags = ops.headTags
        then:
            head!=null
            tags.size()==1
    }
}
