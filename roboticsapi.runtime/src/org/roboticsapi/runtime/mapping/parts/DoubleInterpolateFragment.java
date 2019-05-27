/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleEquals;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.core.primitives.Lerp;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ActiveNetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class DoubleInterpolateFragment extends ActiveNetFragment {

	private final DataflowOutPort interpolatedValue;
	private final DataflowOutPort donePort;

	public DoubleInterpolateFragment(String name, DataflowOutPort active, double initialValue, double goal,
			double epsilon, double velocity) throws MappingException {
		super("DoubleInterpolate<" + name + ">", active);

		double x = Math.abs(goal - initialValue);

		Clock clock = add(new Clock());
		Interval interval = add(new Interval(0.0, x / velocity));
		Lerp lerp = add(new Lerp(initialValue, goal));

		DoubleEquals comparer = add(new DoubleEquals(initialValue, goal, epsilon));

		connect(clock.getOutValue(), interval.getInValue());
		connect(interval.getOutValue(), lerp.getInAmount());
		connect(lerp.getOutValue(), comparer.getInFirst());

		interpolatedValue = addOutPort(new DoubleDataflow(), true, lerp.getOutValue());

		donePort = addOutPort(new StateDataflow(), true, comparer.getOutValue());

	}

	public DataflowOutPort getInterpolatedValue() {
		return interpolatedValue;
	}

	public DataflowOutPort getDonePort() {
		return donePort;
	}
}
