package com.sergio.dto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReloaderClassLoader extends ClassLoader {

    private final String classUrl;

    public ReloaderClassLoader(String classUrl) {
        this.classUrl = classUrl;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (!name.contains("PersonSample")) return super.loadClass(name);
        final Path path = Paths.get(classUrl);

        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }
}
