package org.jenkinsci.backend.depscan;

import hudson.util.VersionNumber;

/**
 * @author Kohsuke Kawaguchi
 */
public interface Reporter {
    /**
     * Reports a problem.
     *
     * @param version
     *      Applicable to this version and later
     * @param msg
     *      Human readable message that points to the issue.
     */
    void problem(VersionNumber version, String msg);
}
