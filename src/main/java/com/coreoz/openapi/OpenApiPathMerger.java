package com.coreoz.openapi;

import com.coreoz.http.routes.HttpRoutes;
import com.coreoz.http.routes.parsing.HttpRouteDefinition;
import com.coreoz.http.routes.parsing.ParsedRoute;
import com.coreoz.openapi.config.OpenApiMergerConfiguration;
import com.coreoz.openapi.config.OpenApiOperationAction;
import com.coreoz.openapi.config.OpenApiOperationActionType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

class OpenApiPathMerger {
    static <T extends HttpRouteDefinition> void mergePaths(
        @NotNull OpenAPI baseDefinitions,
        @NotNull OpenAPI definitionsToBeAdded,
        @NotNull OpenApiComponentRecorder componentRecorder,
        @NotNull OpenApiMergerConfiguration<T> mergeConfiguration
    ) {
        Iterable<ParsedRoute<T>> parsedRoutes = mergeConfiguration
            .routes()
            .stream()
            .map(HttpRoutes::parseRoute)
            ::iterator;
        ListMerger.compare(
            extractOpenApiOperations(definitionsToBeAdded),
            parsedRoutes,
            Comparator
                .comparing((ParsedRoute<?> parsedRoute) -> parsedRoute.parsedPath().genericPath())
                .thenComparing((ParsedRoute<?> left, ParsedRoute<?> right) -> left.routeDefinition().method().compareToIgnoreCase(right.routeDefinition().method())),
            leftOnly -> addOperationToBaseDefinition(
                baseDefinitions,
                mergeConfiguration.actions().onMissingRoute().onMissingRoute(leftOnly),
                leftOnly.routeDefinition(),
                componentRecorder
            ),
            rightOnly -> addOperationToBaseDefinition(
                baseDefinitions,
                mergeConfiguration.actions().onMissingOperation().onMissingOperation(rightOnly),
                rightOnly.routeDefinition(),
                componentRecorder
            ),
            (left, right) -> addOperationToBaseDefinition(
                baseDefinitions,
                mergeConfiguration.actions().onRouteMatch().onRouteMatch(left, right),
                left.routeDefinition(),
                componentRecorder
            )
        );
    }

    private static <T extends HttpRouteDefinition> void addOperationToBaseDefinition(OpenAPI baseDefinitions, OpenApiOperationAction openApiOperationAction, T rightOnly, @NotNull OpenApiComponentRecorder componentRecorder) {
        // TODO to implement => create HttpRouteDefinitionSimple and call other method
    }

    private static void addOperationToBaseDefinition(@NotNull OpenAPI baseDefinitions, OpenApiOperationAction openApiOperationAction, HttpRouteDefinitionSimple operationDefinition, @NotNull OpenApiComponentRecorder componentRecorder) {
        if (openApiOperationAction.actionType() == OpenApiOperationActionType.NONE) {
            return;
        }

        PathItem.HttpMethod operationMethod = PathItem.HttpMethod.valueOf(operationDefinition.method());
        // Collect and possibly rename operation associated direct schemas
        OpenApiSchemaMerger.updateOperationSchemaNames(componentRecorder, operationDefinition.operation());
        // Update operation id
        operationDefinition.operation().operationId(openApiOperationAction.operationId());

        // Reuse existing path item, or create a new one if none already exists
        PathItem pathItem = null;
        if (baseDefinitions.getPaths() != null) {
            pathItem = baseDefinitions.getPaths().get(openApiOperationAction.operationPath());
        }
        if (pathItem == null) {
            pathItem = new PathItem();
            baseDefinitions.path(openApiOperationAction.operationPath(), pathItem);
        }

        // TODO handle cases where the route already exists in baseDefinitions
        // Configure the operation in the path item
        pathItem.operation(
            operationMethod,
            operationDefinition.operation()
        );

    }

    private static @NotNull Iterable<ParsedRoute<HttpRouteDefinitionSimple>> extractOpenApiOperations(@NotNull OpenAPI baseDefinitions) {
        return baseDefinitions
            .getPaths()
            .entrySet()
            .stream()
            .flatMap(entry -> entry
                .getValue()
                .readOperationsMap()
                .entrySet()
                .stream()
                .map(operationEntry -> HttpRoutes.parseRoute(new HttpRouteDefinitionSimple(operationEntry.getKey().name(), entry.getKey(), operationEntry.getValue())))
            )::iterator;
    }
}
