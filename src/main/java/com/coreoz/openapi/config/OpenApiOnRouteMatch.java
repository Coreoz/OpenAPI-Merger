package com.coreoz.openapi.config;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import com.coreoz.http.routes.parsing.ParsedRoute;
import com.coreoz.openapi.HttpRouteDefinitionSimple;

@FunctionalInterface
public interface OpenApiOnRouteMatch<T extends HttpRouteDefinition> {
    OpenApiOperationAction onRouteMatch(ParsedRoute<HttpRouteDefinitionSimple> parsedOperation, ParsedRoute<T> parsedRoute);

    static <T extends HttpRouteDefinition> OpenApiOnRouteMatch<T> addOperation() {
        return (parsedOperation, parsedRoute) -> OpenApiOperationAction.add(
            parsedOperation.routeDefinition().operation().getOperationId(),
            parsedOperation.routeDefinition().path()
        );
    }
}
