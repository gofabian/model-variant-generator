package de.gofabian.modelvariant.processor;

import com.google.auto.service.AutoService;
import de.gofabian.modelvariant.annotation.FieldVariant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({
        "de.gofabian.modelvariant.annotation.FieldVariant",
        "de.gofabian.modelvariant.annotation.FieldVariants"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ModelVariantProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }
        var annotatedElements = roundEnv.getElementsAnnotatedWithAny(annotations.toArray(new TypeElement[0]));
        if (annotatedElements.isEmpty()) {
            return true;
        }

        var fieldVariants = collectFieldVariants(annotatedElements);
        var fieldVariantsMap = fieldVariants.stream()
                .collect(Collectors.groupingBy(fieldVariantContext -> fieldVariantContext.config().name()));

        fieldVariantsMap.forEach((variantName, groupedFieldVariants) -> {
            try {
                writeVariantFile(groupedFieldVariants);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Could not write variant '" + variantName + "' to file, cause: " + e.getClass().getName());
            }
        });

        return true;
    }

    private List<FieldVariantContext> collectFieldVariants(Set<? extends Element> annotatedElements) {
        return annotatedElements.stream().flatMap(element -> {
            var className = ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString();
            var fieldName = element.getSimpleName().toString();
            var fieldTypeName = element.asType().toString();

            var variantAnnotations = element.getAnnotationsByType(FieldVariant.class);
            return Arrays.stream(variantAnnotations)
                    .map(fieldVariantAnnotation -> new FieldVariantContext(className, fieldName, fieldTypeName, fieldVariantAnnotation));
        }).toList();
    }

    private void writeVariantFile(List<FieldVariantContext> fieldVariantContexts) throws IOException {
        if (fieldVariantContexts.isEmpty()) {
            return;
        }

        String className = fieldVariantContexts.get(0).className();
        int lastDot = className.lastIndexOf('.');

        String packageName = null;
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String variantName = fieldVariantContexts.get(0).config().name();
        String variantClassName = className + variantName + "Variant";
        String variantSimpleClassName = variantClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(variantClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public record ");
            out.print(variantSimpleClassName);
            out.print("(");

            for (int i = 0; i < fieldVariantContexts.size(); i++) {
                var fieldVariant = fieldVariantContexts.get(i);

                if (i > 0) {
                    out.print(",");
                }
                out.println("");

                out.print("        ");
                out.print(fieldVariant.fieldTypeName());
                out.print(" ");
                out.print(fieldVariant.fieldName());
            }

            out.println();
            out.println(") {");
            out.println("}");
        }
    }
}
