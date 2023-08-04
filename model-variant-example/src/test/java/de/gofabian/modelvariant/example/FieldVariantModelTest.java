package de.gofabian.modelvariant.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldVariantModelTest {

    @Test
    void create_variant_contains_create_fields() {
        var variant = new FieldVariantModelCreateVariant("name", "description");
        assertEquals("name", variant.name());
        assertEquals("description", variant.description());
    }

    @Test
    void update_variant_contains_update_fields() {
        var variant = new FieldVariantModelUpdateVariant(42, "name", "description");
        assertEquals(42, variant.id());
        assertEquals("name", variant.name());
        assertEquals("description", variant.description());
    }

}
