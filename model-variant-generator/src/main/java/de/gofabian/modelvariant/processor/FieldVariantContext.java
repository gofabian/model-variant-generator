package de.gofabian.modelvariant.processor;

import de.gofabian.modelvariant.annotation.FieldVariant;

public record FieldVariantContext(
        String className,
        String fieldName,
        String fieldTypeName,
        FieldVariant config
) {
}