/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements vector sliding average filter
 */
public class JVectorAverage extends JPrimitive {
	private JInPort<RPIVector> inValue = add("inValue", new JInPort<RPIVector>());
	private JInPort<RPIbool> inReset = add("inReset", new JInPort<RPIbool>());
	private JOutPort<RPIVector> outValue = add("outValue", new JOutPort<RPIVector>());
	private JParameter<RPIdouble> propDuration = add("Duration", new JParameter<RPIdouble>());

	private MutableVector[] history;
	private MutableVector sum = RPICalc.vectorCreate();
	private int len = 0;
	private int pos = -1;

	private MutableVector vec = RPICalc.vectorCreate();
	private RPIVector value = RPICalc.rpiVectorCreate();

	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
		len = (int) (propDuration.get().get() / getNet().getCycleTime());
		history = new MutableVector[len];
		for (int i = 0; i < len; i++)
			history[i] = new MutableVector();
		pos = -1;
	}

	@Override
	public void updateData() {
		if (inReset.get() != null && inReset.get().get()) {
			pos = -1;
			sum.set(0, 0, 0);
		}

		if (anyNull(inValue))
			return;
		MutableVector hist = history[pos % len];
		pos++;
		if (pos >= len) {
			hist.scale(-1);
			sum.add(hist);
		}
		RPICalc.rpiToVector(inValue.get(), hist);
		sum.add(hist);
		sum.scaleTo(1 / Math.min(len, pos + 1), vec);
		RPICalc.vectorToRpi(vec, value);
		outValue.set(value);
	}

}
