/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.world.sensor.TransformationSensor;

public final class TransformationDataflowSensor extends TransformationSensor {

	private final DataflowOutPort port;

	public TransformationDataflowSensor(DataflowOutPort port, RoboticsRuntime runtime) {
		super(runtime);
		this.port = port;
	}

	public DataflowOutPort getTransformationDataflowPort() {
		return port;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && port.equals(((TransformationDataflowSensor) obj).port);
	}

	@Override
	public int hashCode() {
		return classHash(port);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "TransformationDataflow(" + port + ")";
	}

}