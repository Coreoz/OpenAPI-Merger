package com.coreoz.openapi;

import com.coreoz.openapi.config.OpenApiOnNewComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

class OpenApiComponentRecorder {
    private static final String COMPONENT_SCHEMA_PREFIX = "#/components/schemas/";

    private final OpenApiOnNewComponent onNewComponent;
    private final Set<OpenApiSchemaMapping> addedSchemas;

    OpenApiComponentRecorder(OpenApiOnNewComponent onNewComponent) {
        this.onNewComponent = onNewComponent;
        this.addedSchemas = new HashSet<>();
    }

    String addSchema(String baseName) {
        // TODO verify if schema has not already been added
        String newSchemaName = renameSchema(baseName, onNewComponent);
        addedSchemas.add(createMapping(baseName, newSchemaName));
        return newSchemaName;
    }

    Set<OpenApiSchemaMapping> readSchemaMappings() {
        return addedSchemas;
    }

    private static OpenApiSchemaMapping createMapping(String currentSchemaName, String newSchemaName) {
        return new OpenApiSchemaMapping(parseSchemaName(currentSchemaName), parseSchemaName(newSchemaName));
    }

    private static @NotNull String parseSchemaName(@NotNull String schemaReference) {
        return schemaReference.substring(COMPONENT_SCHEMA_PREFIX.length());
    }

    private static @NotNull String renameSchema(@NotNull String currentComponentName, @NotNull OpenApiOnNewComponent onNewComponent) {
        if (!currentComponentName.startsWith(COMPONENT_SCHEMA_PREFIX)) {
            return currentComponentName;
        }
        return COMPONENT_SCHEMA_PREFIX + onNewComponent.onNewComponent(parseSchemaName(currentComponentName));
    }
}
