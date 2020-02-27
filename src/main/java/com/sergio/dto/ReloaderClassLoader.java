package com.sergio.dto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReloaderClassLoader extends ClassLoader {

    private String classUrl;

    public ReloaderClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = super.loadClass(name);
        final boolean annotationPresent = clazz.isAnnotationPresent(DTO.class);
        if (!annotationPresent) return clazz;
        
        System.out.println("LOADING CLASS ANNOTATED WITH @DTO = " + name);
        
        final Path path = Paths.get(classUrl);
        
        try {
            final byte[] bytes = Files.readAllBytes(path);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void setClassUrl(String classUrl) {
        this.classUrl = classUrl;
    }
}
