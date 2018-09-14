/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import com.ibm.ws.opentracing.tracer.OpentracingTracerFactory;

import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;
import io.opentracing.Tracer;
import zipkin2.codec.Encoding;

/**
 * Factory for delivering Opentracing Tracers backed by Zipkin implementation.
 */

//Annotation is used for the creation of the OSGI bundle manfiest information.
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL, configurationPid = "opentracingZipkin")
public class OpentracingZipkinTracerFactory implements OpentracingTracerFactory {
	private Config config;
	
	// http://enroute.osgi.org/services/org.osgi.service.component.html
	// http://bnd.bndtools.org/chapters/210-metatype.html
	@Meta.OCD(name = "%zipkin.config.name", localization = "OSGI-INF/l10n/metatype")
	@interface Config {
		@Meta.AD(required = false, deflt = "zipkin", description = "%zipkin.host.description")
		String host();

		@Meta.AD(required = false, deflt = "9411", description = "%zipkin.port.description")
		int port();

		@Meta.AD(required = false, description = "%zipkin.encoding.description")
		Encoding encoding();

		@Meta.AD(required = false, deflt = "true", description = "%zipkin.compress.description")
		boolean compress();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.maxRequests.description")
		int maxRequests();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.maxMessageSize.description")
		int maxMessageSize();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.closeTimeout.description")
		int closeTimeout();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.messageTimeout.description")
		int messageTimeout();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.queuedMaxBytes.description")
		int queuedMaxBytes();

		@Meta.AD(required = false, deflt = Integer.MIN_VALUE + "", description = "%zipkin.queuedMaxSpans.description")
		int queuedMaxSpans();
	}

	@Activate
	protected void activate(Map<String, Object> map) {
		modified(map);
	}

	@Modified
	protected void modified(Map<String, Object> map) {
		this.config = Configurable.createConfigurable(Config.class, map);
	}

	/** {@inheritDoc} */
	public Tracer newInstance(String serviceName) {
		return new OpentracingZipkinTracer(serviceName, config);
	}
}
