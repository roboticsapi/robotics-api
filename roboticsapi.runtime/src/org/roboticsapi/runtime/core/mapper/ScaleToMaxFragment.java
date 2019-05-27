/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * Fragment that calculates a factor necessary to scale an input value to a
 * given maximum value.
 */
public class ScaleToMaxFragment extends NetFragment {

	private final DataflowOutPort outScalingFactor;

	/**
	 * Instantiates a new scale to max fragment.
	 * 
	 * @param maxValue the absolute maximum value
	 * @param value    the value
	 * @throws MappingException
	 */
	public ScaleToMaxFragment(double maxValue, double minValue, DataflowOutPort value) throws MappingException {
		super("Scale to maximum");
		try {
			outScalingFactor = createFragment(maxValue, minValue, null, null, value);
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	public ScaleToMaxFragment(DataflowOutPort maxValue, DataflowOutPort minValue, DataflowOutPort value)
			throws MappingException {
		super("Scale to maximum");
		try {
			outScalingFactor = createFragment(0, 0, maxValue, minValue, value);
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	private DataflowOutPort createFragment(double maxValue, double minValue, DataflowOutPort maxValuePort,
			DataflowOutPort minValuePort, DataflowOutPort value) throws RPIException, MappingException {

		DoubleGreater greater = add(new DoubleGreater());
		if (maxValuePort == null) {
			greater.setSecond(maxValue);
		} else {
			connect(maxValuePort, addInPort(new DoubleDataflow(), true, greater.getInSecond()));
		}
		connect(value, addInPort(new DoubleDataflow(), true, greater.getInFirst()));

		DoubleConditional ifGreater = add(new DoubleConditional());
		ifGreater.getInCondition().connectTo(greater.getOutValue());

		DoubleDivide divGreater = add(new DoubleDivide());
		if (maxValuePort == null) {
			divGreater.setFirst(maxValue);
		} else {
			connect(maxValuePort, addInPort(new DoubleDataflow(), true, divGreater.getInFirst()));
		}
		connect(value, addInPort(new DoubleDataflow(), true, divGreater.getInSecond()));

		ifGreater.getInTrue().connectTo(divGreater.getOutValue());

		DoubleGreater smaller = add(new DoubleGreater());
		if (minValuePort == null) {
			smaller.setFirst(minValue);
		} else {
			connect(minValuePort, addInPort(new DoubleDataflow(), true, smaller.getInFirst()));
		}
		connect(value, addInPort(new DoubleDataflow(), true, smaller.getInSecond()));

		DoubleConditional ifSmaller = add(new DoubleConditional());
		ifSmaller.getInCondition().connectTo(smaller.getOutValue());

		DoubleDivide divSmaller = add(new DoubleDivide());
		if (minValuePort == null) {
			divSmaller.setFirst(minValue);
		} else {
			connect(minValuePort, addInPort(new DoubleDataflow(), true, divSmaller.getInFirst()));
		}
		connect(value, addInPort(new DoubleDataflow(), true, divSmaller.getInSecond()));

		ifSmaller.getInTrue().connectTo(divSmaller.getOutValue());

		ifSmaller.setFalse(1d);

		ifGreater.getInFalse().connectTo(ifSmaller.getOutValue());

		return addOutPort(new DoubleDataflow(), true, ifGreater.getOutValue());
	}

	public DataflowOutPort getOutScalingFactor() {
		return outScalingFactor;
	}

}
