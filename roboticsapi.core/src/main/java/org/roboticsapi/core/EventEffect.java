/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * An event handler execuing a {@link Runnable}
 */
public class EventEffect {
	private final Runnable runnable;
	private final boolean async;

	/**
	 * @return runnable to execute when the event occurs
	 */
	public Runnable getRunnable() {
		return runnable;
	}

	/**
	 * @return true if the {@link Runnable} should be executed asynchronously
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * Creates an EventEffect that executes a {@link Runnable}
	 *
	 * @param runnable {@link Runnable} to execute
	 * @param async    if set to true, the {@link Runnable} should be executed in a
	 *                 new thread
	 */
	public EventEffect(final Runnable runnable, final boolean async) {
		super();
		this.async = async;
		this.runnable = runnable;
	}

	/**
	 * Creates an EventEffect that executes a {@link Runnable} in a new thread
	 *
	 * @param runnable {@link Runnable} to execute in a new thrad
	 */
	public EventEffect(final Runnable thread) {
		this(thread, true);
	}

}