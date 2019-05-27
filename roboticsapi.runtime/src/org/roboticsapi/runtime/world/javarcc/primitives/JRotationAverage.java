/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a rotation sliding average filter
 */
public class JRotationAverage extends JPrimitive {
	private JInPort<RPIRotation> inValue = add("inValue", new JInPort<RPIRotation>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());
	private JParameter<RPIdouble> propDuration = add("Duration", new JParameter<RPIdouble>());

	private MutableRotation[] history;
	private int pos = -1, len = 0;
	private RPIRotation value = RPICalc.rpiRotationCreate();
	private MutableRotation rot = RPICalc.rotationCreate();
	private MutableRotation rotInv = RPICalc.rotationCreate();
	private MutableVector axis = RPICalc.vectorCreate();
	private MutableVector sum = RPICalc.vectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
		len = (int) (propDuration.get().get() / getNet().getCycleTime());
		history = new MutableRotation[len];
		for (int i = 0; i < len; i++)
			history[i] = new MutableRotation();
		pos = -1;
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		pos++;
		MutableRotation hist = history[pos % len];
		RPICalc.rpiToRotation(inValue.get(), hist);
		hist.invertTo(rotInv);
		sum.set(0, 0, 0);
		for (int i = Math.max(0, pos - len + 1); i <= pos; i++) {
			rotInv.multiplyTo(history[i % len], rot);
			rot.getAxisTo(axis);
			sum.add(axis);
		}
		sum.scale(1 / Math.min(len, pos + 1));
		rot.setAxis(sum);
		hist.multiplyTo(rot, rot);
		RPICalc.rotationToRpi(rot, value);
		outValue.set(value);
	}

}
