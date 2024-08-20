package com.coreoz.openapi.config;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @param routes
 * @param componentNamePrefix The prefix that will be used for all new component name that will be added. This is used to avoid conflict with existing components
 * @param operationIdPrefix Used if HttpEndpoint routeId is not present. The prefix that will be used for all new operation id that will be added. This is used to avoid conflict with existing operations
 * @param createMissingRoutes True if routes present in the {@link #routes} and not in OpenAPI base definitions should be added or not
 */
public record OpenApiMergerConfiguration<T extends HttpRouteDefinition>(
    @NotNull List<T> routes,
    @NotNull OpenApiMergerConfigurationActions<T> actions
) {
    private static final OpenApiMergerConfiguration<? extends HttpRouteDefinition> DEFAULT_CONFIGURATION = new OpenApiMergerConfiguration<>(List.of(), OpenApiMergerConfigurationActions.defaultActions());

    public static <T extends HttpRouteDefinition> OpenApiMergerConfiguration<T> defaultConfig() {
        //noinspection unchecked
        return (OpenApiMergerConfiguration<T>) DEFAULT_CONFIGURATION;
    }
}
