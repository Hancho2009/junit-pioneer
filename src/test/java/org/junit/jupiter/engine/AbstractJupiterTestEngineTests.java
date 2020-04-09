/*
 * Copyright 2015-2020 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.engine;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.test.event.ExecutionEventRecorder;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

/**
 * Abstract base class for tests involving the {@link JupiterTestEngine}.
 *
 * @since 5.0
 */
public abstract class AbstractJupiterTestEngineTests {

	private final JupiterTestEngine engine = new JupiterTestEngine();

	protected ExecutionEventRecorder executeTestsForClass(Class<?> testClass) {
		return executeTests(request().selectors(selectClass(testClass)).build());
	}

	protected ExecutionEventRecorder executeTestsForMethod(Class<?> testClass, String methodName) {
		return executeTests(request().selectors(selectMethod(testClass.getName(), methodName)).build());
	}

	protected ExecutionEventRecorder executeTests(LauncherDiscoveryRequest request) {
		var testDescriptor = discoverTests(request);
		var eventRecorder = new ExecutionEventRecorder();
		engine.execute(new ExecutionRequest(testDescriptor, eventRecorder, request.getConfigurationParameters()));
		return eventRecorder;
	}

	protected Throwable getFirstFailuresThrowable(ExecutionEventRecorder recorder) {
		return recorder
				.getFailedTestFinishedEvents()
				.get(0)
				.getPayload(TestExecutionResult.class)
				.flatMap(TestExecutionResult::getThrowable)
				.orElseThrow(AssertionError::new);
	}

	protected TestDescriptor discoverTests(LauncherDiscoveryRequest request) {
		return engine.discover(request, UniqueId.forEngine(engine.getId()));
	}

}
