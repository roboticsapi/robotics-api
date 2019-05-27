/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public final class OrBooleanSensor extends BooleanSensor {

	private final List<BooleanSensor> sensors = new Vector<BooleanSensor>();

	protected OrBooleanSensor(BooleanSensor[] sensors) {
		super(selectRuntime(sensors));
		addInnerSensors(sensors);
		this.sensors.addAll(Arrays.asList(sensors));
	}

	public OrBooleanSensor(BooleanSensor first, BooleanSensor... further) {
		this(combineSensors(first, further));
	}

	private static BooleanSensor[] combineSensors(BooleanSensor first, BooleanSensor... further) {
		BooleanSensor[] ret = new BooleanSensor[further == null ? 1 : further.length + 1];
		ret[0] = first;
		if (further != null) {
			System.arraycopy(further, 0, ret, 1, further.length);
		}
		return ret;
	}

	@Override
	protected Boolean calculateCheapValue() {
		boolean value = false;

		for (BooleanSensor s : getInnerSensors()) {
			Boolean cheapValue = s.getCheapValue();
			if (cheapValue == null) {
				return null;
			}
			value |= cheapValue;
		}

		return value;
	}

	public List<BooleanSensor> getInnerSensors() {
		return sensors;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensors.equals(((OrBooleanSensor) obj).sensors);
	}

	@Override
	public int hashCode() {
		return classHash(sensors);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(sensors);
	}

	@Override
	public String toString() {
		String ret = "";
		for (BooleanSensor sensor : sensors) {
			ret += sensor + " | ";
		}
		if (!sensors.isEmpty()) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "(" + ret + ")";
	}

}
