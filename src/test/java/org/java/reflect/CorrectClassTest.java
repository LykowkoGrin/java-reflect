package org.java.reflect;

public class CorrectClassTest {
    @NotNull
    @Range(min = 10, max = 20)
    int myval = 15;

    @NotNull
    String valNull = "123";
    @Range(min = 10, max = 20)
    int valBig = 15;
    @Size(min = 1, max = 5)
    String valLong = "123";
    @Email
    String email = "test@test.com";
}
