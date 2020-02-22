package com.sergio;

import com.sergio.compiler_api.NumberOfIntFieldProcessor;
import com.sergio.compiler_api.NumberOfIntFieldScanner;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class CountElementProcessorTest {

    @Test
    public void test() {
        final NumberOfIntFieldScanner scanner = new NumberOfIntFieldScanner();
        final NumberOfIntFieldProcessor processor = new NumberOfIntFieldProcessor( scanner );

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null );

        final Path path = Paths.get("src/main/java/com/sergio/PersonSample.java");
        System.out.println("path.toAbsolutePath() = " + path.toAbsolutePath());

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjects(path.toFile());
        final JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics,
                null, null, sources);
        task.setProcessors(Collections.singletonList(processor));
        task.call();

        System.out.format( "Classes %d", scanner.getImportsNumber());
    }
    
}