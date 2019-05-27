/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImmediateFuture<T> implements Future<T> {

	private final T value;

	public ImmediateFuture(T value) {
		this.value = value;

	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return true;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return value;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return get();
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImmediateFuture<?>)) {
			return false;
		}
		return ((ImmediateFuture<?>) obj).value.equals(value);

	}

}
