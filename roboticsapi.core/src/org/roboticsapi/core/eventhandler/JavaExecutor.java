/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

/**
 * An event handler starting a non-realtime thread
 */
public class JavaExecutor extends JavaEffect {
	private final Runnable runnable;
	private final boolean async;

	public Runnable getRunnable() {
		return runnable;
	}

	public JavaExecutor(final Runnable runnable, final boolean async) {
		super();
		this.async = async;
		this.runnable = runnable;
	}

	public JavaExecutor(final Runnable thread) {
		this(thread, true);
	}

	public boolean isAsync() {
		return async;
	}
}
