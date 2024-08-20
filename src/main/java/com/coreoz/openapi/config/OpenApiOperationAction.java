package com.coreoz.openapi.config;

/**
 * TODO to complete
 * @param actionType
 * @param operationId
 * @param operationPath The path to be used when importing the new operation. Path parameters must have the same names as the original ones, else the operation parameter must be changed accordingly
 */
public record OpenApiOperationAction(OpenApiOperationActionType actionType, String operationId, String operationPath) {
    private static final OpenApiOperationAction NONE = new OpenApiOperationAction(OpenApiOperationActionType.NONE, null, null);

    public static OpenApiOperationAction none() {
        return NONE;
    }

    public static OpenApiOperationAction add(String operationId, String operationPath) {
        return new OpenApiOperationAction(OpenApiOperationActionType.ADD, operationId, operationPath);
    }
}
