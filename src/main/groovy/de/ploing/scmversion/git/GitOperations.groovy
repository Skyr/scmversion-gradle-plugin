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

    GitOperations() {
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        repository = builder.readEnvironment().findGitDir(new File('.').absoluteFile).build()
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
            if (headVersion.equalsIgnoreCase(entry.value.objectId.name)) {
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
