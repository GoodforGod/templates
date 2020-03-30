// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IResponse {

    @Nullable String getBody();

    @NotNull IResponse setBody(String body);

    @Nullable String getHeader(String key);

    @NotNull IResponse setHeader(@NotNull String key, @NotNull String value);

    @NotNull Map<String, String> getHeaders();

    @NotNull IResponse setContentType(String contentType);

    @Nullable String getContentType();

    @NotNull IResponse setStatusCode(int code);

    int getStatusCode();
}
