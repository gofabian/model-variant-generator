# Model Variant Generator - Java

This library consists of a Java annotation processor that generates variants based on given
Java models.

## Maven

Add this library as a `compile` dependency. It will be called during compilation.

```maven
<dependency>
    <groupId>de.gofabian</groupId>
    <artifactId>model-variant-parent</artifactId>
    <version>0.1.0</version>
    <scope>compile</scope>
</dependency>
```

## Example

Imagine you want to have multiple variants of a domain model, e.g. to represent inputs for
creating and updating the original domain model.

Imagine you want to:

- have an **original model** containing all fields,
- have a **create variant** that does not contain generated fields (e.g. `id`, `createdAt`),
- have an **update variant** that contains the `id` but no other generated fields (e.g. `createdAt`).

You can do that by adding annotations to the original model:

```java
import de.gofabian.modelvariant.annotation.FieldVariant;

record Model(
        @FieldVariant(name = "Update")
        int id,

        @FieldVariant(name = "Create")
        @FieldVariant(name = "Update")
        String name,

        OffsetDateTime createdAt
) {
}
```

This library will fulfill your requirements by generating the following variants during compilation:

```java
record ModelCreateVariant(
        String name
) {
}

record ModelUpdateVariant(
        int id,
        String name
) {
}
```

## How does it work?

- The generator is a Java annotation processor.
- It is called during compilation.
- It reads the variant annotations and generates variants accordingly.
- The variants can be used in your code.
- The generator itself will not be part of the bytecode if you add it with `compile` scope.

## Next Steps

Release 0.2+:

- [ ] takeover original nullabililty
    - part of Variant field annotation
    - read jetbrains notnull/nullable annotations
    - produce jetbrains notnull/nullable annotations
- [ ] configure immutability
    - class annotation
    - immutable by default: Java records
    - mutable: custom Java class
- [ ] processor configuration?
    - global options to set e.g.
    - unmodifiable = true/false
    - @SupportedOptions("key"), add compiler arg in maven-compiler-plugin -Akey=value, processingEnv.getOptions().get("
      key")
