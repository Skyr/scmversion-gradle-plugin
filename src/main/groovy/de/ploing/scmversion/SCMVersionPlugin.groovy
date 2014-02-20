package de.ploing.scmversion

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Stefan Schlott
 */
abstract class SCMVersionPlugin implements Plugin<Project> {
    def logger

    @Override
    void apply(Project project) {
        logger = project.logger
        // Register extension first - avoid gradle errors in case of initialization problems
        project.extensions.create("scmversion", SCMVersionPluginExtension)
        // If plugin successfully initialized: Setup tasks
        if (setupSCM(project)) {
            setupSetVersionTask()
            setupCreateVersionFileTask()
        }
    }

    void setupSetVersionTask() {

    }

    void setupCreateVersionFileTask() {

    }

    /**
     * Set up the actual version plugin implementation (initialize variables, check repo, etc.)
     * @param project
     * @return true if everything is fine and operational, false otherwise
     */
    abstract boolean setupSCM(Project project)

    /**
     * Determines if a repo is dirty, i.e. there are uncommitted changes
     * @return true if there are uncommitted changes
     */
    abstract boolean isRepoDirty()

    /**
     * Determines the version string of the current head
     * @return the version id of the current head
     */
    abstract String getHeadVersion()

    /**
     * Gets all tags attached to the current head
     * @return a set of tags (may be empty, is never null)
     */
    abstract Set<String> getHeadTags()

    /**
     * Get all tags of the repository
     * @return a set of all tags (may be empty, is never null)
     */
    abstract Set<String> getTags()
}
