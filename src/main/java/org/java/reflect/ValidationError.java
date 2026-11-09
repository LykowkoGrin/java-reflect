package org.java.reflect;

public record ValidationError(String fieldName, String annotationName, String message) {
}
