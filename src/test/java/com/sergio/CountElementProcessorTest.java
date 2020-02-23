package com.sergio;

import com.sergio.dto.DTOAnnotationProcessor;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static junit.framework.TestCase.fail;

public class CountElementProcessorTest {

    @Test
    public void test() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null );

        final Path path = Paths.get("src/main/java/com/sergio/dto/PersonSample.java");

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjects(path.toFile());
        final JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics,
                null, null, sources);
        task.setProcessors(Collections.singletonList(new DTOAnnotationProcessor()));
        task.call();

        diagnostics.getDiagnostics().forEach(d -> {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                System.out.println(d.getMessage(null));
                System.out.println("Line error: " + d.getLineNumber());
                System.out.println("Column number: " + d.getColumnNumber());
                fail();
            }
        });
    }
    
}