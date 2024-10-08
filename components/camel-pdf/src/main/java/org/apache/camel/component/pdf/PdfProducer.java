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
package org.apache.camel.component.pdf;

import java.io.*;
import java.util.Collection;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.component.pdf.text.AutoFormattedWriterAbstractFactory;
import org.apache.camel.component.pdf.text.LineBuilderStrategy;
import org.apache.camel.component.pdf.text.LineTerminationWriterAbstractFactory;
import org.apache.camel.component.pdf.text.SplitStrategy;
import org.apache.camel.component.pdf.text.TextProcessingAbstractFactory;
import org.apache.camel.component.pdf.text.WriteStrategy;
import org.apache.camel.support.DefaultProducer;
import org.apache.camel.support.ExchangeHelper;
import org.apache.pdfbox.io.RandomAccessStreamCacheImpl;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.component.pdf.PdfHeaderConstants.*;

public class PdfProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(PdfProducer.class);

    private final WriteStrategy writeStrategy;
    private final SplitStrategy splitStrategy;
    private final LineBuilderStrategy lineBuilderStrategy;
    private final PdfConfiguration pdfConfiguration;

    public PdfProducer(PdfEndpoint endpoint) {
        super(endpoint);
        this.pdfConfiguration = endpoint.getPdfConfiguration();
        TextProcessingAbstractFactory textProcessingFactory = createTextProcessingFactory(pdfConfiguration);
        this.writeStrategy = textProcessingFactory.createWriteStrategy();
        this.splitStrategy = textProcessingFactory.createSplitStrategy();
        this.lineBuilderStrategy = textProcessingFactory.createLineBuilderStrategy();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Object result;
        switch (pdfConfiguration.getOperation()) {
            case append:
                result = doAppend(exchange);
                break;
            case create:
                result = doCreate(exchange);
                break;
            case extractText:
                result = doExtractText(exchange);
                break;
            case merge:
                result = doMerge(exchange);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown operation %s", pdfConfiguration.getOperation()));
        }
        // propagate headers
        exchange.getMessage().setHeaders(exchange.getIn().getHeaders());
        // and set result
        exchange.getMessage().setBody(result);
    }

    private OutputStream doMerge(Exchange exchange) throws IOException, NoSuchHeaderException {
        LOG.debug("Got {} operation, going to merge multiple files into a single pdf document.",
                pdfConfiguration.getOperation());
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        List<File> files = ExchangeHelper.getMandatoryHeader(exchange, FILES_TO_MERGE_HEADER_NAME, List.class);
        if (files.size() < 2) {
            throw new IllegalArgumentException("Must provide at least 2 files to merge");
        }
        for (File file : files) {
            mergerUtility.addSource(file);
        }
        mergerUtility.setDestinationStream(new ByteArrayOutputStream());
        mergerUtility.mergeDocuments(RandomAccessStreamCacheImpl::new);
        return mergerUtility.getDestinationStream();
    }

    private Object doAppend(Exchange exchange) throws IOException {
        LOG.debug("Got {} operation, going to append text to provided pdf.", pdfConfiguration.getOperation());
        String body = exchange.getIn().getBody(String.class);
        try (PDDocument document = exchange.getIn().getHeader(PDF_DOCUMENT_HEADER_NAME, PDDocument.class)) {
            if (document == null) {
                throw new IllegalArgumentException(
                        String.format("%s header is expected for append operation",
                                PDF_DOCUMENT_HEADER_NAME));
            }

            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }

            ProtectionPolicy protectionPolicy = exchange.getIn().getHeader(
                    PROTECTION_POLICY_HEADER_NAME, ProtectionPolicy.class);

            appendToPdfDocument(body, document, protectionPolicy);
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream;
        }
    }

    private String doExtractText(Exchange exchange) throws IOException {
        LOG.debug("Got {} operation, going to extract text from provided pdf.", pdfConfiguration.getOperation());
        try (PDDocument document = exchange.getIn().getBody(PDDocument.class)) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            return pdfTextStripper.getText(document);
        }
    }

    private OutputStream doCreate(Exchange exchange) throws IOException {
        LOG.debug("Got {} operation, going to create and write provided string to pdf document.",
                pdfConfiguration.getOperation());
        String body = exchange.getIn().getBody(String.class);
        try (PDDocument document = new PDDocument()) {
            StandardProtectionPolicy protectionPolicy = exchange.getIn().getHeader(
                    PROTECTION_POLICY_HEADER_NAME, StandardProtectionPolicy.class);
            appendToPdfDocument(body, document, protectionPolicy);
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream;
        }
    }

    private void appendToPdfDocument(String text, PDDocument document, ProtectionPolicy protectionPolicy) throws IOException {
        Collection<String> words = splitStrategy.split(text);
        Collection<String> lines = lineBuilderStrategy.buildLines(words);
        writeStrategy.write(lines, document);
        if (protectionPolicy != null) {
            document.protect(protectionPolicy);
        }
    }

    private TextProcessingAbstractFactory createTextProcessingFactory(PdfConfiguration pdfConfiguration) {
        TextProcessingAbstractFactory result;
        switch (pdfConfiguration.getTextProcessingFactory()) {
            case autoFormatting:
                result = new AutoFormattedWriterAbstractFactory(pdfConfiguration);
                break;
            case lineTermination:
                result = new LineTerminationWriterAbstractFactory(pdfConfiguration);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Unknown text processing factory %s",
                                pdfConfiguration.getTextProcessingFactory()));
        }
        return result;
    }
}
