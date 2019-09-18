/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableQuaternion;
import org.roboticsapi.core.world.mutable.MutableRotation;
import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

public class JRotationBezier extends JPrimitive {
	private JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());
	private JInPort<RPIRotation> inFrom = add("inFrom", new JInPort<RPIRotation>());
	private JInPort<RPIVector> inFromVel = add("inFromVel", new JInPort<RPIVector>());
	private JInPort<RPIVector> inToVel = add("inToVel", new JInPort<RPIVector>());
	private JInPort<RPIRotation> inTo = add("inTo", new JInPort<RPIRotation>());
	private JOutPort<RPIRotation> outValue = add("outValue", new JOutPort<RPIRotation>());

	private MutableQuaternion fr = RPICalc.quaternionCreate();
	private MutableQuaternion tr = RPICalc.quaternionCreate();
	private MutableQuaternion fp = RPICalc.quaternionCreate();
	private MutableQuaternion tm = RPICalc.quaternionCreate();
	private double[] val = new double[4];
	private MutableRotation from = RPICalc.rotationCreate();
	private MutableRotation fromPlus = RPICalc.rotationCreate();
	private MutableRotation toMinus = RPICalc.rotationCreate();
	private MutableRotation to = RPICalc.rotationCreate();
	private MutableRotation rot = RPICalc.rotationCreate();
	private RPIRotation value = RPICalc.rpiRotationCreate();
	private MutableVector fromVel = RPICalc.vectorCreate();
	private MutableVector toVel = RPICalc.vectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inFrom, inFromVel, inToVel, inTo);
	}

	private double get(MutableQuaternion q, int index) {
		switch (index) {
		case 0:
			return q.getX();
		case 1:
			return q.getY();
		case 2:
			return q.getZ();
		case 3:
			return q.getW();
		}
		return 0;
	}

	@Override
	public void updateData() {
		if (anyNull(inFrom, inTo, inFromVel, inToVel))
			return;
		RPICalc.rpiToRotation(inFrom.get(), from);
		RPICalc.rpiToRotation(inTo.get(), to);

		RPICalc.rpiToRotation(inFrom.get(), from);
		RPICalc.rpiToRotation(inFrom.get(), from);
		RPICalc.rpiToVector(inFromVel.get(), fromVel);
		RPICalc.rpiToVector(inToVel.get(), toVel);
		from.addDeltaTo(fromVel, 1.0 / 3, fromPlus);
		to.addDeltaTo(toVel, -1.0 / 3, toMinus);

		from.getQuaternionTo(fr);
		fromPlus.getQuaternionTo(fp);
		toMinus.getQuaternionTo(tm);
		to.getQuaternionTo(tr);

		double t = inValue.get().get(), len = 0;
		for (int i = 0; i < 4; i++) {

			double a = get(fr, i);
			double b = -3 * get(fr, i) + 3 * get(fp, i);
			double c = 3 * get(fr, i) - 6 * get(fp, i) + 3 * get(tm, i);
			double d = -get(fr, i) + 3 * get(fp, i) - 3 * get(tm, i) + get(tr, i);

			val[i] = a + t * (b + t * (c + d * t));
			len += val[i] * val[i];
		}
		len = Math.sqrt(len);
		rot.setQuaternion(val[0] / len, val[1] / len, val[2] / len, val[3] / len);
		RPICalc.rotationToRpi(rot, value);
		outValue.set(value);
	}

}
