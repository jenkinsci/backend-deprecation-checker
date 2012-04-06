package org.jenkinsci.backend.depscan;

import hudson.model.Items;
import hudson.util.VersionNumber;
import org.kohsuke.asm3.Type;
import org.kohsuke.asm3.commons.EmptyVisitor;

/**
 * @author Kohsuke Kawaguchi
 */
public class Items_fromNameListChecker extends EmptyVisitor {
    private final Reporter reporter;

    public Items_fromNameListChecker(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (owner.equals(ITEMS_CLASS) &&  name.equals("fromNameList") && desc.equals(METHOD_DESCRIPTOR)) {
            reporter.problem(V1_406,"Found Item.fromNameList(String,Class). Replace with fromNameList(ItemGroup,String,Class,)");
        }
    }

    private static final VersionNumber V1_406 = new VersionNumber("1.406");
    private static final String ITEMS_CLASS = Type.getInternalName(Items.class);
    private static final String METHOD_DESCRIPTOR;
    static {
        try {
            METHOD_DESCRIPTOR = Type.getMethodDescriptor(Items.class.getMethod("fromNameList",String.class,Class.class));
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
