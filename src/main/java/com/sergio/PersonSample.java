package com.sergio;

import java.util.UUID;

public class PersonSample {
    
    private String name;
    private int age;
    private UUID id;

    public int getAge() {
        if (age > 12) {
            return 12;
        }
        return age;
    }
}
