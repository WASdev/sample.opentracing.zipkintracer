/*****************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *****************************************************************************/
package com.ibm.ws.opentracing.zipkin;

import java.util.concurrent.TimeUnit;

import com.ibm.ws.opentracing.zipkin.OpentracingZipkinTracerFactory.Config;

import brave.Tracing;
import brave.Tracing.Builder;
import brave.opentracing.BraveTracer;
import brave.propagation.CurrentTraceContext;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * This class wraps the Brave tracer to provide a generic tracer implementation
 * to the opentracing feature.
 *
 * In theory the Brave tracer could be replaced by another tracer
 * implementation.
 *
 */
public class OpentracingZipkinTracer implements Tracer {

    public static final boolean DEBUG = Boolean.getBoolean("opentracingDebug");

    Tracer tracer;

    /**
     * Creates and returns a tracer that is uses a serviceName and the location of a
     * zipkin host/port to provide tracing capability.
     *
     * @param serviceName Service Name
     * @param config      Configuration
     */
    public OpentracingZipkinTracer(String serviceName, Config config) {
        String traceServiceUrl = "http://" + config.host() + ":" + config.port() + "/api/v2/spans";

        OkHttpSender.Builder senderBuilder = OkHttpSender.newBuilder().endpoint(traceServiceUrl)
                .compressionEnabled(config.compress());
        if (config.maxRequests() != Integer.MIN_VALUE) {
            senderBuilder.maxRequests(config.maxRequests());
        }
        if (config.encoding() != null) {
            senderBuilder.encoding(config.encoding());
        }
        if (config.maxMessageSize() != Integer.MIN_VALUE) {
            senderBuilder.messageMaxBytes(config.maxMessageSize());
        }
        Sender sender = senderBuilder.build();
        AsyncReporter.Builder reporterBuilder = AsyncReporter.builder(sender);
        if (config.closeTimeout() != Integer.MIN_VALUE) {
            reporterBuilder.closeTimeout(config.closeTimeout(), TimeUnit.SECONDS);
        }
        if (config.messageTimeout() != Integer.MIN_VALUE) {
            reporterBuilder.messageTimeout(config.messageTimeout(), TimeUnit.SECONDS);
        }
        if (config.queuedMaxBytes() != Integer.MIN_VALUE) {
            reporterBuilder.queuedMaxBytes(config.queuedMaxBytes());
        }
        if (config.queuedMaxSpans() != Integer.MIN_VALUE) {
            reporterBuilder.queuedMaxSpans(config.queuedMaxSpans());
        }
        AsyncReporter<zipkin2.Span> reporter = reporterBuilder.build();
        Builder tracingBuilder = Tracing.newBuilder();
        tracingBuilder.currentTraceContext(CurrentTraceContext.Default.create());
        Tracing braveTracing = tracingBuilder.localServiceName(serviceName).spanReporter(reporter).build();
        tracer = BraveTracer.create(braveTracing);
        System.out.println("Created " + toString());
    }

    /** {@inheritDoc} */
    public SpanBuilder buildSpan(String operationName) {
        if (DEBUG) {
            System.out.println(toString() + " buildSpan: " + operationName);
        }
        SpanBuilder result = tracer.buildSpan(operationName);

        if (DEBUG) {
            System.out.println(toString() + " buildSpan: " + result);
        }
        return result;
    }

    /** {@inheritDoc} */
    public <C> SpanContext extract(Format<C> format, C carrier) {
        SpanContext result = tracer.extract(format, carrier);

        if (DEBUG) {
            System.out.println(toString() + " extract: " + result);
        }
        return result;
    }

    /** {@inheritDoc} */
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        if (DEBUG) {
            System.out.println(toString() + " inject spanContext: " + spanContext + ", format: " + format
                    + ", carrier: " + carrier);
        }
        tracer.inject(spanContext, format, carrier);
    }

    /** {@inheritDoc} */
    public Span activeSpan() {
        Span result = tracer.activeSpan();

        if (DEBUG) {
            System.out.println(toString() + " activeSpan: " + result);
        }
        return result;
    }

    /** {@inheritDoc} */
    public ScopeManager scopeManager() {
        ScopeManager result = tracer.scopeManager();

        if (DEBUG) {
            System.out.println(toString() + " scopeManager: " + result);
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + " { tracer: " + tracer + " }";
    }

    /** {@inheritDoc} */
    public Scope activateSpan(Span span) {
        Scope scope = tracer.activateSpan(span);

        if (DEBUG) {
            System.out.println(toString() + " activateSpan: " + scope);
        }
        return scope;
    }

    /** {@inheritDoc} */
    public void close() {
        if (DEBUG) {
            System.out.println(toString() + " close");
        }
        tracer.close();
        return;
    }
}
