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
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableTwist;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a frame online trajectory generator
 */
public class JFrameOTG extends JPrimitive {
	private JInPort<RPITwist> inCurVel = add("inCurVel", new JInPort<RPITwist>());
	private JInPort<RPIFrame> inCurPos = add("inCurPos", new JInPort<RPIFrame>());
	private JInPort<RPITwist> inDestVel = add("inDestVel", new JInPort<RPITwist>());
	private JInPort<RPIFrame> inDestPos = add("inDestPos", new JInPort<RPIFrame>());
	private JInPort<RPIdouble> inMaxTransVel = add("inMaxTransVel", new JInPort<RPIdouble>()),
			inMaxTransAcc = add("inMaxTransAcc", new JInPort<RPIdouble>()),
			inMaxRotVel = add("inMaxRotVel", new JInPort<RPIdouble>()),
			inMaxRotAcc = add("inMaxRotAcc", new JInPort<RPIdouble>());
	private JParameter<RPIdouble> propMaxTransVel = add("MaxTransVel", new JParameter<RPIdouble>()),
			propMaxTransAcc = add("MaxTransAcc", new JParameter<RPIdouble>()),
			propMaxRotVel = add("MaxRotVel", new JParameter<RPIdouble>()),
			propMaxRotAcc = add("MaxRotAcc", new JParameter<RPIdouble>());
	private JOutPort<RPIFrame> outPos = add("outPos", new JOutPort<RPIFrame>());
	private JOutPort<RPITwist> outVel = add("outVel", new JOutPort<RPITwist>());

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inCurVel, inCurPos, inDestPos);
	}

	MutableTransformation curInv = RPICalc.frameCreate();
	MutableTransformation diff = RPICalc.frameCreate();
	MutableTransformation curPos = RPICalc.frameCreate();
	MutableTransformation destPos = RPICalc.frameCreate();
	MutableTwist curVel = RPICalc.twistCreate();
	MutableTwist destVel = RPICalc.twistCreate();
	MutableVector axis = RPICalc.vectorCreate();

	MutableTwist goalVel = RPICalc.twistCreate();
	MutableTwist goalAcc = RPICalc.twistCreate();
	MutableTwist cmdVel = RPICalc.twistCreate();
	MutableTransformation cmdPos = RPICalc.frameCreate();
	RPIFrame pos = RPICalc.rpiFrameCreate();
	RPITwist vel = RPICalc.rpiTwistCreate();

	@Override
	public void updateData() {
		if (anyNull(inCurPos, inDestPos, inCurVel))
			return;
		// read variables
		RPICalc.rpiToFrame(inCurPos.get(), curPos);
		RPICalc.rpiToFrame(inDestPos.get(), destPos);
		RPICalc.rpiToTwist(inCurVel.get(), curVel);
		if (inDestVel.get() == null) {
			destVel.set(0, 0, 0, 0, 0, 0);
		} else {
			RPICalc.rpiToTwist(inDestVel.get(), destVel);
		}
		double maxTransVel = inMaxTransVel.get(propMaxTransVel).get();
		double maxTransAcc = inMaxTransAcc.get(propMaxTransAcc).get();
		double maxRotVel = inMaxRotVel.get(propMaxRotVel).get();
		double maxRotAcc = inMaxRotAcc.get(propMaxRotAcc).get();
		double dt = getNet().getCycleTime();

		// reduce maximum velocity to allow decelerating to goal

		curPos.invertTo(curInv);
		curInv.multiplyTo(destPos, diff);
		diff.getRotation().getAxisTo(axis);
		double transDist = diff.getTranslation().getLength(), rotDist = axis.getLength();
		double decelTransVel = Math.sqrt(2 * maxTransAcc * 0.8 * transDist);
		double decelRotVel = Math.sqrt(2 * maxRotAcc * 0.8 * rotDist);
		double maxDecelTransVel = decelTransVel < maxTransVel ? decelTransVel : maxTransVel;
		double maxDecelRotVel = decelRotVel < maxRotVel ? decelRotVel : maxRotVel;

		// find out twist to goal
		curPos.getDeltaTo(destPos, dt, goalVel);
		double transVel = goalVel.getTranslation().getLength();
		double rotVel = goalVel.getRotation().getLength();

		// reduce goal twist to given maximum values
		double transFactor = 1, rotFactor = 1;
		if (transVel > maxDecelTransVel)
			transFactor = transVel / maxDecelTransVel;
		if (rotVel > maxDecelRotVel)
			rotFactor = rotVel / maxDecelRotVel;
		double factor = rotFactor > transFactor ? rotFactor : transFactor;
		goalVel.getTranslation().scale(1 / factor);
		goalVel.getRotation().scale(1 / factor);
		goalVel.getTranslation().add(destVel.getTranslation());
		goalVel.getRotation().add(destVel.getRotation());

		transVel = goalVel.getTranslation().getLength();
		rotVel = goalVel.getRotation().getLength();
		transFactor = 1;
		rotFactor = 1;
		if (transVel > maxTransVel)
			transFactor = transVel / maxTransVel;
		if (rotVel > maxRotVel)
			rotFactor = rotVel / maxRotVel;
		factor = rotFactor > transFactor ? rotFactor : transFactor;
		goalVel.getTranslation().scale(1 / factor);
		goalVel.getRotation().scale(1 / factor);

		// adapt goal twist to current velocity and maximum acceleration
		curVel.getDeltaTo(goalVel, dt, goalAcc);
		double transAcc = goalAcc.getTranslation().getLength();
		double rotAcc = goalAcc.getRotation().getLength();
		transFactor = 1;
		rotFactor = 1;
		if (transAcc > maxTransAcc)
			transFactor = transAcc / maxTransAcc;
		if (rotAcc > maxRotAcc)
			rotFactor = rotAcc / maxRotAcc;
		factor = rotFactor > transFactor ? rotFactor : transFactor;
		goalAcc.getTranslation().scale(1 / factor);
		goalAcc.getRotation().scale(1 / factor);

		// calculate new twist
		curVel.addDeltaTo(goalAcc, dt, cmdVel);
		RPICalc.twistToRpi(cmdVel, vel);
		outVel.set(vel);
		// calculate corresponding position
		curPos.addDeltaTo(cmdVel, dt, cmdPos);
		RPICalc.frameToRpi(cmdPos, pos);
		outPos.set(pos);
	}
};
