package com.sergio.dto;

import com.sun.tools.javac.util.Pair;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class DTOAnnotationProcessorTest {

    @Test
    public void testDTOAnnotationProcessor() throws ClassNotFoundException {

        final Pair<Boolean, DiagnosticCollector<JavaFileObject>> compile = compile();
        final boolean compilationSuccess = compile.fst;
        final DiagnosticCollector<JavaFileObject> diagnostic = compile.snd;
        
        if (!compilationSuccess) {
            diagnostic.getDiagnostics().forEach(d -> {
                if (d.getKind() == Diagnostic.Kind.ERROR) {
                    System.out.println(d.getMessage(null));
                    System.out.println("Line error: " + d.getLineNumber());
                    System.out.println("Column number: " + d.getColumnNumber());
                }
            });
            fail();
        }

        ClassLoader parentClassLoader = PersonSample.class.getClassLoader();
        ReloaderClassLoader loader = new ReloaderClassLoader(parentClassLoader);
        loader.setClassUrl("src/main/java/com/sergio/dto/PersonSample.class");
        Class<?> clazz = loader.loadClass("com.sergio.dto.PersonSample");
        Set<String> declaredMethods = Stream.of(clazz.getDeclaredMethods()).map(Method::getName).collect(toSet());
        assertThat(declaredMethods).contains("getId", "getName");
        assertThat(declaredMethods).contains("setId", "setName");
    }

    @Test
    public void shouldPrintNoteMessageWhenClassIsGoingToBeProcessed() {

        final Pair<Boolean, DiagnosticCollector<JavaFileObject>> compile = compile();
        final boolean compilationSuccess = compile.fst;
        final DiagnosticCollector<JavaFileObject> diagnostic = compile.snd;
        
        assertThat(compilationSuccess).isTrue();

        final boolean match = diagnostic.getDiagnostics().stream()
                .filter(d -> d.getKind() == Diagnostic.Kind.NOTE)
                .anyMatch(d -> d.getMessage(null).contains("annotated as @DTO. Will be processed."));
        
        assertThat(match).isTrue();
        
    }

    private Pair<Boolean, DiagnosticCollector<JavaFileObject>> compile() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null );

        final Path path = Paths.get("src/main/java/com/sergio/dto/PersonSample.java");

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjects(path.toFile());
        final JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics,
                null, null, sources);
        
        task.setProcessors(Collections.singletonList(new DTOAnnotationProcessor()));

        boolean success = task.call();

        return new Pair<>(success, diagnostics);
    }
    
}