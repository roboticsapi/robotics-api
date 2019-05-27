/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleArray;
import org.roboticsapi.runtime.core.primitives.DoubleArraySet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmMapper.KinematicsFragment;
import org.roboticsapi.runtime.robot.primitives.Kin;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;

public class RobotArmKinematicsFragment extends KinematicsFragment {
	private final DataflowInPort jointInPort;
	private final DataflowOutPort frameOutPort;

	public RobotArmKinematicsFragment(SoftRobotRobotArmDriver robot) throws MappingException {
		super("SoftRobotRobotArm.Kinematics");
		try {
			final Kin kin = new Kin();
			kin.setRobot(robot.getDeviceName());
			add(kin);

			DoubleArray da = new DoubleArray(robot.getJointCount());
			add(da);

			DoubleArraySet[] das = new DoubleArraySet[robot.getJointCount()];
			InPort[] dasinp = new InPort[robot.getJointCount()];
			OutPort lastarray = da.getOutArray();
			for (int i = 0; i < robot.getJointCount(); i++) {
				das[i] = new DoubleArraySet(robot.getJointCount(), i);
				dasinp[i] = das[i].getInValue();
				das[i].getInArray().connectTo(lastarray);
				lastarray = das[i].getOutArray();
				add(das[i]);
			}

			kin.getInJoints().connectTo(lastarray);

			jointInPort = addInPort(new JointsDataflow(robot.getJointCount(), robot), true, dasinp);
			frameOutPort = addOutPort(new RelationDataflow(robot.getBase(), robot.getFlange()), false,
					kin.getOutFrame());
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	@Override
	public DataflowInPort getInJoints() {
		return jointInPort;
	}

	@Override
	public DataflowOutPort getOutFrame() {
		return frameOutPort;
	}

}
