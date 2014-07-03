package de.ploing.scmversion.git

import de.ploing.scmversion.SCMDetector
import de.ploing.scmversion.SCMOperations


class GitDetector implements SCMDetector {
    @Override
    String getName() {
        return 'git'
    }

    @Override
    boolean isInUse(File baseDir) {
        def gitDir = new File("${baseDir.absolutePath}${File.separator}.git")
        return gitDir.exists() && gitDir.isDirectory()
    }

    @Override
    SCMOperations getOperations(File baseDir) {
        return new GitVersionPlugin(baseDir)
    }
}
