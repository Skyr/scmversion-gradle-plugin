package de.ploing.scmversion.git

import de.ploing.scmversion.SCMDetector
import de.ploing.scmversion.SCMOperations
import de.ploing.scmversion.SCMVersionPluginExtension
import org.eclipse.jgit.storage.file.FileRepositoryBuilder


class GitDetector implements SCMDetector {
    private SCMVersionPluginExtension params
    GitDetector(SCMVersionPluginExtension params) {
        this.params = params
    }

    @Override
    String getName() {
        return 'git'
    }

    /**
     * Gets the root directory of the repository
     *
     * @param baseDir the directory where to start the search
     * @return the directory, or null if no repo found
     */
    File getGitRootDir(final File baseDir) {
        // Determine uppermost directory for repo search
        /* CeilingDir implementation seems buggy! (problem: start dir = ceiling dir)
        File ceilingDir = baseDir.absoluteFile
        for (int i=0; i<params.scmRootSearchDepth; i++) {
            File parentDir = ceilingDir.getParentFile()
            if (parentDir!=null) {
                ceilingDir = parentDir
            }
        }
        // Find git root
        FileRepositoryBuilder builder = new FileRepositoryBuilder()
        return builder.addCeilingDirectory(ceilingDir).findGitDir(baseDir.absoluteFile).getGitDir()
        */
        File searchDir = baseDir.absoluteFile
        final int steps = params.scmRootSearchDepth>0 ? params.scmRootSearchDepth : 0
        for (int i=0; i<=steps; i++) {
            def gitDir = new File("${searchDir.absolutePath}${File.separator}.git")
            if (gitDir.exists() && gitDir.isDirectory()) {
                return searchDir
            }
            def parentDir = searchDir.getParentFile()
            if (parentDir!=null) {
                searchDir = parentDir
            }
        }
        return null
    }

    @Override
    boolean isInUse(File baseDir) {
        def gitRoot = getGitRootDir(baseDir)
        return gitRoot!=null
    }

    @Override
    SCMOperations getOperations(File baseDir) {
        return new GitOperations(getGitRootDir(baseDir))
    }
}
