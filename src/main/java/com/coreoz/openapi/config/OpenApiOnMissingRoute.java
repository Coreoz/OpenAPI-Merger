package com.coreoz.openapi.config;

import com.coreoz.http.routes.parsing.ParsedRoute;
import com.coreoz.openapi.HttpRouteDefinitionSimple;

@FunctionalInterface
public interface OpenApiOnMissingRoute {
    OpenApiOperationAction onMissingRoute(ParsedRoute<HttpRouteDefinitionSimple> parsedOperation);

    static OpenApiOnMissingRoute addOperation() {
        return parsedOperation -> OpenApiOperationAction.add(
            parsedOperation.routeDefinition().operation().getOperationId(),
            parsedOperation.routeDefinition().path()
        );
    }
}
