/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.runtime.CommandRealtimeException;

public class ExceptionHandler<T extends CommandRealtimeException> {
	private final Class<T> exceptionType;
	private final EventEffect effect;
	private final boolean handled;

	public ExceptionHandler(Class<T> exceptionType, EventEffect effect, boolean handled) {
		this.exceptionType = exceptionType;
		this.effect = effect;
		this.handled = handled;

	}

	public Class<T> getExceptionType() {
		return exceptionType;
	}

	public EventEffect getEffect() {
		return effect;
	}

	public boolean isHandled() {
		return handled;
	}
}
