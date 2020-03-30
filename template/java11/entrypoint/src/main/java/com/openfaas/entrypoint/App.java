// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.entrypoint;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.Headers;

import com.openfaas.model.*;

public class App {

    public static void main(String[] args) throws Exception {
        int port = 8082;

        IHandler handler = new com.openfaas.function.Handler();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        InvokeHandler invokeHandler = new InvokeHandler(handler);

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
                InputStream inputStream = t.getRequestBody();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                // StandardCharsets.UTF_8.name() > JDK 7
                requestBody = result.toString(StandardCharsets.UTF_8);
	        }
            
            // System.out.println(requestBody);
            Headers reqHeaders = t.getRequestHeaders();
            Map<String, String> reqHeadersMap = new HashMap<>();

            for (Map.Entry<String, java.util.List<String>> header : reqHeaders.entrySet()) {
                java.util.List<String> headerValues = header.getValue();
                if(headerValues.size() > 0) {
                    reqHeadersMap.put(header.getKey(), headerValues.get(0));
                }
            }

            // for(Map.Entry<String, String> entry : reqHeadersMap.entrySet()) {
            //     System.out.println("Req header " + entry.getKey() + " " + entry.getValue());
            // }

            final IRequest req = new Request(requestBody, reqHeadersMap,t.getRequestURI().getRawQuery(), t.getRequestURI().getPath());
            final IResponse res = this.handler.Handle(req);

            final String response = res.getBody();
            byte[] bytesOut = response.getBytes(StandardCharsets.UTF_8);

            final Headers responseHeaders = t.getResponseHeaders();
            final String contentType = res.getContentType();
            if(contentType.length() > 0) {
                responseHeaders.set("Content-Type", contentType);
            }

            for(Map.Entry<String, String> entry : res.getHeaders().entrySet()) {
                responseHeaders.set(entry.getKey(), entry.getValue());
            }

            t.sendResponseHeaders(res.getStatusCode(), bytesOut.length);

            OutputStream os = t.getResponseBody();
            os.write(bytesOut);
            os.close();

            System.out.println("Request / " + bytesOut.length +" bytes written.");
        }
    }
}
