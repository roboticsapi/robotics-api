/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;

/**
 * A dataflow sensor representing a dataflow out port. Note: This sensor is not
 * mapped, populate the sensor mapping context yourself!
 */
public final class DoubleDataflowSensor extends DoubleSensor {

	private final DataflowOutPort dataflow;

	public DoubleDataflowSensor(DataflowOutPort dataflow, AbstractMapperRuntime runtime) {
		super(runtime);
		this.dataflow = dataflow;
	}

	public DataflowOutPort getDataflow() {
		return dataflow;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && ((DoubleDataflowSensor) obj).dataflow.equals(dataflow);
	}

	@Override
	public int hashCode() {
		return classHash(dataflow);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "DoubleDataflow(" + dataflow + ")";
	}

}
