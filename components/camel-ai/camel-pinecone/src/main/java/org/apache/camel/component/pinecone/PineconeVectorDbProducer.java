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
package org.apache.camel.component.pinecone;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.protobuf.Struct;
import io.pinecone.clients.Index;
import io.pinecone.clients.Pinecone;
import io.pinecone.proto.FetchResponse;
import io.pinecone.proto.UpdateResponse;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.unsigned_indices_model.QueryResponseWithUnsignedIndices;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.support.DefaultProducer;
import org.openapitools.db_control.client.model.CollectionModel;
import org.openapitools.db_control.client.model.DeletionProtection;
import org.openapitools.db_control.client.model.IndexModel;

public class PineconeVectorDbProducer extends DefaultProducer {
    private Pinecone client;
    private ExecutorService executor;

    public PineconeVectorDbProducer(PineconeVectorDbEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public PineconeVectorDbEndpoint getEndpoint() {
        return (PineconeVectorDbEndpoint) super.getEndpoint();
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();

        this.client = getEndpoint().getClient();
    }

    @Override
    public void process(Exchange exchange) {
        final Message in = exchange.getMessage();
        final PineconeVectorDbAction action = in.getHeader(PineconeVectorDb.Headers.ACTION, PineconeVectorDbAction.class);

        try {
            if (action == null) {
                throw new NoSuchHeaderException("The action is a required header", exchange, PineconeVectorDb.Headers.ACTION);
            }

            switch (action) {
                case CREATE_COLLECTION:
                    createCollection(exchange);
                    break;
                case CREATE_SERVERLESS_INDEX:
                    createServerlessIndex(exchange);
                    break;
                case CREATE_POD_INDEX:
                    createPodIndex(exchange);
                    break;
                case UPSERT:
                    upsert(exchange);
                    break;
                case UPDATE:
                    update(exchange);
                    break;
                case DELETE_INDEX:
                    deleteIndex(exchange);
                    break;
                case DELETE_COLLECTION:
                    deleteCollection(exchange);
                    break;
                case FETCH:
                    fetch(exchange);
                    break;
                case QUERY:
                    query(exchange);
                    break;
                case QUERY_BY_ID:
                    queryById(exchange);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported action: " + action.name());
            }
        } catch (Exception e) {
            exchange.setException(e);
        }
    }

    // ***************************************
    //
    // Actions
    //
    // ***************************************

    private void createCollection(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();

        String indexName = getEndpoint().getConfiguration().getIndexName();

        String collectionName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_NAME, String.class);
        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        CollectionModel result = this.client.createCollection(collectionName, indexName);

        populateCollectionResponse(result, exchange);

    }

