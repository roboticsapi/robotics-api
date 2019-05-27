/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.sensor.DoubleSensor;

public class OverrideParameter implements DeviceParameters {

	private final DoubleSensor override;
	private final Scaling scaling;

	public enum Scaling {
		RELATIVE, ABSOLUTE
	}

	public OverrideParameter(double override, Scaling scaling) {
		this(DoubleSensor.fromValue(override), scaling);
	}

	public OverrideParameter(DoubleSensor override, Scaling scaling) {
		if (override == null) {
			throw new IllegalArgumentException("DoubleSensor override must not be null.");
		}

		this.override = override;
		this.scaling = scaling;
	}

	public DoubleSensor getOverride() {
		return override;
	}

	public Scaling getScaling() {
		return scaling;
	}

	public static OverrideParameter MAXIMUM = new OverrideParameter(1, Scaling.ABSOLUTE);

	@Override
	public boolean respectsBounds(DeviceParameters boundingObject) {
		if (!(boundingObject instanceof OverrideParameter)) {
			throw new IllegalArgumentException("Argument must be of type " + getClass().getName());
		}

		OverrideParameter object = (OverrideParameter) boundingObject;

		if (object.getScaling() != getScaling()) {
			return true;
		}

		if (object.getOverride().getCheapValue() != null && getOverride().getCheapValue() != null
				&& object.getOverride().getCheapValue() <= getOverride().getCheapValue()) {
			return false;
		}

		return true;
	}

}
