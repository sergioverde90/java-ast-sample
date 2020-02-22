package com.sergio.compiler_api;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes( "*" )
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CountElementProcessor extends AbstractProcessor {

    private final CountClassesAndMethodsFieldScanner scanner;

    public CountElementProcessor( final CountClassesAndMethodsFieldScanner scanner ) {
        this.scanner = scanner;
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {
        if( !environment.processingOver()) {
            for( final Element element: environment.getRootElements() ) {
                scanner.scan(element    );
            }
        }

        return true;
    }
}
