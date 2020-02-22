package com.sergio.compiler_api;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NumberOfIntFieldProcessor extends AbstractProcessor {

    private final NumberOfIntFieldScanner scanner;
    private Trees trees;
    private JavacProcessingEnvironment env;

    public NumberOfIntFieldProcessor( final NumberOfIntFieldScanner scanner ) {
        this.scanner = scanner;
    }

    @Override
    public synchronized void init( final ProcessingEnvironment processingEnvironment ) {
        super.init(processingEnvironment);
        trees = Trees.instance(processingEnvironment);
        this.env = (JavacProcessingEnvironment) processingEnvironment;
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        if(!environment.processingOver()) {
            for( final Element element: environment.getRootElements() ) {
                JCTree tree = (JCTree) trees.getPath(element).getCompilationUnit();
                System.out.println("tree = " + tree);
                
            }
        }
        return true;
    }
}
