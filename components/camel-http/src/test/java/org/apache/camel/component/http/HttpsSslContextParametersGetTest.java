/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.http;

import org.apache.camel.Exchange;
import org.apache.camel.component.http.handler.BasicValidationHandler;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.junit.jupiter.api.Test;

import static org.apache.camel.component.http.HttpMethods.GET;

public class HttpsSslContextParametersGetTest extends HttpsGetTest {

    private HttpServer localServer;

    @Override
    public final void doPreSetup() throws Exception {
        localServer = ServerBootstrap.bootstrap()
                .setCanonicalHostName("localhost").setHttpProcessor(getBasicHttpProcessor())
                .setConnectionReuseStrategy(getConnectionReuseStrategy()).setResponseFactory(getHttpResponseFactory())
                .setSslContext(getSSLContext())
                .register("/mail/", new BasicValidationHandler(GET.name(), null, null, getExpectedContent())).create();
        localServer.start();
    }

    @Override
    public void cleanupResources() {
        if (localServer != null) {
            localServer.stop();
        }
    }

    @Override
    @Test
    public void httpsGet() {

        Exchange exchange = template.request("https://localhost:" + localServer.getLocalPort()
                                             + "/mail/?x509HostnameVerifier=x509HostnameVerifier&sslContextParameters=#sslContextParameters",
                exchange1 -> {
                });

        assertExchange(exchange);
    }

}
