package org.java.reflect;

public class IncorrectClassTest {
    @NotNull
    String valNull = null;
    @Range(min = 10, max = 20)
    int valBig = 10000;
    @Size(min = 1, max = 5)
    String valLong = "12345678910";
    @Email
    String email = "test";
}
