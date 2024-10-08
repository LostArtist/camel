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
package org.apache.camel.impl;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.ExpressionSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionSupportTest extends ContextTestSupport {

    private static class MyExpression extends ExpressionSupport {

        @Override
        protected String assertionFailureMessage(Exchange exchange) {
            return "foo";
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T evaluate(Exchange exchange, Class<T> type) {
            String in = exchange.getIn().getBody(String.class);
            if ("Kaboom".equals(in)) {
                return null;
            }
            return (T) in;
        }
    }

    @Test
    public void testExpressionSupport() {
        MyExpression my = new MyExpression();

        Exchange e = new DefaultExchange(context);
        e.getIn().setBody("bar");

        my.assertMatches("bar", e);
    }

    @Test
    public void testExpressionSupportFail() {
        MyExpression my = new MyExpression();

        Exchange e = new DefaultExchange(context);
        e.getIn().setBody("Kaboom");
        AssertionError ae = assertThrows(AssertionError.class,
                () -> my.assertMatches("damn", e),
                "Should have thrown exception");

        assertTrue(ae.getMessage().contains("foo"));
    }
}
