package de.ploing.scmversion


/**
 * @author Stefan Schlott
 */
public interface SCMOperations {
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