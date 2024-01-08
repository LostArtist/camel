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
package org.apache.camel.builder.endpoint.dsl;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import javax.annotation.processing.Generated;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.AbstractEndpointBuilder;

/**
 * The Dynamic Router control endpoint for operations that allow routing
 * participants to subscribe or unsubscribe to participate in dynamic message
 * routing.
 * 
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.EndpointDslMojo")
public interface DynamicRouterControlEndpointBuilderFactory {


    /**
     * Builder for endpoint for the Dynamic Router Control component.
     */
    public interface DynamicRouterControlEndpointBuilder
            extends
                EndpointProducerBuilder {
        default AdvancedDynamicRouterControlEndpointBuilder advanced() {
            return (AdvancedDynamicRouterControlEndpointBuilder) this;
        }
        /**
         * The destination URI for exchanges that match.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param destinationUri the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder destinationUri(
                String destinationUri) {
            doSetProperty("destinationUri", destinationUri);
            return this;
        }
        /**
         * The subscription predicate language.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Default: simple
         * Group: control
         * 
         * @param expressionLanguage the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder expressionLanguage(
                String expressionLanguage) {
            doSetProperty("expressionLanguage", expressionLanguage);
            return this;
        }
        /**
         * The subscription predicate.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param predicate the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder predicate(String predicate) {
            doSetProperty("predicate", predicate);
            return this;
        }
        /**
         * A Predicate instance in the registry.
         * 
         * The option is a: &lt;code&gt;org.apache.camel.Predicate&lt;/code&gt;
         * type.
         * 
         * Group: control
         * 
         * @param predicateBean the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder predicateBean(
                org.apache.camel.Predicate predicateBean) {
            doSetProperty("predicateBean", predicateBean);
            return this;
        }
        /**
         * A Predicate instance in the registry.
         * 
         * The option will be converted to a
         * &lt;code&gt;org.apache.camel.Predicate&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param predicateBean the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder predicateBean(
                String predicateBean) {
            doSetProperty("predicateBean", predicateBean);
            return this;
        }
        /**
         * The subscription priority.
         * 
         * The option is a: &lt;code&gt;java.lang.Integer&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param priority the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder priority(Integer priority) {
            doSetProperty("priority", priority);
            return this;
        }
        /**
         * The subscription priority.
         * 
         * The option will be converted to a
         * &lt;code&gt;java.lang.Integer&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param priority the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder priority(String priority) {
            doSetProperty("priority", priority);
            return this;
        }
        /**
         * The channel to subscribe to.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param subscribeChannel the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder subscribeChannel(
                String subscribeChannel) {
            doSetProperty("subscribeChannel", subscribeChannel);
            return this;
        }
        /**
         * The subscription ID; if unspecified, one will be assigned and
         * returned.
         * 
         * The option is a: &lt;code&gt;java.lang.String&lt;/code&gt; type.
         * 
         * Group: control
         * 
         * @param subscriptionId the value to set
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder subscriptionId(
                String subscriptionId) {
            doSetProperty("subscriptionId", subscriptionId);
            return this;
        }
    }

    /**
     * Advanced builder for endpoint for the Dynamic Router Control component.
     */
    public interface AdvancedDynamicRouterControlEndpointBuilder
            extends
                EndpointProducerBuilder {
        default DynamicRouterControlEndpointBuilder basic() {
            return (DynamicRouterControlEndpointBuilder) this;
        }
        /**
         * Whether the producer should be started lazy (on the first message).
         * By starting lazy you can use this to allow CamelContext and routes to
         * startup in situations where a producer may otherwise fail during
         * starting and cause the route to fail being started. By deferring this
         * startup to be lazy then the startup failure can be handled during
         * routing messages via Camel's routing error handlers. Beware that when
         * the first message is processed then creating and starting the
         * producer may take a little time and prolong the total processing time
         * of the processing.
         * 
         * The option is a: &lt;code&gt;boolean&lt;/code&gt; type.
         * 
         * Default: false
         * Group: producer (advanced)
         * 
         * @param lazyStartProducer the value to set
         * @return the dsl builder
         */
        default AdvancedDynamicRouterControlEndpointBuilder lazyStartProducer(
                boolean lazyStartProducer) {
            doSetProperty("lazyStartProducer", lazyStartProducer);
            return this;
        }
        /**
         * Whether the producer should be started lazy (on the first message).
         * By starting lazy you can use this to allow CamelContext and routes to
         * startup in situations where a producer may otherwise fail during
         * starting and cause the route to fail being started. By deferring this
         * startup to be lazy then the startup failure can be handled during
         * routing messages via Camel's routing error handlers. Beware that when
         * the first message is processed then creating and starting the
         * producer may take a little time and prolong the total processing time
         * of the processing.
         * 
         * The option will be converted to a &lt;code&gt;boolean&lt;/code&gt;
         * type.
         * 
         * Default: false
         * Group: producer (advanced)
         * 
         * @param lazyStartProducer the value to set
         * @return the dsl builder
         */
        default AdvancedDynamicRouterControlEndpointBuilder lazyStartProducer(
                String lazyStartProducer) {
            doSetProperty("lazyStartProducer", lazyStartProducer);
            return this;
        }
    }

    public interface DynamicRouterControlBuilders {
        /**
         * Dynamic Router Control (camel-dynamic-router)
         * The Dynamic Router control endpoint for operations that allow routing
         * participants to subscribe or unsubscribe to participate in dynamic
         * message routing.
         * 
         * Category: messaging
         * Since: 4.3
         * Maven coordinates: org.apache.camel:camel-dynamic-router
         * 
         * @return the dsl builder for the headers' name.
         */
        default DynamicRouterControlHeaderNameBuilder dynamicRouterControl() {
            return DynamicRouterControlHeaderNameBuilder.INSTANCE;
        }
        /**
         * Dynamic Router Control (camel-dynamic-router)
         * The Dynamic Router control endpoint for operations that allow routing
         * participants to subscribe or unsubscribe to participate in dynamic
         * message routing.
         * 
         * Category: messaging
         * Since: 4.3
         * Maven coordinates: org.apache.camel:camel-dynamic-router
         * 
         * Syntax: <code>dynamic-router-control:controlAction</code>
         * 
         * Path parameter: controlAction (required)
         * Control action
         * There are 3 enums and the value can be one of: subscribe,
         * unsubscribe, list
         * 
         * @param path controlAction
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder dynamicRouterControl(
                String path) {
            return DynamicRouterControlEndpointBuilderFactory.endpointBuilder("dynamic-router-control", path);
        }
        /**
         * Dynamic Router Control (camel-dynamic-router)
         * The Dynamic Router control endpoint for operations that allow routing
         * participants to subscribe or unsubscribe to participate in dynamic
         * message routing.
         * 
         * Category: messaging
         * Since: 4.3
         * Maven coordinates: org.apache.camel:camel-dynamic-router
         * 
         * Syntax: <code>dynamic-router-control:controlAction</code>
         * 
         * Path parameter: controlAction (required)
         * Control action
         * There are 3 enums and the value can be one of: subscribe,
         * unsubscribe, list
         * 
         * @param componentName to use a custom component name for the endpoint
         * instead of the default name
         * @param path controlAction
         * @return the dsl builder
         */
        default DynamicRouterControlEndpointBuilder dynamicRouterControl(
                String componentName,
                String path) {
            return DynamicRouterControlEndpointBuilderFactory.endpointBuilder(componentName, path);
        }
    }

    /**
     * The builder of headers' name for the Dynamic Router Control component.
     */
    public static class DynamicRouterControlHeaderNameBuilder {
        /**
         * The internal instance of the builder used to access to all the
         * methods representing the name of headers.
         */
        private static final DynamicRouterControlHeaderNameBuilder INSTANCE = new DynamicRouterControlHeaderNameBuilder();

        /**
         * The control action header.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterControlAction}.
         */
        public String dynamicRouterControlAction() {
            return "CamelDynamicRouterControlAction";
        }

        /**
         * The Dynamic Router channel that the subscriber is subscribing on.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterSubscribeChannel}.
         */
        public String dynamicRouterSubscribeChannel() {
            return "CamelDynamicRouterSubscribeChannel";
        }

        /**
         * The subscription ID.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterSubscriptionId}.
         */
        public String dynamicRouterSubscriptionId() {
            return "CamelDynamicRouterSubscriptionId";
        }

        /**
         * The URI on which the routing participant wants to receive matching
         * exchanges.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterDestinationUri}.
         */
        public String dynamicRouterDestinationUri() {
            return "CamelDynamicRouterDestinationUri";
        }

        /**
         * The priority of this subscription.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterPriority}.
         */
        public String dynamicRouterPriority() {
            return "CamelDynamicRouterPriority";
        }

        /**
         * The predicate to evaluate exchanges for this subscription.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterPredicate}.
         */
        public String dynamicRouterPredicate() {
            return "CamelDynamicRouterPredicate";
        }

        /**
         * The name of the bean in the registry that identifies the subscription
         * predicate.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code DynamicRouterPredicateBean}.
         */
        public String dynamicRouterPredicateBean() {
            return "CamelDynamicRouterPredicateBean";
        }

        /**
         * The language for the predicate when supplied as a string.
         * 
         * The option is a: {@code String} type.
         * 
         * Group: producer
         * 
         * @return the name of the header {@code
         * DynamicRouterExpressionLanguage}.
         */
        public String dynamicRouterExpressionLanguage() {
            return "CamelDynamicRouterExpressionLanguage";
        }
    }
    static DynamicRouterControlEndpointBuilder endpointBuilder(
            String componentName,
            String path) {
        class DynamicRouterControlEndpointBuilderImpl extends AbstractEndpointBuilder implements DynamicRouterControlEndpointBuilder, AdvancedDynamicRouterControlEndpointBuilder {
            public DynamicRouterControlEndpointBuilderImpl(String path) {
                super(componentName, path);
            }
        }
        return new DynamicRouterControlEndpointBuilderImpl(path);
    }
}