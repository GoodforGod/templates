// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IRequest {

    @Nullable String getBody();

    @NotNull Map<String, String> getHeaders();

    @Nullable String getHeader(String key);

    @Nullable String getQueryRaw();

    @Nullable Map<String, String> getQuery();

    @NotNull String getPathRaw();

    @NotNull Map<String, String> getPath();
}