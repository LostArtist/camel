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
package org.apache.camel.component.file;

import java.io.File;
import java.util.UUID;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

public class FileAbsoluteAndRelativeConsumerTest extends ContextTestSupport {
    private static final String TEST_FILE_NAME = "hello." + UUID.randomUUID() + ".txt";

    @Test
    public void testRelative() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:relative");
        mock.expectedMessageCount(1);

        mock.message(0).header(Exchange.FILE_NAME).isEqualTo("test" + File.separator + TEST_FILE_NAME);
        mock.message(0).header(Exchange.FILE_NAME_ONLY).isEqualTo(TEST_FILE_NAME);

        template.sendBodyAndHeader(fileUri("filerelative"), "Hello World", Exchange.FILE_NAME, "test/" + TEST_FILE_NAME);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testAbsolute() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:absolute");
        mock.expectedMessageCount(1);

        mock.message(0).header(Exchange.FILE_NAME).isEqualTo("test" + File.separator + TEST_FILE_NAME);
        mock.message(0).header(Exchange.FILE_NAME_ONLY).isEqualTo(TEST_FILE_NAME);

        template.sendBodyAndHeader(fileUri("fileabsolute"), "Hello World", Exchange.FILE_NAME, "test/" + TEST_FILE_NAME);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(fileUri("filerelative?initialDelay=0&delay=10&recursive=true")).convertBodyTo(String.class)
                        .to("mock:relative");

                from("file://" + testDirectory("fileabsolute").toAbsolutePath() + "?initialDelay=0&delay=10&recursive=true")
                        .convertBodyTo(String.class)
                        .to("mock:absolute");
            }
        };
    }
}
