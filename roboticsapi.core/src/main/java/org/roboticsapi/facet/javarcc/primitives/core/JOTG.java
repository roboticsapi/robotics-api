/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JOTG extends JPrimitive {

	private class OTGData {
		public double Acceleration;
		public double Velocity;
		public double Position;
	};

	JInPort<RPIdouble> inCurPos = add("inCurPos", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inCurVel = add("inCurVel", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inCurAcc = add("inCurAcc", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inDestPos = add("inDestPos", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inDestVel = add("inDestVel", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inMaxVel = add("inMaxVel", new JInPort<RPIdouble>());
	JInPort<RPIdouble> inMaxAcc = add("inMaxAcc", new JInPort<RPIdouble>());

	JOutPort<RPIdouble> outPos = add("outPos", new JOutPort<RPIdouble>());
	JOutPort<RPIdouble> outVel = add("outVel", new JOutPort<RPIdouble>());
	JOutPort<RPIdouble> outAcc = add("outAcc", new JOutPort<RPIdouble>());

	JParameter<RPIdouble> propMaxVel = add("maxVel", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propMaxAcc = add("maxAcc", new JParameter<RPIdouble>());

	double samplingTime;

	double maxAcceleration;
	double maxVelocity;

	// variables to store values of last cycle, if no monitor is available
	double lastPosition, lastVelocity;
	double lastCmdPosition;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inDestPos);
	}

	@Override
	public void updateData() {
		if (anyNull(inDestPos))
			return;
		samplingTime = getNet().getCycleTime();
		maxVelocity = inMaxVel.get(propMaxVel).get();
		maxAcceleration = inMaxAcc.get(propMaxAcc).get();

		// Workaround (aka Hack) to avoid the need of a robot monitor
		RPIdouble curposition;
		RPIdouble curvelocity = new RPIdouble(0.0), curacceleration = new RPIdouble(0.0);

		curposition = inCurPos.get();
		if (curposition == null) {
			curposition = new RPIdouble(lastCmdPosition);
		}

		curvelocity = inCurVel.get();
		if (curvelocity == null) {
			if (lastPosition == lastPosition)
				curvelocity = new RPIdouble((curposition.get() - lastPosition) / samplingTime);
			else
				curvelocity = new RPIdouble(0);
		}

		curacceleration = inCurAcc.get();
		if (curacceleration == null) {
			if (lastVelocity == lastVelocity)
				curacceleration = new RPIdouble((curvelocity.get() - lastVelocity) / samplingTime);
			else
				curacceleration = new RPIdouble(0);
		}
		lastPosition = curposition.get();
		lastVelocity = curvelocity.get();

		RPIdouble destPos = inDestPos.get();
		RPIdouble destVel = inDestVel.get();
		if (destVel == null)
			destVel = new RPIdouble(0.0);
		if (destPos == null) {
			outPos.set(null);
			outVel.set(null);
			outAcc.set(null);

		} else {
			OTGData data = calculate(destPos.get(), destVel.get(), curposition.get(), curvelocity.get(),
					curacceleration.get());

			outPos.set(new RPIdouble(data.Position));
			outVel.set(new RPIdouble(data.Velocity));
			outAcc.set(new RPIdouble(data.Acceleration));
			lastCmdPosition = data.Position;
		}

	}

	double calculateDesiredAcceleration(double position, double velocity, double currentPosition,
			double currentVelocity) {

		double ek = (currentPosition - position) / maxAcceleration;
		double ek2 = (currentVelocity - velocity) / maxAcceleration;
		double zk = (1 / samplingTime) * ((ek / samplingTime) + (ek2 / 2));

		double m = Math.floor((1 + Math.sqrt(1 + 8 * Math.abs(zk))) / 2);

		double zk2 = ek2 / samplingTime;

		double sigma = zk2 + zk / m + ((m - 1) / 2) * sign(zk);

		double acc = (-maxAcceleration) * sat(sigma)
				* (1 + sign(currentVelocity * sign(sigma) + maxVelocity - samplingTime * maxAcceleration)) / 2;

		return acc;
	}

	OTGData calculate(double position, double velocity, double currentPosition, double currentVelocity,
			double currentAcceleration) {
		OTGData result = new OTGData();

		double resultAcc = calculateDesiredAcceleration(position, velocity, currentPosition, currentVelocity);
		double resultVel = currentVelocity + samplingTime * resultAcc;
		double resultPos = currentPosition + samplingTime * resultVel;

		result.Acceleration = resultAcc;
		result.Velocity = resultVel;
		result.Position = resultPos;

		return result;
	}

	private double sat(double x) {
		if (x < -1)
			return -1;
		else if (x > 1)
			return 1;
		else
			return x;
	}

	private int sign(double x) {
		if (x < 0)
			return -1;
		else if (x > 0)
			return 1;
		else
			return 0;
	}

}
