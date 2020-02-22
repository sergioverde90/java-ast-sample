package com.sergio.java_plugin;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleJavaPlugin implements Plugin {
    @Override
    public String getName() {
        return "MyCustumPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("task.getClass() = " + task.getClass());
        Context context = ((BasicJavacTask) task).getContext();
        
        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent e) {}

            @Override
            public void finished(TaskEvent e) {
                if (e.getKind() != TaskEvent.Kind.PARSE) return;

                e.getCompilationUnit().accept(new TreeScanner<Void, Void>(){
                    
                    @Override
                    public Void visitMethod(MethodTree node, Void aVoid) {
                        final List<? extends VariableTree> collect = findAnnotatedParameters(node);
                        
                        if (!collect.isEmpty()) {
                            System.out.println("collect = " + collect);
                            collect.forEach(p -> addCheck(p, context));
                        }
                        
                        return super.visitMethod(node, aVoid);
                    }

                    private void addCheck(VariableTree p, Context context) {
                        TreeMaker.instance(context);
                    }

                }, null);
                
            }
        });
        
    }

    private List<? extends VariableTree> findAnnotatedParameters(MethodTree node) {
        return node.getParameters().stream()
                .filter(p -> p.getType().toString().equals(int.class.getName())
                        && p.getModifiers().getAnnotations().stream()
                        .anyMatch(a -> a.getAnnotationType().toString().equals(Positive.class.getSimpleName())))
                .collect(Collectors.toList());
    }
    
}
