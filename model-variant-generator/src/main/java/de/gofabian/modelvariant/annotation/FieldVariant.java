package de.gofabian.modelvariant.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(FieldVariants.class)
public @interface FieldVariant {

    String name();

//    Mutability mutable() default Mutability.DEFAULT;
//
//    Nullability nullable() default Nullability.DEFAULT;
//
//    enum Nullability {
//        DEFAULT,
//        ORIGINAL,
//        FORCE_NULLABLE,
//        FORCE_NOT_NULL
//    }
//
//    enum Mutability {
//        DEFAULT,
//        MUTABLE,
//        IMMUTABLE;
//    }

}
