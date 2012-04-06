package org.jenkinsci.backend.depscan;

import org.kohsuke.asm3.Type;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kohsuke Kawaguchi
 */
public class MethodMatcher {
    private final Method method;

    private final Set<String> owners = new HashSet<String>();
    private final String methodName,descriptor;

    public MethodMatcher(Method method) {
        this.method = method;

        addOwner(method.getDeclaringClass());
        methodName = method.getName();
        descriptor = Type.getMethodDescriptor(method);
    }

    public MethodMatcher addOwner(Class c) {
        owners.add(Type.getInternalName(c));
        return this;
    }

    public boolean matches(String owner, String name, String desc) {
        return this.owners.contains(owner) && this.methodName.equals(name) && this.descriptor.equals(desc);
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
