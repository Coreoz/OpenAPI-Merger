package com.coreoz.openapi.config;

@FunctionalInterface
public interface OpenApiOnNewComponent {
    String onNewComponent(String componentName);

    static OpenApiOnNewComponent identity() {
        return componentName -> componentName;
    }
}
