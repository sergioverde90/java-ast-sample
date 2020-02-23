package com.sergio.dto;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.stream.Collectors;

public class SimpleJavaTranslator extends TreeTranslator {

    private final Context context;

    public SimpleJavaTranslator(Context context) {
        this.context = context;
    }

    @Override
    public <T extends JCTree> T translate(T t) {
        return super.translate(t);
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl clazz) {

        super.visitClassDef(clazz);

        final boolean isAnnotated = isClassAnnotated(clazz);

        if (isAnnotated) {
            final com.sun.tools.javac.util.List<JCTree> getters = createGetters(context, clazz);
            clazz.defs = clazz.defs.appendList(getters);
            result = clazz;
        }

    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        super.visitMethodDef(jcMethodDecl);
    }

    /**
     * public <type> get<MemberName>() {
     *  return <member>;
     * }
     */
    private com.sun.tools.javac.util.List<JCTree> createGetters(Context context, ClassTree node) {
        final Names names = Names.instance(context);
        final TreeMaker maker = TreeMaker.instance(context);

        return com.sun.tools.javac.util.List.from(node.getMembers().stream()
                .filter(m -> m.getKind() == Tree.Kind.VARIABLE)
                .map(m -> {
                    final VariableTree varTree = (VariableTree) m;
                    final Name name = names.fromString(varTree.getName().toString());
                    final Name methodName = names.fromString("get" + name);
                    final JCTree.JCExpression memberName = maker.Ident(name);
                    final JCTree.JCExpression returnType = maker.Ident(names.fromString(varTree.getType().toString()));
                    final JCTree.JCBlock block = maker.Block(0, List.of(maker.Return(memberName)));
                    return maker.MethodDef(
                            maker.Modifiers(Flags.PUBLIC),
                            methodName,
                            returnType,
                            List.nil(),
                            List.nil(),
                            List.nil(),
                            block,
                            null
                    ).getTree();
                }).collect(Collectors.toList()));
    }

    private boolean isClassAnnotated(ClassTree node) {
        return node.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().equals(DTO.class.getSimpleName()));
    }

}
