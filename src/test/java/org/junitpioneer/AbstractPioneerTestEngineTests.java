/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junitpioneer;

import static java.util.Arrays.stream;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.jupiter.engine.AbstractJupiterTestEngineTests;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.test.event.ExecutionEventRecorder;

public abstract class AbstractPioneerTestEngineTests extends AbstractJupiterTestEngineTests {

	protected ExecutionEventRecorder executeTests(Class<?> type, String... methodNames) {
		if (methodNames.length == 0)
			return executeTestClass(type);
		else
			return executeTestMethods(type, methodNames);
	}

	private ExecutionEventRecorder executeTestClass(Class<?> type) {
		var request = request().selectors(selectClass(type)).build();
		return executeTests(request);
	}

	private ExecutionEventRecorder executeTestMethods(Class<?> type, String[] methodSignatures) {
		var selectors = stream(methodSignatures)
				.map(methodSignature -> selectMethodWithPossibleParameters(type, methodSignature))
				.toArray(DiscoverySelector[]::new);
		var request = request().selectors(selectors).build();
		return executeTests(request);
	}

	private MethodSelector selectMethodWithPossibleParameters(Class<?> type, String methodSignature) {
		int open = methodSignature.indexOf('(');
		int close = methodSignature.indexOf(')');
		var hasValidParameters = 0 < open && open < close && close == methodSignature.length() - 1;

		if (hasValidParameters) {
			var methodName = methodSignature.substring(0, open);
			var methodParameters = methodSignature.substring(open + 1, close);
			return selectMethod(type, methodName, methodParameters);
		} else {
			return selectMethod(type, methodSignature);
		}
	}

}
