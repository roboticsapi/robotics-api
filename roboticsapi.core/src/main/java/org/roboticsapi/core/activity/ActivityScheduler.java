/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;

public class ActivityScheduler {

	/** The singleton instance */
	private static ActivityScheduler theInstance = new ActivityScheduler();
	private final Map<Device, ActivityResults> results = new HashMap<>();
	private final Map<Device, ActivityHandle> handles = new HashMap<>();

	public static ActivityScheduler getInstance() {
		return theInstance;
	}

	public ActivityResults getResults(Device device) {
		return results.get(device);
	}

	public void setResults(Device device, ActivityHandle handle, ActivityResults results) {
		this.results.put(device, results);
		this.handles.put(device, handle);
	}

	public void cancel(Actuator device) throws RoboticsException {
		ActivityHandle h = handles.get(device);
		if (h != null) {
			h.cancelExecute();
		}
	}

	public void endExecute(Actuator device) throws RoboticsException {
		ActivityHandle h = handles.get(device);
		if (h != null) {
			h.endExecute();
		}
	}

}
