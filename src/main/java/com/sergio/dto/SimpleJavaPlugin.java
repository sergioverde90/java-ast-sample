package com.sergio.dto;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

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
                    public Void visitClass(ClassTree node, Void aVoid) {
                        final boolean isAnnotated = isClassAnnotated(node);
                        if (isAnnotated) {
                            final TreeMaker maker = TreeMaker.instance(context);
                            final List<JCTree> getters = createGetters(context, node);
                            JCTree.JCClassDecl clazz = (JCTree.JCClassDecl) node;

                            final com.sun.tools.javac.util.List<JCTree> jcTrees = com.sun.tools.javac.util.List.from(getters);

                            System.out.println("jcTrees = " + jcTrees);

                            JCTree.JCClassDecl assd = maker.ClassDef(
                                    clazz.getModifiers(),
                                    clazz.getSimpleName(),
                                    clazz.getTypeParameters(),
                                    clazz.getExtendsClause(),
                                    clazz.getImplementsClause(),
                                    jcTrees
                            );
                            return super.visitClass(assd, aVoid);
                        }
                        return super.visitClass(node, aVoid);
                    }

                    /**
                     * 
                     * public <type> get<MemberName>() {
                     *     return <member>;
                     * }
                     *
                     */

                    private List<JCTree> createGetters(Context context, ClassTree node) {
                        final Names names = Names.instance(context);
                        final TreeMaker maker = TreeMaker.instance(context);

                        return node.getMembers().stream().map(m -> {
                            final VariableTree varTree = (VariableTree) m;
                            final Name name = names.fromString(varTree.getName().toString());
                            final Name methodName = names.fromString("get" + name);
                            final JCTree.JCExpression memberName = maker.Ident(name);
                            final JCTree.JCExpression returnType = maker.Ident(names.fromString(varTree.getType().toString()));
                            final JCTree.JCBlock block = maker.Block(0, com.sun.tools.javac.util.List.of(maker.Return(memberName)));
                            return maker.MethodDef(
                                    maker.Modifiers(Flags.PUBLIC),
                                    methodName,
                                    returnType,
                                    com.sun.tools.javac.util.List.nil(),
                                    com.sun.tools.javac.util.List.nil(),
                                    com.sun.tools.javac.util.List.nil(),
                                    block,
                                    null
                            ).getTree();
                        }).collect(Collectors.toList());
                    }
                }, null);
                
            }
        });
        
    }

    private boolean isClassAnnotated(ClassTree node) {
        return node.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().equals(DTO.class.getSimpleName()));
    }
    
}
