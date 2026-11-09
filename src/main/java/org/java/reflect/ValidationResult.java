package org.java.reflect;
import java.util.List;

public final class ValidationResult {
    private final List<ValidationError> errors;

    public ValidationResult(List<ValidationError> errors) {
        this.errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
