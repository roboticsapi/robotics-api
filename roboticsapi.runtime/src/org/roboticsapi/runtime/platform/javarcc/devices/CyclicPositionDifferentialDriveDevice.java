/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.javarcc.devices;

import org.roboticsapi.runtime.cartesianmotion.javarcc.devices.CyclicPositionCartesianMotionDevice;
import org.roboticsapi.runtime.world.javarcc.primitives.RPICalc;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableTwist;
import org.roboticsapi.world.mutable.MutableVector;

public class CyclicPositionDifferentialDriveDevice extends CyclicPositionCartesianMotionDevice {

	private double k;
	private double eps;
	private MutableTwist curVelToCommand = RPICalc.twistCreate();
	private MutableTwist lastVelToCommand = null;

	private MutableTransformation cmdPos = RPICalc.frameCreate();
	private MutableTwist cmdVel = RPICalc.twistCreate();
	private MutableTwist errVel = RPICalc.twistCreate();
	private MutableVector xVec = RPICalc.vectorCreate();
	private RPITwist velToCommand = RPICalc.rpiTwistCreate();
	private RPIFrame posToCommand = RPICalc.rpiFrameCreate();

	public CyclicPositionDifferentialDriveDevice(int smoothLength, double k, double eps) {
		super(smoothLength);
		this.k = k;
		this.eps = eps;
	}

	@Override
	public void requestData(long realTicks) {
		super.requestData(realTicks);
		if (curVelToCommand != null)
			lastVelToCommand = curVelToCommand;
		curVelToCommand = null;
	}

	public MutableTwist getDifferentialDriveVelocityToCommand(MutableTransformation msrPos, MutableTwist ret) {
		if (curVelToCommand != null)
			return curVelToCommand;

		double maxVel = getMaximumPositionVelocity();
		double maxRot = getMaximumRotationVelocity();

		RPICalc.rpiToFrame(getPositionToCommand(posToCommand), cmdPos);
		RPICalc.rpiToTwist(getVelocityToCommand(velToCommand), cmdVel);
		if (msrPos == null)
			return null;
		msrPos.getDeltaTo(cmdPos, 1, errVel);
		errVel.getTranslation().scale(k);
		errVel.getTranslation().add(cmdVel.getTranslation());
		errVel.getRotation().scale(k);
		errVel.getRotation().add(cmdVel.getRotation());

		MutableVector transVel = errVel.getTranslation(), rotVel = errVel.getTranslation();
		if (transVel.getLength() < eps && rotVel.getLength() > eps) {
			curVelToCommand.getTranslation().set(0, 0, 0);
			curVelToCommand.getRotation().set(rotVel.getX(), rotVel.getY(), rotVel.getZ());
			return limitAcceleration(lastVelToCommand, curVelToCommand, getCycleTime(), curVelToCommand);
		}

		if (transVel.getLength() < 1e-5) {
			curVelToCommand.getTranslation().set(0, 0, 0);
			curVelToCommand.getRotation().set(rotVel.getX(), rotVel.getY(), rotVel.getZ());
			return limitAcceleration(lastVelToCommand, curVelToCommand, getCycleTime(), curVelToCommand);
		}

		xVec.set(1, 0, 0);
		xVec.rotate(msrPos.getRotation());
		xVec.crossTo(transVel, rotVel);
		rotVel.scale(k / transVel.getLength());

		if (rotVel.getLength() > maxRot)
			rotVel.scale(maxRot / rotVel.getLength());

		double xlen = transVel.getLength();
		double x = xVec.dot(transVel);

		double fac = Math.abs(x / xlen);
		x = x * fac * fac * fac;
		if (x > maxVel)
			x = x / Math.abs(x) * maxVel;
		if (x < 0)
			rotVel.scale(-1);

		curVelToCommand.getTranslation().set(x, 0, 0);
		rotVel.unRotateTo(msrPos.getRotation(), curVelToCommand.getRotation());
		return limitAcceleration(lastVelToCommand, curVelToCommand, getCycleTime(), curVelToCommand);
	}

	private MutableTwist limitAcceleration(MutableTwist lastVel, MutableTwist curVel, double cycleTime,
			MutableTwist ret) {
		double posScale = 1, rotScale = 1;
		MutableVector lv = lastVel.getTranslation();
		MutableVector lr = lastVel.getRotation();
		MutableVector cv = curVel.getTranslation();
		MutableVector cr = curVel.getRotation();
		lv.scaleTo(-1, ret.getTranslation());
		ret.getTranslation().add(cv);
		ret.getTranslation().scale(1 / cycleTime);
		lr.scaleTo(-1, ret.getRotation());
		ret.getTranslation().add(cr);
		ret.getTranslation().scale(1 / cycleTime);
		double posMax = getMaximumPositionAcceleration();
		double rotMax = getMaximumRotationAcceleration();
		if (posMax > 0 && ret.getTranslation().getLength() / posMax > posScale)
			posScale = ret.getTranslation().getLength() / posMax;
		if (rotMax > 0 && ret.getRotation().getLength() / rotMax > rotScale)
			rotScale = ret.getRotation().getLength() / rotMax;
		ret.getTranslation().scale(cycleTime / posScale);
		ret.getRotation().scale(cycleTime / rotScale);
		ret.getTranslation().add(lv);
		ret.getRotation().add(lr);
		return ret;
	}

}
