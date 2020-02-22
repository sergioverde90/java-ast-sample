package com.sergio;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CountElementProcessorTest {

    public static void main(String[] args) {
        final NumberOfIntFieldScanner scanner = new NumberOfIntFieldScanner();
        final NumberOfIntFieldProcessor processor = new NumberOfIntFieldProcessor( scanner );

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null );

        final Path path = Paths.get("src/com/sergio/PersonSample.java");
        System.out.println("path.toAbsolutePath() = " + path.toAbsolutePath());

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjects(path);
        final JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics,
                null, null, sources);
        task.setProcessors( List.of(processor));
        task.call();

        System.out.format( "Classes %d", scanner.getImportsNumber());
    }
    
}