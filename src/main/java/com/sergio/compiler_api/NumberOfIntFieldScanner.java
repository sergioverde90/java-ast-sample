package com.sergio.compiler_api;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

public class NumberOfIntFieldScanner extends TreePathScanner<Object, Trees> {
    
    private int importsNumber = 0;
    
    @Override
    public Object visitClass(ClassTree node, Trees trees) {
        //importsNumber++;
        return super.visitClass(node, trees);
    }

    @Override
    public Object visitIf(final IfTree node, Trees trees) {
        importsNumber++;
        System.out.println("node = " + node.getThenStatement());
        return super.visitIf(new IfTree() {
            @Override
            public ExpressionTree getCondition() {
                return node.getCondition();
            }

            @Override
            public StatementTree getThenStatement() {
                return node.getThenStatement();
            }

            @Override
            public StatementTree getElseStatement() {
                return node.getElseStatement();
            }

            @Override
            public Kind getKind() {
                return node.getKind();
            }

            @Override
            public <R, D> R accept(TreeVisitor<R, D> visitor, D data) {
                return node.accept(visitor, data);
            }
        }, trees);
    }

    @Override
    public Object visitImport(ImportTree node, Trees trees) {
        return super.visitImport(node, trees);
    }

    public int getImportsNumber() {
        return importsNumber;
    }
    
}
