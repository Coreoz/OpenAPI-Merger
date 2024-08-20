package com.coreoz.openapi;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import com.coreoz.openapi.config.OpenApiMergerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.jetbrains.annotations.NotNull;

public class OpenApiMerger {
    /**
     * @param baseDefinitions
     * @param definitionsToBeAdded The definition to be added will be modified. If this is not the desired behavior, a copy of the base definition must be performed before calling this function
     * @param mergeConfiguration
     * @return
     */
    public static <T extends HttpRouteDefinition> @NotNull OpenAPI addDefinitions(
        @NotNull OpenAPI baseDefinitions, @NotNull OpenAPI definitionsToBeAdded, @NotNull OpenApiMergerConfiguration<T> mergeConfiguration
    ) {
        OpenApiComponentRecorder componentRecorder = new OpenApiComponentRecorder(mergeConfiguration.actions().onNewComponent());
        OpenApiPathMerger.mergePaths(baseDefinitions, definitionsToBeAdded, componentRecorder, mergeConfiguration);

        // TODO merge tags information to have tag descriptions

        OpenApiSchemaMerger.mergeSchemas(baseDefinitions, definitionsToBeAdded, componentRecorder);
        return baseDefinitions;
    }
}
