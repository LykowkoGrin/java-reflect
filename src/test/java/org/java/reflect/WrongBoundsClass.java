package org.java.reflect;

class WrongBoundsClass {
    @Range(min = 20, max = 10)
    int value = 15;
}
