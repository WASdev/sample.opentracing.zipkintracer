/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.opentracing.zipkin;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.ActiveSpan;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * 
 *
 */
public class OpentracingZipkinTracer implements Tracer {
	Tracer tracer;

	/**
	 * @param serviceName
	 * @param zipkinHost
	 * @param zipkinPort
	 */
	public OpentracingZipkinTracer(String serviceName, String zipkinHost, String zipkinPort) {
		String traceServiceUrl = "http://"+zipkinHost+":"+zipkinPort+"/api/v1/spans";
		Sender sender = OkHttpSender.create(traceServiceUrl);
		Reporter<zipkin.Span> reporter = AsyncReporter.builder(sender).build();
		Tracing braveTracing = Tracing.newBuilder()
				.localServiceName(serviceName)
				.reporter(reporter)
				.build();
		tracer = BraveTracer.create(braveTracing);
	}

	/** {@inheritDoc} */
	public ActiveSpan activeSpan() {
		return tracer.activeSpan();
	}

	/** {@inheritDoc} */
	public ActiveSpan makeActive(Span arg0) {
		return tracer.makeActive(arg0);
	}

	/** {@inheritDoc} */
	public SpanBuilder buildSpan(String arg0) {
		return tracer.buildSpan(arg0);
	}

	/** {@inheritDoc} */
	public <C> SpanContext extract(Format<C> arg0, C arg1) {
		return tracer.extract(arg0, arg1);
	}

	/** {@inheritDoc} */
	public <C> void inject(SpanContext arg0, Format<C> arg1, C arg2) {
		tracer.inject(arg0, arg1, arg2);
	}
}
