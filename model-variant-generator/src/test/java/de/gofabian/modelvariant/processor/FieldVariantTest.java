package de.gofabian.modelvariant.processor;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldVariantTest {

    @Test
    void when_no_annotations_exist_then_no_source_file_will_be_generated() {
        var compilation = com.google.testing.compile.Compiler.javac()
                .withProcessors(new ModelVariantProcessor())
                .compile(JavaFileObjects.forSourceString("Model", """
                        class Model {
                            String name;
                        }
                        """));

        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();
        assertEquals(0, compilation.generatedSourceFiles().size());
    }

    @Test
    void when_field_is_annotated_then_source_file_will_be_generated() {
        var compilation = com.google.testing.compile.Compiler.javac()
                .withProcessors(new ModelVariantProcessor())
                .compile(JavaFileObjects.forSourceString("Model", """
                        import de.gofabian.modelvariant.annotation.FieldVariant;
                                                
                        class Model {
                            @FieldVariant(name="Create") String name;
                        }
                        """));

        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();
        assertEquals(1, compilation.generatedSourceFiles().size());
        CompilationSubject.assertThat(compilation).generatedSourceFile("ModelCreateVariant")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceString("ModelCreateVariant", """
                        public record ModelCreateVariant(
                                java.lang.String name
                        ) {}
                        """));
    }

    @Test
    void when_field_is_not_annotated_then_it_will_not_be_part_of_variant() {
        var compilation = com.google.testing.compile.Compiler.javac()
                .withProcessors(new ModelVariantProcessor())
                .compile(JavaFileObjects.forSourceString("Model", """
                        import de.gofabian.modelvariant.annotation.FieldVariant;
                                                
                        class Model {
                            String id;
                            @FieldVariant(name="Create") String name;
                        }
                        """));

        CompilationSubject.assertThat(compilation).succeededWithoutWarnings();
        assertEquals(1, compilation.generatedSourceFiles().size());
        CompilationSubject.assertThat(compilation).generatedSourceFile("ModelCreateVariant")
                .hasSourceEquivalentTo(JavaFileObjects.forSourceString("ModelCreateVariant", """
                        public record ModelCreateVariant(
                                java.lang.String name
                        ) {}
                        """));
    }


}
