/*
 * Copyright 2009-2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenkinsci.backend.depscan.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.asm3.AnnotationVisitor;

public class TeeAnnotationVisitor implements AnnotationVisitor {
    private final List<AnnotationVisitor> visitors = new ArrayList<AnnotationVisitor>();

    public TeeAnnotationVisitor(AnnotationVisitor... visitors) {
        Collections.addAll(this.visitors, visitors);
    }

    public void addVisitor(AnnotationVisitor visitor) {
        visitors.add(visitor);
    }

    public static AnnotationVisitor mergeVisitors(AnnotationVisitor current, AnnotationVisitor visitor) {
        if (visitor != null) {
            if (current == null) {
                return visitor;
            } else if (current instanceof TeeAnnotationVisitor) {
                ((TeeAnnotationVisitor)current).addVisitor(visitor);
                return current;
            } else {
                TeeAnnotationVisitor tee = new TeeAnnotationVisitor(current);
                tee.addVisitor(visitor);
                return tee;
            }
        } else {
            return current;
        }
    }

    public void visit(String name, Object value) {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visit(name, value);
        }
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor result = null;
        for (AnnotationVisitor visitor : visitors) {
            result = mergeVisitors(result, visitor.visitAnnotation(name, desc));
        }
        return result;
    }

    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor result = null;
        for (AnnotationVisitor visitor : visitors) {
            result = mergeVisitors(result, visitor.visitArray(name));
        }
        return result;
    }

    public void visitEnum(String name, String desc, String value) {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visitEnum(name, desc, value);
        }
    }

    public void visitEnd() {
        for (AnnotationVisitor visitor : visitors) {
            visitor.visitEnd();
        }
    }
}