    private void createServerlessIndex(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();

        String indexName = getEndpoint().getConfiguration().getIndexName();
        String collectionSimilarityMetricName = getEndpoint().getConfiguration().getCollectionSimilarityMetric();

        String collectionCloudName = getEndpoint().getConfiguration().getCloud();
        String collectionCloudRegionName = getEndpoint().getConfiguration().getCloudRegion();
        int collectionDimension = PineconeVectorDb.DEFAULT_COLLECTION_DIMENSION;

        if (getEndpoint().getConfiguration().getCollectionDimension() != null) {
            collectionDimension = getEndpoint().getConfiguration().getCollectionDimension().intValue();
        }

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, String.class) != null) {
            collectionSimilarityMetricName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, String.class);
        }

        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, Integer.class) != null) {
            collectionDimension = in.getHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, Integer.class);
        }
        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD, String.class) != null) {
            collectionCloudName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD, String.class);
        }

        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD_REGION, String.class) != null) {
            collectionCloudRegionName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD_REGION, String.class);
        }

        IndexModel result = this.client.createServerlessIndex(indexName, collectionSimilarityMetricName, collectionDimension,
                collectionCloudName, collectionCloudRegionName, DeletionProtection.DISABLED);

        populateIndexResponse(result, exchange);

    }

    private void createPodIndex(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        String indexName = getEndpoint().getConfiguration().getIndexName();
        String collectionSimilarityMetricName = getEndpoint().getConfiguration().getCollectionSimilarityMetric();
        int collectionDimension = PineconeVectorDb.DEFAULT_COLLECTION_DIMENSION;

        if (getEndpoint().getConfiguration().getCollectionDimension() != null) {
            collectionDimension = getEndpoint().getConfiguration().getCollectionDimension().intValue();
        }

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, String.class) != null) {
            collectionSimilarityMetricName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, String.class);
        }

        if (in.getHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, Integer.class) != null) {
            collectionDimension = in.getHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, Integer.class);
        }

        String indexPodType = in.getHeader(PineconeVectorDb.Headers.INDEX_POD_TYPE, String.class);
        String indexPodEnv = in.getHeader(PineconeVectorDb.Headers.INDEX_POD_ENVIRONMENT, String.class);

        IndexModel result = this.client.createPodsIndex(indexName, collectionDimension,
                indexPodEnv, indexPodType, collectionSimilarityMetricName);

        populateIndexResponse(result, exchange);

    }

    private void upsert(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        List elements = in.getMandatoryBody(List.class);
        String indexName = getEndpoint().getConfiguration().getIndexName();
        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }
        String indexId = in.getHeader(PineconeVectorDb.Headers.INDEX_ID, String.class);
        String namespace = in.getHeader(PineconeVectorDb.Headers.NAMESPACE, String.class);

        Index index = this.client.getIndexConnection(indexName);

        UpsertResponse result = index.upsert(indexId, elements, null, null, null, namespace);

        populateUpsertResponse(result, exchange);

    }

    private void update(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        List elements = in.getMandatoryBody(List.class);
        String indexName = getEndpoint().getConfiguration().getIndexName();

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }
        String indexId = in.getHeader(PineconeVectorDb.Headers.INDEX_ID, String.class);
        String namespace = in.getHeader(PineconeVectorDb.Headers.NAMESPACE, String.class);
        Index index = this.client.getIndexConnection(indexName);

        UpdateResponse result = index.update(indexId, elements, namespace);

        populateUpdateResponse(result, exchange);

    }

    private void deleteIndex(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        String indexName = getEndpoint().getConfiguration().getIndexName();

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }
        this.client.deleteIndex(indexName);
    }

    private void deleteCollection(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        String collectionName = in.getHeader(PineconeVectorDb.Headers.COLLECTION_NAME, String.class);
        this.client.deleteCollection(collectionName);
    }

    private void fetch(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        List elements = in.getMandatoryBody(List.class);
        String indexName = getEndpoint().getConfiguration().getIndexName();

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        Index index = this.client.getIndexConnection(indexName);

        FetchResponse result
                = index.fetch(elements);

        populateFetchResponse(result, exchange);
    }

    private void query(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();
        List elements = in.getMandatoryBody(List.class);
        String indexName = getEndpoint().getConfiguration().getIndexName();

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        int topK = in.getHeader(PineconeVectorDb.Headers.QUERY_TOP_K, Integer.class);
        Index index = this.client.getIndexConnection(indexName);

        // Optional arguments, can be null
        String namespace = in.getHeader(PineconeVectorDb.Headers.NAMESPACE, String.class);
        Struct filter = in.getHeader(PineconeVectorDb.Headers.QUERY_FILTER, Struct.class);
        boolean includeValues = (in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_VALUES, Boolean.class) == null)
                ? false : in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_VALUES, Boolean.class);
        boolean includeMetadata = (in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_METADATA, Boolean.class) == null)
                ? false : in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_METADATA, Boolean.class);

        QueryResponseWithUnsignedIndices result
                = index.queryByVector(topK, elements, namespace, filter, includeValues, includeMetadata);

        populateQueryResponse(result, exchange);
    }

    private void queryById(Exchange exchange) throws Exception {
        final Message in = exchange.getMessage();

        // Check to see if indexName is stored in the header, if it is not, check the configuration
        String indexName = getEndpoint().getConfiguration().getIndexName();

        if (in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class) != null) {
            indexName = in.getHeader(PineconeVectorDb.Headers.INDEX_NAME, String.class);
        }

        int topK = in.getHeader(PineconeVectorDb.Headers.QUERY_TOP_K, Integer.class);
        Index index = this.client.getIndexConnection(indexName);

        String indexId = in.getHeader(PineconeVectorDb.Headers.INDEX_ID, String.class);

        // Optional arguments, can be null
        String namespace = in.getHeader(PineconeVectorDb.Headers.NAMESPACE, String.class);
        Struct filter = in.getHeader(PineconeVectorDb.Headers.QUERY_FILTER, Struct.class);
        boolean includeValues = (in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_VALUES, Boolean.class) == null)
                ? false : in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_VALUES, Boolean.class);
        boolean includeMetadata = (in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_METADATA, Boolean.class) == null)
                ? false : in.getHeader(PineconeVectorDb.Headers.QUERY_INCLUDE_METADATA, Boolean.class);

        QueryResponseWithUnsignedIndices result
                = index.queryByVectorId(topK, indexId, namespace, filter, includeValues, includeMetadata);

        populateQueryResponse(result, exchange);
    }

    // ***************************************
    //
    // Helpers
    //
    // ***************************************

    private CamelContext getCamelContext() {
        return getEndpoint().getCamelContext();
    }

    private void populateCollectionResponse(CollectionModel r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }

    private void populateFetchResponse(FetchResponse r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }

    private void populateIndexResponse(IndexModel r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }

    private void populateUpsertResponse(UpsertResponse r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }

    private void populateUpdateResponse(UpdateResponse r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }

    private void populateQueryResponse(QueryResponseWithUnsignedIndices r, Exchange exchange) {
        Message out = exchange.getMessage();
        out.setBody(r);
    }
}
