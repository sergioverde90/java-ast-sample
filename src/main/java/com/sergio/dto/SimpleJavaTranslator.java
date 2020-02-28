package com.sergio.dto;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
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
            final com.sun.tools.javac.util.List<JCTree> setters = createSetters(context, clazz);
            
            clazz.defs = clazz.defs
                    .appendList(getters)
                    .appendList(setters);
            result = clazz;

            System.out.println("result = " + result);
        }

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
                    final Name methodName = names.fromString("get" + capitalize(name));
                    final JCTree.JCExpression memberName = maker.Ident(name);
                    final Tree type = varTree.getType();
                    final JCTree.JCBlock block = maker.Block(0, List.of(maker.Return(memberName)));
                    return maker.MethodDef(
                            maker.Modifiers(Flags.PUBLIC),
                            methodName,
                            calculateReturnType(type),
                            List.nil(), // generic type parameters
                            List.nil(), // parameter list
                            List.nil(), // throws clause
                            block,
                            null
                    ).getTree();
                }).collect(Collectors.toList()));
    }

    /**
     * public void set<MemberName>(<MemberType> <MemberName>) {
     *   this.<member> = <MemberName>;
     * }
     */
    private com.sun.tools.javac.util.List<JCTree> createSetters(Context context, ClassTree node) {

        final Names names = Names.instance(context);
        final TreeMaker maker = TreeMaker.instance(context);
        final Symtab symb = Symtab.instance(context);
        final Types types = Types.instance(context);

        return com.sun.tools.javac.util.List.from(node.getMembers().stream()
                .filter(m -> m.getKind() == Tree.Kind.VARIABLE)
                .map(m -> {
                    final VariableTree varTree = (VariableTree) m;
                    final Name methodName = names.fromString("set" + capitalize(names.fromString(varTree.getName().toString())));
                    final Name name = names.fromString(varTree.getName().toString());
                    final JCTree.JCExpression memberName = maker.Ident(name);

                    final String capitalizedParameterName = "a" + capitalize(varTree.getName().toString());
                    final Name parameterName = names.fromString(capitalizedParameterName);

                    final JCTree.JCVariableDecl param = calculateParam(parameterName, maker, varTree, symb);
                    final JCTree.JCAssign assign = maker.Assign(memberName, maker.Ident(param.getName()));
                    final JCTree.JCExpressionStatement exec = maker.Exec(assign);
                    final JCTree.JCBlock block = maker.Block(0, List.of(exec));
                    
                    return maker.MethodDef(
                            maker.Modifiers(Flags.PUBLIC),
                            methodName,
                            maker.TypeIdent(TypeTag.VOID),
                            List.nil(),     // generic type parameters
                            List.of(param), // parameter list
                            List.nil(),     // throws clause
                            block,          // method body
                            null    // default methods (for interface declaration)    
                    ).getTree();
                }).collect(Collectors.toList()));
    }

    private JCTree.JCVariableDecl calculateParam(Name parameterName, TreeMaker maker, VariableTree varTree, Symtab syms) {
        if (varTree.getType().getClass().equals(JCTree.JCIdent.class)) {
            JCTree.JCIdent type = (JCTree.JCIdent) varTree.getType();
            return maker.at(type.pos).Param(parameterName, type.type, null);
        } else if (varTree.getType().getClass().equals(JCTree.JCPrimitiveTypeTree.class)) {
            JCTree.JCPrimitiveTypeTree type = (JCTree.JCPrimitiveTypeTree) varTree.getType();
            return maker.at(type.pos).Param(parameterName, type.type, syms.noSymbol);
        } else if (varTree.getType().getClass().equals(JCTree.JCArrayTypeTree.class)) {
            JCTree.JCArrayTypeTree type = (JCTree.JCArrayTypeTree) varTree.getType();
            return maker.at(type.pos).Param(parameterName, type.type, syms.noSymbol);
        } else {
            throw new IllegalStateException("type not found = " + varTree.getType().getClass());
        }
    }

    private static String capitalize(CharSequence name) {
        return (char) (name.charAt(0) ^ ' ') + name.toString().substring(1);
    }

    private JCTree.JCExpression calculateReturnType(Tree type) {
        if (type.getClass().equals(JCTree.JCPrimitiveTypeTree.class)) {
            return (JCTree.JCPrimitiveTypeTree) type;
        } else if (JCTree.JCIdent.class.equals(type.getClass())){
            return (JCTree.JCIdent) type;
        } else if (JCTree.JCArrayTypeTree.class.equals(type.getClass())) {
            return (JCTree.JCArrayTypeTree) type;
        } else {
            throw new IllegalStateException("type not detected = " + type.getClass());
        }
    }

    private boolean isClassAnnotated(ClassTree node) {
        return node.getModifiers().getAnnotations().stream()
                .anyMatch(a -> a.getAnnotationType().toString().equals(DTO.class.getSimpleName()));
    }

}
