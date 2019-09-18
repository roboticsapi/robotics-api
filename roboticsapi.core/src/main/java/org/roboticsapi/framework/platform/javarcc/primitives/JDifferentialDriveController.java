/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.javarcc.primitives;

import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.core.world.mutable.MutableVector;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.primitives.world.RPICalc;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

public class JDifferentialDriveController extends JPrimitive {
	private JInPort<RPIFrame> inPosition = add("inPosition", new JInPort<RPIFrame>());
	private JInPort<RPITwist> inTwist = add("inTwist", new JInPort<RPITwist>());
	private JInPort<RPIFrame> inGoal = add("inGoal", new JInPort<RPIFrame>());
	private JOutPort<RPITwist> outTwist = add("outTwist", new JOutPort<RPITwist>());
	private JParameter<RPIdouble> velX = add("VelX", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> velYaw = add("VelYaw", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> pX = add("PX", new JParameter<RPIdouble>());
	private JParameter<RPIdouble> pYaw = add("PYaw", new JParameter<RPIdouble>());

	public JDifferentialDriveController() {
		velX.set(new RPIdouble(0.5));
		velYaw.set(new RPIdouble(1));
		pX.set(new RPIdouble(3));
		pYaw.set(new RPIdouble(3));
	}

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inPosition);
		connected(inGoal);
	}

	private MutableVector pos = RPICalc.vectorCreate();
	private MutableVector goal = RPICalc.vectorCreate();
	private MutableVector diff = RPICalc.vectorCreate();
	private MutableTwist ret = RPICalc.twistCreate();
	private RPITwist twist = RPICalc.rpiTwistCreate();

	@Override
	public void updateData() {
		if (anyNull(inPosition, inGoal))
			return;
		RPICalc.rpiToVector(inPosition.get().getPos(), pos);
		RPICalc.rpiToVector(inGoal.get().getPos(), goal);
		double th = inPosition.get().getRot().getA().get();
		double thGoal = inGoal.get().getRot().getA().get();
		inTwist.get();
		double maxX = velX.get().get(), maxYaw = velYaw.get().get();
		if (Double.isNaN(th) || Double.isNaN(goal.getX()) || Double.isNaN(thGoal))
			return;
		pos.scaleTo(-1, diff);
		goal.addTo(diff, diff);
		double dist = diff.getLength();

		double a = Math.atan2(goal.getY() - pos.getY(), goal.getX() - pos.getX());
		double direction = 1;
		if (Math.abs(angleNormalize(a - th)) > Math.PI / 2) {
			direction = -1;
			a = a + Math.PI;
		}

		double orient = Math.max(0, Math.min(1, 2 - dist * 100));
		double move = Math.max(0, Math.min(1, 2 - Math.abs(angleNormalize(a - th) * 10)));
		double aim = Math.max(0, 1 - orient * 4 - move);

		double vxAim = 0;
		double vthAim = angleNormalize(a - th) * pYaw.get().get();
		double vxMove = dist * pX.get().get() * direction;
		double vthMove = 0;
		double vxOrient = 0;
		double vthOrient = angleNormalize(thGoal - th) * pYaw.get().get();

		double vx = vxAim * aim + vxMove * move + vxOrient * orient;
		double vth = vthAim * aim + vthMove * move + vthOrient * orient;
		if (vx > maxX)
			vx = maxX;
		else if (vx < -maxX)
			vx = -maxX;

		if (vth > maxYaw)
			vth = maxYaw;
		if (vth < -maxYaw)
			vth = -maxYaw;

		ret.set(vx, 0, 0, 0, 0, vth);
		RPICalc.twistToRpi(ret, twist);
		outTwist.set(twist);
	}

	private double angleNormalize(double angle) {
		while (angle > Math.PI)
			angle -= Math.PI * 2;
		while (angle < -Math.PI)
			angle += Math.PI * 2;
		return angle;
	}

}
