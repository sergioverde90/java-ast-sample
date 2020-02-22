package com.sergio.compiler_api;

import com.sergio.java_plugin.Positive;

import java.util.UUID;

public class PersonSample {
    
    private String name;
    private int age;
    private UUID id;

    public void getAge(@Positive int age) {
        this.age = age;
    }
}
