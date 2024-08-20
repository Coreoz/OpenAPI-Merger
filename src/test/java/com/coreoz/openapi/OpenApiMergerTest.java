package com.coreoz.openapi;

import com.coreoz.openapi.config.OpenApiMergerConfiguration;
import com.coreoz.openapi.config.OpenApiMergerConfigurationActions;
import com.coreoz.openapi.config.OpenApiOnMissingOperation;
import com.coreoz.openapi.config.OpenApiOnMissingRoute;
import com.coreoz.openapi.config.OpenApiOnNewComponent;
import com.coreoz.openapi.config.OpenApiOperationAction;
import com.google.common.io.Resources;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.SneakyThrows;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

// TODO more unit testing
public class OpenApiMergerTest {
    @SneakyThrows
    @Test
    public void basic_test() {
        OpenAPI baseDefinitions = readResource("/base.yaml");
        OpenAPI petStoreDefinitions = readResource("/petstore.yaml");
        OpenAPI result = OpenApiMerger.addDefinitions(
            baseDefinitions,
            petStoreDefinitions,
            new OpenApiMergerConfiguration<>(
                List.of(
                    new MockHttpRoute("route-test", "GET", "/pets-gateway/{id}"),
                    new MockHttpRoute("pets", "GET", "/pets")
                ),
                new OpenApiMergerConfigurationActions<>(
                    (operation, routeDefinition) -> OpenApiOperationAction.add(
                        routeDefinition.routeDefinition().routeId(),
                        routeDefinition.routeDefinition().path()
                    ),
                    OpenApiOnMissingOperation.noneAction(),
                    OpenApiOnMissingRoute.addOperation(),
                    OpenApiOnNewComponent.identity()
                )
            )
        );
        System.out.println(Yaml.mapper().writeValueAsString(result));
    }

    @SneakyThrows
    private OpenAPI readResource(String resourcePath) {
        //noinspection DataFlowIssue
        return new OpenAPIParser()
            .readContents(Resources.toString(OpenApiMergerTest.class.getResource(resourcePath), StandardCharsets.UTF_8), null, null)
            .getOpenAPI();
    }
}
