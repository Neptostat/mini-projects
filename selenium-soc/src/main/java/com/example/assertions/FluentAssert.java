package com.example.assertions;

import org.assertj.core.api.Assertions;

public class FluentAssert {
    public static void that(boolean condition, String message) {
        Assertions.assertThat(condition).as(message).isTrue();
    }

    public static void contains(String actual, String expected, String message) {
        Assertions.assertThat(actual).as(message).contains(expected);
    }
}
