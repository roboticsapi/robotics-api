/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;

/**
 * Abstract implementation for a digital/analog input.
 *
 * @param <ID> type of the driver
 * @param <T>  type of the input
 */
public abstract class Input<ID extends InputDriver<T>, T> extends AbstractDevice<ID> {

	public final T getValue() throws RealtimeValueReadException {
		return getSensor().getCurrentValue();
	}

	public abstract RealtimeValue<T> getSensor();

	public abstract RealtimeValue<T> getSensor(RoboticsRuntime runtime);
}