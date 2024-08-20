package com.coreoz.openapi;

import com.coreoz.http.routes.router.HttpRoute;

public record MockHttpRoute(String routeId, String method, String path) implements HttpRoute { }
