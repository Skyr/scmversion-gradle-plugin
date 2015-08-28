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
package de.ploing.scmversion.git

import de.ploing.scmversion.SCMOperations
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**
 * @author Stefan Schlott
 */
class GitOperations implements SCMOperations {
    Repository repository
    Git git

    GitOperations(File baseDir) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        repository = builder.readEnvironment().findGitDir(baseDir.absoluteFile).build()
        ObjectId head = repository.resolve('HEAD')
        if (head==null) {
            throw new IllegalArgumentException('Repository has no HEAD')
        }
        git = new Git(repository)
    }

    @Override
    boolean isRepoDirty() {
        Status status = git.status().call()
        return !status.clean
    }

    @Override
    String getHeadVersion() {
        ObjectId head = repository.resolve('HEAD')
        return head.name
    }

    @Override
    Set<String> getHeadTags() {
        def result = new HashSet<String>()
        def headVersion = getHeadVersion()
        Map<String, Ref> refList = repository.getTags()
        for (Map.Entry<String, Ref> entry: refList.entrySet()) {
            Ref r = entry.value
            if (!r.peeled) {
              r = repository.peel(r)
            }
            if ((r.peeledObjectId != null && headVersion.equalsIgnoreCase(r.peeledObjectId.name)) ||
                headVersion.equalsIgnoreCase(r.objectId.name)) {
                result.add(entry.key)
            }
        }
        return result;
    }

    @Override
    Set<String> getTags() {
        return repository.getTags().keySet()
    }
}
