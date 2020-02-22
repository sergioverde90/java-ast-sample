package com.sergio;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner9;

public class CountClassesAndMethodsFieldScanner extends ElementScanner9<Void, Void> {
    
    private int numberOfClasses;

    @Override
    public Void visitType(TypeElement e, Void aVoid) {
        ++this.numberOfClasses;
        return super.visitType(e, aVoid);
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }
}
