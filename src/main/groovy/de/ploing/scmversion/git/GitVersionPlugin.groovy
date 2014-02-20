package de.ploing.scmversion.git

import de.ploing.scmversion.SCMVersionPlugin
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.Project

/**
 * @author Stefan Schlott
 */
class GitVersionPlugin extends SCMVersionPlugin {
    Repository repository
    Git git

    @Override
    boolean setupSCM(Project project) {
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        try {
            repository = builder.readEnvironment().findGitDir(new File('.').absoluteFile).build()
        } catch (IllegalArgumentException e) {
            logger.warn('No git repository found. git version plugin not initialized.')
            return false
        }
        git = new Git(repository)
        return true
    }

    @Override
    boolean isRepoDirty() {
        Status status = git.status().call()
        return !status.clean
    }

    @Override
    String getHeadVersion() {
        ObjectId head = repository.resolve("HEAD")
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
