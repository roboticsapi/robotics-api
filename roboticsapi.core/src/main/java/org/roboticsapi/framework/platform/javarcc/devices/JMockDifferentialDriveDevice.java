/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.javarcc.devices;

import org.roboticsapi.core.world.mutable.MutableTransformation;
import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.facet.javarcc.devices.AbstractJDevice;
import org.roboticsapi.facet.javarcc.devices.PeriodicTask;
import org.roboticsapi.facet.javarcc.primitives.world.RPICalc;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;
import org.roboticsapi.framework.cartesianmotion.javarcc.interfaces.JCartesianPositionInterface;
import org.roboticsapi.framework.platform.javarcc.interfaces.JRobotBaseInterface;

public class JMockDifferentialDriveDevice extends AbstractJDevice
		implements JCartesianPositionInterface, JRobotBaseInterface {

	private static final double CYCLE_TIME = 0.05;

	private CyclicPositionDifferentialDriveDevice cpdd = new CyclicPositionDifferentialDriveDevice(0, 1, 0.05);

	private MutableTransformation pos = RPICalc.frameCreate();
	private MutableTwist vel = RPICalc.twistCreate();
	private MutableTwist lvel = RPICalc.twistCreate();

	private RPIFrame msrPos = RPICalc.rpiFrameCreate();
	private RPITwist msrVel = RPICalc.rpiTwistCreate();

	public JMockDifferentialDriveDevice(double maxPosVel, double maxRotVel, double maxPosAcc, double maxRotAcc) {
		addTask(new PeriodicTask(CYCLE_TIME) {
			@Override
			public void doPeriodicTask() {
				cpdd.requestData();
				cpdd.getDifferentialDriveVelocityToCommand(pos, vel);
				vel.getTranslation().rotateTo(pos.getRotation(), lvel.getTranslation());
				vel.getRotation().rotateTo(pos.getRotation(), lvel.getRotation());
				pos.addDelta(lvel, CYCLE_TIME);
			}
		});

		cpdd.setMaximumAcceleration(maxPosAcc, maxPosAcc);
		cpdd.setMaximumVelocity(maxPosVel, maxRotVel);
	}

	@Override
	public int getWheelCount() {
		return 2;
	}

	@Override
	public double getWheelPosition(int i) {
		return 0;
	}

	@Override
	public double getWheelVelocity(int i) {
		return 0;
	}

	@Override
	public RPIFrame getMeasuredPosition() {
		RPICalc.frameToRpi(pos, msrPos);
		return msrPos;
	}

	@Override
	public RPITwist getMeasuredVelocity() {
		RPICalc.twistToRpi(vel, msrVel);
		return msrVel;
	}

	@Override
	public RPITwist getCommandedVelocity() {
		return cpdd.getCommandedVelocity();
	}

	@Override
	public RPIFrame getCommandedPosition() {
		return cpdd.getCommandedPosition();
	}

	@Override
	public int getCartesianPositionDeviceError() {
		return 0;
	}

	@Override
	public int checkPosition(RPIFrame pos) {
		return 0;
	}

	@Override
	public void setPosition(RPIFrame pos, Long time) {
		cpdd.setCartesianPosition(pos, time);
	}

}
