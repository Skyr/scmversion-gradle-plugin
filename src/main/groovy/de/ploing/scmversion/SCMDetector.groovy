package de.ploing.scmversion

/**
 * @author Stefan Schlott
 */
public interface SCMDetector {
    /**
     * The name of the scm system. Used for statically selecting the scm to be used.
     *
     * @return the scm name
     */
    abstract public String getName();

    /**
     * Checks if the project uses this scm. Used for scm autodetection.
     *
     * @param baseDir the base directory of the project
     * @return true, if the scm is applicable
     */
    abstract public boolean isInUse(File baseDir);

    /**
     * Instantiates the actual instance doing the scm operations.
     *
     * @param baseDir the base directory of the project
     * @return
     */
    abstract public SCMOperations getOperations(File baseDir);
}