package com.coreoz.openapi.config;

import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import com.coreoz.http.routes.parsing.ParsedRoute;

@FunctionalInterface
public interface OpenApiOnMissingOperation<T extends HttpRouteDefinition> {
    OpenApiOperationAction onMissingOperation(ParsedRoute<T> route);

    static <T extends HttpRouteDefinition> OpenApiOnMissingOperation<T> noneAction() {
        return routeDefinition -> OpenApiOperationAction.none();
    }
}
