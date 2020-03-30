// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.entrypoint;

import com.openfaas.model.IHandler;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        int port = 8082;

        final IHandler handler = new com.openfaas.function.Handler();

        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        final InvokeHandler invokeHandler = new InvokeHandler(handler);

        server.createContext("/", invokeHandler);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class InvokeHandler implements HttpHandler {
        IHandler handler;

        private InvokeHandler(IHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestBody = "";
            String method = t.getRequestMethod();

            if (method.equalsIgnoreCase("POST")) {
                try (final InputStream inputStream = t.getRequestBody()) {
                    try (final ByteArrayOutputStream result = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            result.write(buffer, 0, length);
                        }
                        // StandardCharsets.UTF_8.name() > JDK 7
                        requestBody = result.toString(StandardCharsets.UTF_8);
                    }
                }
            }

            final Headers reqHeaders = t.getRequestHeaders();
            final Map<String, String> reqHeadersMap = reqHeaders.isEmpty()
                    ? Collections.emptyMap()
                    : new HashMap<>(reqHeaders.size());

            reqHeaders.forEach((k, v) -> {
                if (v.size() > 0) {
                    //noinspection ConstantConditions
                    reqHeadersMap.put(k, v.get(0));
                }
            });

            final IRequest req = new Request(requestBody, reqHeadersMap, t.getRequestURI().getRawQuery(), t.getRequestURI().getPath());
            final IResponse res = this.handler.Handle(req);

            final String response = res.getBody();
            byte[] bytesOut = response.getBytes(StandardCharsets.UTF_8);

            final Headers responseHeaders = t.getResponseHeaders();
            final String contentType = res.getContentType();
            if (contentType.length() > 0) {
                responseHeaders.set("Content-Type", contentType);
            }

            res.getHeaders().forEach(responseHeaders::set);
            t.sendResponseHeaders(res.getStatusCode(), bytesOut.length);

            try (final OutputStream os = t.getResponseBody()) {
                os.write(bytesOut);
            }

            logger.info("Request / {} bytes written.", bytesOut.length);
        }
    }
}
