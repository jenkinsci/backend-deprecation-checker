package org.jenkinsci.backend.depscan.checkers;

import hudson.model.Hudson;
import hudson.util.VersionNumber;
import org.jenkinsci.backend.depscan.Reporter;
import org.kohsuke.asm3.commons.EmptyVisitor;
import org.objectweb.asm.Type;

/**
 * @author Kohsuke Kawaguchi
 */
public class JenkinsGetItemByFullNameChecker extends EmptyVisitor {
    private final Reporter reporter;

    public JenkinsGetItemByFullNameChecker(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (name.equals("getItemByFullName") && (owner.equals("hudson/model/Hudson") || owner.equals("jenkins/model/Jenkins"))) {
            reporter.problem(V1_406,"Found suspicious Jenkins.getItemByFullName()");
        }
    }

    private static final VersionNumber V1_406 = new VersionNumber("1.406");
}
