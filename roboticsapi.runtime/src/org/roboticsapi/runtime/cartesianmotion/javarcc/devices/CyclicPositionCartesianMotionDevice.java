/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.javarcc.devices;

import org.roboticsapi.runtime.world.javarcc.primitives.RPICalc;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableTwist;
import org.roboticsapi.world.mutable.MutableVector;

public class CyclicPositionCartesianMotionDevice {

	// data from RPI net
	private final MutableTransformation RPIPos = RPICalc.frameCreate();
	private final MutableTwist RPIVel = RPICalc.twistCreate();
	private long RPITicks;

	// data from RPI net at last CP cycle
	private final MutableTransformation RPIPos1 = RPICalc.frameCreate();
	private long RPITicks1;
	private final MutableTransformation RPIPos2 = RPICalc.frameCreate();
	private long RPITicks2;

	// data from last CP cycle
	private final MutableTransformation CPPos = RPICalc.frameCreate();
	private final MutableTwist CPVel = RPICalc.twistCreate();
	private long lastCPTicks, lastRealTicks;
	private long tickDiffSum;
	private long[] tickDiffs;
	private int tickDiffCount, tickDiffPos;

	private final RPIFrame cmdPos = RPICalc.rpiFrameCreate();
	private final RPITwist cmdVel = RPICalc.rpiTwistCreate();

	private double maxPosAcc, maxRotAcc, maxPosVel, maxRotVel;

	public CyclicPositionCartesianMotionDevice(int smoothLength) {
		RPITicks = 0;
		RPITicks1 = 0;
		RPITicks2 = 0;

		lastCPTicks = lastRealTicks = 0L;

		tickDiffs = new long[smoothLength];
		for (int i = 0; i < smoothLength; i++) {
			tickDiffs[i] = 0L;
		}
		tickDiffCount = smoothLength;

		resetTimeHistory();
	}

	private final MutableTwist twist = RPICalc.twistCreate();
	private final MutableRotation rot = RPICalc.rotationCreate();
	private final MutableTransformation frame = RPICalc.frameCreate();

	public void setCartesianPosition(RPIFrame pos, long time) {

		if (RPITicks != 0 && RPITicks != time) {
			RPICalc.rpiToRotation(pos.getRot(), rot);
			rot.invert();
			RPICalc.rpiToFrame(pos, frame);
			RPIPos.getDeltaTo(frame, (time - RPITicks) / 1e3, twist);
			twist.getTranslation().rotateTo(rot, RPIVel.getTranslation());
			twist.getRotation().rotateTo(rot, RPIVel.getRotation());
		}

		RPICalc.rpiToFrame(pos, RPIPos);
		RPITicks = time;
	}

	public void setCartesianPositionStatic(RPIFrame pos) {
		RPICalc.rpiToFrame(pos, CPPos);
		RPICalc.rpiToFrame(pos, RPIPos);
		RPIVel.set(0, 0, 0, 0, 0, 0);
	}

	public RPITwist getCommandedVelocity() {
		RPICalc.twistToRpi(RPIVel, cmdVel);
		return cmdVel;
	}

	public RPIFrame getCommandedPosition() {
		RPICalc.frameToRpi(RPIPos, cmdPos);
		return cmdPos;
	}

	public void setMaximumAcceleration(double maxPosAcc, double maxRotAcc) {
		this.maxPosAcc = maxPosAcc;
		this.maxRotAcc = maxRotAcc;
	}

	public void setMaximumVelocity(double maxPosVel, double maxRotVel) {
		this.maxPosVel = maxPosVel;
		this.maxRotVel = maxRotVel;
	}

	public double getMaximumPositionAcceleration() {
		return maxPosAcc;
	}

	public double getMaximumRotationAcceleration() {
		return maxRotAcc;
	}

	public double getMaximumPositionVelocity() {
		return maxPosVel;
	}

	public double getMaximumRotationVelocity() {
		return maxRotVel;
	}

