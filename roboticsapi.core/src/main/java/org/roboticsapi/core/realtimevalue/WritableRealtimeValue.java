/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;

/**
 * Interface for all RealtimeValues whose values may be set by Robotics API
 * applications.
 *
 * @param <T> the type of data that can be written
 */
public interface WritableRealtimeValue<T> {

	/**
	 * Sets a new value for this {@link WritableRealtimeValue}.
	 *
	 * Altered values are propagated to any {@link RoboticsRuntime} that currently
	 * uses the RealtimeValue, e.g. as part of running {@link Command}s. However, no
	 * timing guarantees are given for the propagation.
	 *
	 * @param value the new value
	 */
	public void setValue(T value);

	/**
	 * Gets the value last set for this {@link WritableRealtimeValue}, or the
	 * default value if no other value has been set.
	 *
	 * @return the last set value
	 */
	public T getValue();
}
