/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JOTG2 extends JPrimitive {

	public JInPort<RPIdouble> inCurPos = add("inCurPos", new JInPort<RPIdouble>());
	public JInPort<RPIdouble> inCurVel = add("inCurVel", new JInPort<RPIdouble>());
	public JInPort<RPIdouble> inDesPos = add("inDesPos", new JInPort<RPIdouble>());
	public JInPort<RPIdouble> inMaxVel = add("inMaxVel", new JInPort<RPIdouble>());
	public JInPort<RPIdouble> inMaxAcc = add("inMaxAcc", new JInPort<RPIdouble>());

	JOutPort<RPIdouble> outPos = add("outPos", new JOutPort<RPIdouble>());
	JOutPort<RPIdouble> outVel = add("outVel", new JOutPort<RPIdouble>());
	JOutPort<RPIdouble> outAcc = add("outAcc", new JOutPort<RPIdouble>());

	JParameter<RPIdouble> propMaxVel = add("maxVel", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propMaxAcc = add("maxAcc", new JParameter<RPIdouble>());

	double curPos = 0, curVel = 0;
	double desPos = 0, desVel = 0;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inDesPos);
	}

	@Override
	public void updateData() {
		if (anyNull(inDesPos))
			return;
		double samplingTime = getNet().getCycleTime();
		double maxVelocity = inMaxVel.get(propMaxVel).get();
		double maxAcceleration = inMaxAcc.get(propMaxAcc).get();

		if (maxAcceleration > 2 * maxVelocity / samplingTime) {
			maxAcceleration = 2 * maxVelocity / samplingTime;
		}

		double newPos, newVel, acc;

		if (inCurPos.isConnected()) {
			curPos = inCurPos.get().get(); // q_(k-1)
		}

		if (inCurVel.isConnected()) {
			curVel = inCurVel.get().get(); // q'_(k-1)
		}

		double lastPos = desPos;

		desPos = inDesPos.get().get(); // r_(k-1)
		desVel = (desPos - lastPos) / samplingTime; // r'_(k-1)

		acc = calcAcceleration(desPos, desVel, curPos, curVel, maxVelocity, maxAcceleration, samplingTime); // u_(k-1)
		newVel = calcVelocity(curVel, acc, samplingTime); // q'_k
		newPos = calcPosition(curPos, newVel, curVel, samplingTime); // q_k

		// System.out.println(curPos + " " + curVel + " " + acc);

		outPos.set(new RPIdouble(newPos));
		outVel.set(new RPIdouble(newVel));
		outAcc.set(new RPIdouble(acc));

		curPos = newPos;
		curVel = newVel;
	}

	static double calcAcceleration(double desPos, double desVel, double curPos, double curVel, double maxVel,
			double maxAcc, double t) {
		double ek = (curPos - desPos) / maxAcc;
		double ek2 = (curVel - desVel) / maxAcc;

		double zk = (1 / t) * ((ek / t) + (ek2 / 2));
		double zk2 = ek2 / t;

		double m = Math.floor((1 + Math.sqrt(1 + 8 * Math.abs(zk))) / 2);
		double sigma = zk2 + zk / m + ((m - 1) / 2) * sign(zk);

		curVel = curVel * sign(sigma);
		double step = curVel + maxVel - t * maxAcc;

		if (step < 0) {
			maxAcc = (maxVel + curVel) / t;
		}
		return (-maxAcc) * sat(sigma); // * (1 + sign(step)) / 2;
	}

	static double calcVelocity(double curVel, double acc, double t) {
		return curVel + t * acc;
	}

	static double calcPosition(double curPos, double newVel, double curVel, double t) {
		return curPos + t / 2 * (newVel + curVel);
	}

	static double sat(double x) {
		if (x < -1)
			return -1;
		else if (x > 1)
			return 1;
		else
			return x;
	}

	static int sign(double x) {
		if (x < 0)
			return -1;
		else
			return 1;
	}

}
