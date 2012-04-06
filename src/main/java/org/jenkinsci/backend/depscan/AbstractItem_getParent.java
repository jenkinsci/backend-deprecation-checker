package org.jenkinsci.backend.depscan;

import hudson.model.Hudson;
import hudson.util.VersionNumber;
import org.kohsuke.asm3.commons.EmptyVisitor;
import org.objectweb.asm.Type;

/**
 * Hudson Item.getParent() is suspicious. This method is defined on number of Item subtypes, so we don't check owner.
 * At this point, anything that calls "Hudson" is suspicious anyway :-)
 *
 * See 072556e8 in the core.
 *
 * @author Kohsuke Kawaguchi
 */
public class AbstractItem_getParent extends EmptyVisitor {
    private final Reporter reporter;

    public AbstractItem_getParent(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (name.equals("getParent") && desc.equals(METHOD_DESCRIPTOR)) {
            reporter.problem(V1_395,"Found suspicious getParent() call that returns Hudson");
        }
    }

    private static final VersionNumber V1_395 = new VersionNumber("1.395");
    private static final String METHOD_DESCRIPTOR = Type.getMethodDescriptor(Type.getType(Hudson.class), new Type[0]);
}
