package com.coreoz.openapi.config;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record OpenApiMergerConfigurationActions<T extends HttpRouteDefinition>(
    @NotNull OpenApiOnRouteMatch<T> onRouteMatch,
    @NotNull OpenApiOnMissingOperation<T> onMissingOperation,
    @NotNull OpenApiOnMissingRoute onMissingRoute,
    @NotNull OpenApiOnNewComponent onNewComponent
) {
    private static final OpenApiMergerConfigurationActions<? extends HttpRouteDefinition> DEFAULT_ACTIONS = new OpenApiMergerConfigurationActions<>(
        OpenApiOnRouteMatch.addOperation(),
        OpenApiOnMissingOperation.noneAction(),
        OpenApiOnMissingRoute.addOperation(),
        OpenApiOnNewComponent.identity()
    );

    public static <T extends HttpRouteDefinition> @NotNull OpenApiMergerConfigurationActions<T> defaultActions() {
        //noinspection unchecked
        return (OpenApiMergerConfigurationActions<T>) DEFAULT_ACTIONS;
    }

    // TODO à déplacer dans l'adapter de merge sûrement
    public static @NotNull String makeOperationIdPrefix(@Nullable String prefix, @NotNull String operationId) {
        return Strings.nullToEmpty(prefix) + operationId;
    }
}
