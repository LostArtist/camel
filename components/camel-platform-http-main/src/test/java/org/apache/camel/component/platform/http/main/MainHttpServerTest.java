/*
/*

    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements. See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with

    the License. You may obtain a copy of the License at
    *

    http://www.apache.org/licenses/LICENSE-2.0
    *

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    */

package org.apache.camel.component.platform.http.main;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.camel.CamelContext;
import org.apache.camel.component.platform.http.vertx.VertxPlatformHttpServerConfiguration;
import org.apache.camel.health.HealthCheck;
import org.apache.camel.health.HealthCheckHelper;
import org.apache.camel.health.HealthCheckRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.HttpServerConfigurationProperties;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.apache.camel.support.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MainHttpServerTest {

    private CamelContext camelContext;


    //private RoutingContext ctx;

    @Test
    public void statusIsntSatisfied() throws IOException, InterruptedException {

        //server.newHttpServer(configuration.getConfiguration());

        boolean enable = true;

        MainHttpServer server = new MainHttpServer();

        DefaultRegistry reg = new DefaultRegistry();
        reg.setLocalBeanRepository(reg.getLocalBeanRepository());



        server.setCamelContext(new DefaultCamelContext(new DefaultRegistry()));
        HealthCheckRegistry hcr = camelContext.getCamelContextExtension().getContextPlugin(HealthCheckRegistry.class);
        Collection<HealthCheck.Result> col = HealthCheckHelper.invoke(camelContext);



        server.setHost("0.0.0.0");
        server.setPort(8080);
        server.setPath("/");
    /*if (configuration.getMaxBodySize() != null) {
        server.setMaxBodySize(configuration.getMaxBodySize());
    }*/
        server.setUseGlobalSslContextParameters(enable);
        server.setDevConsoleEnabled(enable);
        server.setHealthCheckEnabled(enable);
        server.setUploadEnabled(enable);
        server.setUploadSourceDir("camel.jbang.sourceDir");

        server.start();

/* Handler<RoutingContext> handler = new Handler<RoutingContext>() {
@Override
public void handle(RoutingContext ctx) {
ctx.response().getStatusCode();
}
assertFalse();
int status = 500;
server.getStatus();
};*/

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/q/health/ready"))
                .build();

        //uri = new URIBuilder()

        //CloseableHttpClient client = HttpClients.createDefault();
        //HttpClient client = new HttpClient();
        //HttpClient client = HttpClient.newHttpClient();
        //HttpClient client = HttpClient.newBuilder().build();


        //HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

/* try (Socket socket = new Socket()) {
socket.connect(new InetSocketAddress("0.0.0.0", 8080), 5000);
} catch (IOException e) {

    }*/

        HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());



        assertEquals(500, response.statusCode());




    }

}