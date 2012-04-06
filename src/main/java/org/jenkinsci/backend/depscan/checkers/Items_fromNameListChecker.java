package org.jenkinsci.backend.depscan.checkers;

import hudson.model.Hudson;
import hudson.model.Items;
import hudson.util.VersionNumber;
import jenkins.model.Jenkins;
import org.jenkinsci.backend.depscan.MethodMatcher;
import org.jenkinsci.backend.depscan.Reporter;
import org.kohsuke.asm3.commons.EmptyVisitor;

/**
 * Check context-less version.
 *
 * @author Kohsuke Kawaguchi
 */
public class Items_fromNameListChecker extends EmptyVisitor {
    private final Reporter reporter;

    public Items_fromNameListChecker(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (item_fromNameList.matches(owner,name,desc)) {
            reporter.problem(V1_406,"Found Item.fromNameList(String,Class). Replace with fromNameList(ItemGroup,String,Class,)");
        }

        if (jenkins_getItem1.matches(owner,name,desc)) {
            reporter.problem(V1_406,"Found suspicious Jenkins.getItem(String). Replace with Jenkins.getItem(String,context)");
        }

//        if (jenkins_getItem2.matches(owner,name,desc)) {
//            reporter.problem(V1_406,"Found Jenkins.getItem(String,Class). Replace with Jenkins.getItem(String,context,Class)");
//        }
    }

    private static final VersionNumber V1_406 = new VersionNumber("1.406");
    private static final MethodMatcher jenkins_getItem1;
    private static final MethodMatcher item_fromNameList;
    static {
        try {
            item_fromNameList = new MethodMatcher(Items.class.getMethod("fromNameList",String.class,Class.class));
            jenkins_getItem1 = new MethodMatcher(Jenkins.class.getMethod("getItem",String.class)).addOwner(Hudson.class);
//            jenkins_getItem2 = new MethodMatcher(Jenkins.class.getMethod("getItem",String.class,Class.class)).addOwner(Hudson.class);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }
}
