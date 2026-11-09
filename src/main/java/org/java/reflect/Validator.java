package org.java.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Validator {
    private List<ValidationError> handleAnnotations(Field field, Object value){
        List<ValidationError> errors = new ArrayList<>();
        Class<?>[] classesAnnotations = {NotNull.class, Size.class, Range.class, Email.class};
        for (Class<?> annotationClass : classesAnnotations){
            Annotation annotation = field.getAnnotation(annotationClass.asSubclass(Annotation.class));
            if (annotation != null){
                String errorMsg = null;
                if (annotationClass == NotNull.class) errorMsg = validateNotNull((NotNull) annotation, value);
                if (annotationClass == Size.class) errorMsg = validateSize(field, (Size) annotation, value);
                if (annotationClass == Range.class) errorMsg = validateRange(field, (Range) annotation, value);;
                if (annotationClass == Email.class) errorMsg = validateEmail(field, (Email) annotation, value);
                if (errorMsg != null){
                    errors.add(new ValidationError(field.getName(), annotation.annotationType().getSimpleName(), errorMsg));
                }
            }
        }
        return errors;
    }
    public ValidationResult validate(Object object) {
        List<ValidationError> errors = new ArrayList<>();
        if(object == null){
            throw new NullPointerException();
        }

        for (Class<?> type = object.getClass(); type != Object.class; type = type.getSuperclass()) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields){
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                if (!field.trySetAccessible()){
                    throw new ValidationConfigurationException("значения поля "
                            + field.getName()
                            + " не может быть прочитанно!");
                }
                Object value;
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    throw new ValidationConfigurationException("не удалось прочитать значения поля "
                            + field.getDeclaringClass().getName() + " "
                            + field.getName());
                }
                errors.addAll(handleAnnotations(field,value));
            }
        }
        return new ValidationResult(errors);
    }

    private String validateNotNull(NotNull annotation, Object value){
        if (value == null){
            return annotation.message();
        }
        return null;
    }
    private String validateSize(Field field, Size annotation, Object value){

        if (!CharSequence.class.isAssignableFrom(field.getType())) {
            throw new ValidationConfigurationException("аннотацию Size можно применять только к объектам расширяющих CharSequence");
        }
        if (value == null){
            return null;
        }
        if (annotation.max() < annotation.min()){
            throw new ValidationConfigurationException("max должен быть больше чем min!");
        }
        if (0 >= annotation.min()){
            throw new ValidationConfigurationException("min должен быть больше 0!");
        }

        int length = ((CharSequence)value).length();
        if (length < annotation.min() || length > annotation.max()) {
            return annotation.message();
        }

        return null;
    }
    private String validateRange(Field field, Range annotation, Object value){
        Class<?> type = field.getType();
        boolean numericPrimitive = type.isPrimitive() && type != boolean.class;

        if (!numericPrimitive && !Number.class.isAssignableFrom(type)){
            throw new ValidationConfigurationException("аннотацию можно применять только к числовым полям!");
        }

        if (!Double.isFinite(annotation.min()) || !Double.isFinite(annotation.max())){
            throw new ValidationConfigurationException("границы не должны быть бесконечны!");
        }
        if (annotation.min() > annotation.max()){
            throw new ValidationConfigurationException("max должен быть больше чем min!");
        }

        if (value == null){
            return null;
        }

        double number = type == char.class ? ((Character) value).charValue() : ((Number) value).doubleValue();

        if (Double.isNaN(number) || number < annotation.min() || number > annotation.max()){
            return annotation.message();
        }
        return null;
    }
    private String validateEmail(Field field, Email annotation, Object value){
        if (!CharSequence.class.isAssignableFrom(field.getType())){
            throw new ValidationConfigurationException("аннотацию Email можно применять только к типам расширяющим CharSequence");
        }

        if (value == null){
            return null;
        }

        String email = value.toString();
        String regex = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";
        if (!email.matches(regex)){
            return annotation.message();
        }
        return null;
    }
}
