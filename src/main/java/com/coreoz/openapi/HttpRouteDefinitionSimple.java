package com.coreoz.openapi;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import org.jetbrains.annotations.NotNull;
import io.swagger.v3.oas.models.Operation;

public record HttpRouteDefinitionSimple(@NotNull String method, @NotNull String path, @NotNull Operation operation) implements HttpRouteDefinition {
}
