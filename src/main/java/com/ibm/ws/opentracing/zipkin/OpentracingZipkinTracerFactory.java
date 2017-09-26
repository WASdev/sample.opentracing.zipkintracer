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

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.opentracing.tracer.OpentracingTracerFactory;

import io.opentracing.Tracer;

/**
 * Factory for delivering Opentracing Tracers backed by Zipkin implementation.
 */

//Annotation is used for the creation of the OSGI bundle manfiest information.
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = "opentracingZipkin")
public class OpentracingZipkinTracerFactory implements OpentracingTracerFactory {
	private final String HOST_PROPERTY_NAME = "host";
	private final String PORT_PROPERTY_NAME = "port";
	private final String HOST_DEFAULT_VALUE = "zipkin";
	private final String PORT_DEFAULT_VALUE = "9411";
	
	String zipkinHost;
	String zipkinPort;

	@Activate
	protected void activate(ComponentContext ctx, Map<String, Object> config) {
		modified(ctx, config);
	}

	@Modified
	protected void modified(ComponentContext ctx, Map<String, Object> config) {
		zipkinHost = (String) config.get(HOST_PROPERTY_NAME);
		zipkinPort = (String) config.get(PORT_PROPERTY_NAME);
		if (zipkinHost == null) {
			zipkinHost = HOST_DEFAULT_VALUE;
		}
		if (zipkinPort == null) {
			zipkinPort = PORT_DEFAULT_VALUE;
		}
	}

	/** {@inheritDoc} */
	public Tracer newInstance(String serviceName) {
		return new OpentracingZipkinTracer(serviceName, zipkinHost, zipkinPort);
	}





}
