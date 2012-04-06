package org.jenkinsci.backend.depscan;

import hudson.util.VersionNumber;
import org.jvnet.hudson.update_center.DefaultMavenRepositoryBuilder;
import org.jvnet.hudson.update_center.HPI;
import org.jvnet.hudson.update_center.MavenRepositoryImpl;
import org.jvnet.hudson.update_center.PluginHistory;
import org.kohsuke.asm3.ClassReader;
import org.kohsuke.asm3.Label;
import org.kohsuke.asm3.MethodAdapter;
import org.kohsuke.asm3.MethodVisitor;
import org.kohsuke.asm3.Opcodes;
import org.kohsuke.asm3.commons.EmptyVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class App {
    public static void main(String[] args) throws Exception {
        new App().run();
    }

    public void run() throws Exception {
        MavenRepositoryImpl repo = DefaultMavenRepositoryBuilder.createStandardInstance();

        Collection<PluginHistory> plugins = repo.listHudsonPlugins();
        for (PluginHistory p : new ArrayList<PluginHistory>(plugins)) {
            System.out.println("Checking "+p.artifactId+ " v."+p.latest().version);
            scan(p.latest());
        }
    }

    public void scan(HPI hpi) throws IOException {
        final VersionNumber requiredCore = new VersionNumber(hpi.getRequiredJenkinsVersion());
        File archive = hpi.resolve();

        JarFile jar = new JarFile(archive);
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            final JarEntry je =  e.nextElement();
            if (je.getName().endsWith(".class")) {
                ClassReader cr = new ClassReader(jar.getInputStream(je));

                class Position {
                    int lineNumber;
                    String sourceFileName;
                    String methodName;
                    String methodDescriptor;
                    boolean isDeprecated;

                    @Override
                    public String toString() {
                        String msg = String.format("in %s at %s:%d", methodName + methodDescriptor, sourceFileName, lineNumber);
                        if (isDeprecated)   msg += " (deprecated method)";
                        return msg;
                    }
                }

                final Position pos = new Position();

                Reporter reporter = new Reporter() {
                    public void problem(VersionNumber version, String msg) {
                        System.out.printf("  %s%s\n    %s\n",
                                msg, version.compareTo(requiredCore)>=0 ? "(for "+version+" but built against "+requiredCore+")" : "",
                                pos);
                    }
                };
                final MethodVisitor checker = new Items_fromNameListChecker(reporter);
                cr.accept(new EmptyVisitor() {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        pos.isDeprecated = (access&Opcodes.ACC_DEPRECATED)!=0;
                        pos.methodName = name;
                        pos.methodDescriptor = desc;
                        return new MethodAdapter(checker) {
                            @Override
                            public void visitLineNumber(int line, Label start) {
                                pos.lineNumber = line;
                            }
                        };
                    }

                    @Override
                    public void visitSource(String source, String debug) {
                        pos.sourceFileName = source; // just the short name, without path
                    }

                }, 0);
            }
        }
        jar.close();
    }

}
