/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

/**
 * Parallelized runner based on JUnit's Parameterized runner.
 *
 * Based on a suggestion by Harald Wellmann:
 * http://hwellmann.blogspot.de/2009/12
 * /running-parameterized-junit-tests-in.html
 *
 */
public class ParallelParameterized extends Parameterized {
	private static class ThreadPoolScheduler implements RunnerScheduler {
		private final ExecutorService executor;

		public ThreadPoolScheduler() {
			String threads = System.getProperty("junit.parallel.threads", "16");
			int numThreads = Integer.parseInt(threads);
			executor = Executors.newFixedThreadPool(numThreads);
		}

		@Override
		public void finished() {
			executor.shutdown();
			try {
				executor.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException exc) {
				throw new RuntimeException(exc);
			}
		}

		@Override
		public void schedule(Runnable childStatement) {
			executor.submit(childStatement);
		}
	}

	public ParallelParameterized(Class klass) throws Throwable {
		super(klass);
		setScheduler(new ThreadPoolScheduler());
	}
}
