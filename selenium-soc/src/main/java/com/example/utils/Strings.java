package com.example.utils;

import java.util.UUID;

public final class Strings {
    private Strings(){}
    public static String randomEmail() {
        return "user+" + UUID.randomUUID().toString().substring(0,8) + "@example.test";
    }
}