	public void resetTimeHistory() {
		tickDiffPos = -1;
		tickDiffSum = 0L;
	}

	public void requestData() {
		requestData(System.currentTimeMillis());
	}

	double cycletime;

	private final MutableTransformation lastCPPos = RPICalc.frameCreate();
	private final MutableVector posVel = RPICalc.vectorCreate();
	private final MutableVector rotVel = RPICalc.vectorCreate();

	public void requestData(long realTicks) {
		long CPTicks;
		if (tickDiffCount > 0) {
			if (tickDiffPos > -1) {
				long tickDiff = realTicks - lastRealTicks;
				if (tickDiffPos >= tickDiffCount)
					tickDiffSum -= tickDiffs[tickDiffPos % tickDiffCount];
				tickDiffs[tickDiffPos % tickDiffCount] = tickDiff;
				tickDiffSum += tickDiff;
				if (tickDiffPos >= tickDiffCount)
					CPTicks = lastCPTicks + tickDiffSum / tickDiffCount;
				else
					CPTicks = realTicks;
			} else {
				CPTicks = realTicks;
			}
		} else {
			CPTicks = realTicks;
		}
		cycletime = Math.max(1e-3, (CPTicks - lastCPTicks) / 1e3);
		lastRealTicks = realTicks;
		lastCPTicks = CPTicks;
		tickDiffPos++;

		CPPos.copyTo(lastCPPos);

		if (RPITicks1 < RPITicks) {
			RPITicks2 = RPITicks1;
			RPIPos1.copyTo(RPIPos2);
			RPITicks1 = RPITicks;
			RPIPos.copyTo(RPIPos1);
		}

		if (Math.abs(RPITicks1 - CPTicks) / 1e3 < cycletime * 10000) {
			// If we have values from RPI that are newer than last time,
			// and the values are somewhat recent (less than 10 cycles old),
			// use values from RPI net
			if (RPITicks1 != RPITicks2 && tickDiffCount > 0 && Math.abs(RPITicks1 - CPTicks) / 1e3 < cycletime * 10) {
				// we want smoothing
				RPIPos2.getDeltaTo(RPIPos1, 1, twist);
				RPIPos2.addDeltaTo(twist, (CPTicks - RPITicks2) / (RPITicks1 - RPITicks2), CPPos);
			} else {
				// just send the data from RPI
				RPIPos.copyTo(CPPos);
			}
		} else // no values have been provided by RPI net, brake to
				// standstill
		{
			double maxPosAcc = getMaximumPositionAcceleration();
			CPVel.getTranslation().copyTo(posVel);
			double delta = maxPosAcc * cycletime;
			double curLen = posVel.getLength();
			if (Math.abs(curLen * cycletime) > delta * cycletime) {
				double newLen = curLen - delta;
				posVel.scale(Math.abs(newLen / curLen));
			} else {
				posVel.set(0, 0, 0);
			}

			double maxRotAcc = getMaximumPositionAcceleration();
			CPVel.getRotation().copyTo(rotVel);
			delta = maxRotAcc * cycletime;
			curLen = rotVel.getLength();
			if (Math.abs(curLen * cycletime) > delta * cycletime) {
				double newLen = curLen - delta;
				rotVel.scale(Math.abs(newLen / curLen));
			} else {
				rotVel.set(0, 0, 0);
			}

			posVel.copyTo(twist.getTranslation());
			rotVel.copyTo(twist.getRotation());
			CPPos.addDelta(twist, cycletime);
			CPPos.copyTo(RPIPos);
			RPIVel.set(0, 0, 0, 0, 0, 0);
		}
		lastCPPos.getDeltaTo(CPPos, cycletime, CPVel);
	}

	public RPITwist getVelocityToCommand(RPITwist ret) {
		RPICalc.twistToRpi(CPVel, ret);
		return ret;
	}

	public RPIFrame getPositionToCommand(RPIFrame ret) {
		RPICalc.frameToRpi(CPPos, ret);
		return ret;
	}

	public double getCycleTime() {
		return cycletime;
	}

}
