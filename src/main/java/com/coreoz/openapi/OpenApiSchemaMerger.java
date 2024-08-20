package com.coreoz.openapi;

import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

class OpenApiSchemaMerger {
    static void updateOperationSchemaNames(@NotNull OpenApiComponentRecorder componentRecorder, Operation operation) {
        updateSchemaName(componentRecorder, MoreObjects.firstNonNull(operation.getParameters(), List.of()), Parameter::get$ref, Parameter::set$ref);
        updateSchemaName(
            componentRecorder,
            operation.getRequestBody() == null ? List.of() : List.of(operation.getRequestBody()),
            RequestBody::get$ref,
            RequestBody::set$ref
        );
        updateSchemaName(
            componentRecorder,
            operation.getResponses().entrySet(),
            entry -> entry.getValue().get$ref(),
            (entry, newSchemaName) -> entry.getValue().set$ref(newSchemaName)
        );
        if (operation.getResponses() == null) {
            return;
        }
        for (var apiResponse : operation.getResponses().values()) {
            if (apiResponse.get$ref() != null) {
                String newSchemaName = componentRecorder.addSchema(apiResponse.get$ref());
                apiResponse.set$ref(newSchemaName);
            }
            if (apiResponse.getContent() != null) {
                for (var contentValue : apiResponse.getContent().values()) {
                    updateComponentReferences(contentValue.getSchema(), componentRecorder);
                }
            }
        }
    }

    private static <T> void updateSchemaName(
        @NotNull OpenApiComponentRecorder componentRecorder, @NotNull Collection<T> elements, @NotNull Function<T, String> schemaNameExtractor, @NotNull BiConsumer<T, String> schemaNameUpdater
    ) {
        for (T element : elements) {
            String currentSchemaName = schemaNameExtractor.apply(element);
            if (currentSchemaName != null) {
                String newSchemaName = componentRecorder.addSchema(currentSchemaName);
                schemaNameUpdater.accept(element, newSchemaName);
            }
        }
    }

    static void mergeSchemas(@NotNull OpenAPI baseDefinitions, @NotNull OpenAPI definitionsToBeAdded, @NotNull OpenApiComponentRecorder componentRecorder) {
        if (componentRecorder.readSchemaMappings().isEmpty()) {
            return;
        }

        updateComponentReferences(definitionsToBeAdded, componentRecorder);

        if (baseDefinitions.getComponents() == null) {
            Components components = new Components();
            baseDefinitions.setComponents(components);
        }
        if (baseDefinitions.getComponents().getSchemas() == null) {
            baseDefinitions.getComponents().setSchemas(new HashMap<>());
        }
        for (OpenApiSchemaMapping schemaToBeMerged : componentRecorder.readSchemaMappings()) {
            // TODO verify that schema does not already exists
            baseDefinitions.getComponents().getSchemas().put(
                schemaToBeMerged.newName(),
                definitionsToBeAdded.getComponents().getSchemas().get(schemaToBeMerged.initialName())
            );
        }
    }

    private static void updateComponentReferences(
        @NotNull OpenAPI definitionsToBeAdded, @NotNull OpenApiComponentRecorder componentRecorder
    ) {
        if (definitionsToBeAdded.getComponents() != null && definitionsToBeAdded.getComponents().getSchemas() != null) {
            for (Schema<?> schemaToBeAdded : definitionsToBeAdded.getComponents().getSchemas().values()) {
                updateComponentReferences(schemaToBeAdded, componentRecorder);
            }
        }
    }

    private static void updateComponentReferences(@Nullable Schema<?> schemaToBeAdded, @NotNull OpenApiComponentRecorder componentRecorder) {
        if (schemaToBeAdded == null) {
            return;
        }

        updateSchemaName(componentRecorder, List.of(schemaToBeAdded), Schema::get$ref, Schema::set$ref);
        if (schemaToBeAdded.getItems() != null) {
            updateSchemaName(componentRecorder, List.of(schemaToBeAdded.getItems()), Schema::get$ref, Schema::set$ref);
        }

        if (schemaToBeAdded.getProperties() != null) {
            for (Schema<?> schemaProperties : schemaToBeAdded.getProperties().values()) {
                updateComponentReferences(schemaProperties, componentRecorder);
            }
        }
    }
}
