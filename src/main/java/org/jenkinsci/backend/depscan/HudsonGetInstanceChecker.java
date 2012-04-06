package org.jenkinsci.backend.depscan;

import hudson.model.Hudson;
import org.kohsuke.asm3.MethodVisitor;
import org.kohsuke.asm3.Opcodes;
import org.kohsuke.asm3.commons.EmptyVisitor;

/**
 * Checks for {@link Hudson#getInstance()}
 *
 * @author Kohsuke Kawaguchi
 */
class HudsonGetInstanceChecker extends EmptyVisitor {
    private final Reporter reporter;

    public HudsonGetInstanceChecker(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode== Opcodes.INVOKESTATIC &&  owner.equals("hudson/model/Hudson") &&  name.equals("getInstance")) {
            reporter.problem("Found Hudson.getInstance(). Replace with Jenkins.getInstance()");
        }
    }
}
