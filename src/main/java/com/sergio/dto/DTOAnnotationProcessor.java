package com.sergio.dto;

import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("com.sergio.dto.DTO")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DTOAnnotationProcessor extends AbstractProcessor {

    private JavacProcessingEnvironment env;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.env = (JavacProcessingEnvironment) processingEnv;
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        final Context context = env.getContext();
        final Trees trees = Trees.instance(env);

        if (!environment.processingOver()) { // in order to avoid processing twice in the last round
            for (Element codeElement : environment.getRootElements()) {
                if (codeElement.getKind() != ElementKind.CLASS) continue;
                JCTree tree = (JCTree) trees.getTree(codeElement);
                tree.accept(new SimpleJavaTranslator(context));
            }
        }
        
        return false; // false as default behaviour. Process subsequent annotations if exists.
    }
}
