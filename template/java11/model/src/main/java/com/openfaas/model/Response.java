// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Response implements IResponse {

    private int statusCode = 200;
    private String body = "";
    private String contentType = "";
    private Map<String, String> headers = null;

    public int getStatusCode() {
        return statusCode;
    }

    @NotNull
    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @NotNull
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @NotNull
    @Override
    public Response setHeader(@NotNull String key, @NotNull String value) {
        if (headers == null)
            this.headers = new HashMap<>();
        this.headers.put(key, value);
        return this;
    }

    public String getHeader(String key) {
        return (headers == null)
                ? null
                : this.headers.get(key);
    }

    @NotNull
    public Response setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getContentType() {
        return this.contentType;
    }

    @NotNull
    public Response setBody(String body) {
        this.body = body;
        return this;
    }

    public String getBody() {
        return this.body;
    }
}
