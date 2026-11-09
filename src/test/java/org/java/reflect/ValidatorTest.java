package org.java.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {

    private final Validator validator = new Validator();

    @Test
    void valid(){
        ValidationResult result = validator.validate(new CorrectClassTest());

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void rules(){
        CorrectClassTest object = new CorrectClassTest();
        object.valNull = null;
        assertEquals(1, validator.validate(object).getErrors().size());

        object = new CorrectClassTest();
        object.valLong = "123456";
        assertEquals(1, validator.validate(object).getErrors().size());

        object = new CorrectClassTest();
        object.valBig = 21;
        assertEquals(1, validator.validate(object).getErrors().size());

        object = new CorrectClassTest();
        object.email = "test";
        assertEquals(1, validator.validate(object).getErrors().size());
    }

    @Test
    void invalid(){
        ValidationResult result = validator.validate(new IncorrectClassTest());

        assertFalse(result.isValid());
        assertEquals(4, result.getErrors().size());
    }

    @Test
    void bounds(){
        CorrectClassTest object = new CorrectClassTest();

        object.valLong = "1";
        object.valBig = 10;
        assertTrue(validator.validate(object).isValid());

        object.valLong = "12345";
        object.valBig = 20;
        assertTrue(validator.validate(object).isValid());

        object.valLong = "";
        assertFalse(validator.validate(object).isValid());

        object.valLong = "123456";
        assertFalse(validator.validate(object).isValid());

        object.valLong = "123";
        object.valBig = 9;
        assertFalse(validator.validate(object).isValid());

        object.valBig = 21;
        assertFalse(validator.validate(object).isValid());
    }

    @Test
    void exceptions(){
        assertThrows(NullPointerException.class, () -> validator.validate(null));
        assertThrows(ValidationConfigurationException.class, () -> validator.validate(new WrongBoundsClass()));
        assertThrows(ValidationConfigurationException.class, () -> validator.validate(new WrongTypeClass()));
    }

    @Test
    void fields(){
        ValidationResult result = validator.validate(new ChildClass());

        assertEquals(1, result.getErrors().size());
        assertEquals("inherited", result.getErrors().getFirst().fieldName());
    }

    @Test
    void immutableErrors(){
        ValidationResult result = validator.validate(new IncorrectClassTest());

        assertThrows(UnsupportedOperationException.class, () -> result.getErrors().clear());
    }
}
