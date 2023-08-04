package de.gofabian.modelvariant.example;

import de.gofabian.modelvariant.annotation.FieldVariant;

import java.time.OffsetDateTime;

public record FieldVariantModel(
        @FieldVariant(name = "Update")
        int id,

        @FieldVariant(name = "Create")
        @FieldVariant(name = "Update")
        String name,

        @FieldVariant(name = "Create")
        @FieldVariant(name = "Update")
        String description,

        OffsetDateTime createAt
) {
}
