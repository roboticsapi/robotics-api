/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.core.primitives.DoubleArray;
import org.roboticsapi.runtime.core.primitives.DoubleArrayGet;
import org.roboticsapi.runtime.core.primitives.DoubleArraySet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmMapper.InvKinematicsFragment;
import org.roboticsapi.runtime.robot.primitives.InvKin;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;

public class RobotArmInvKinematicsFragment extends InvKinematicsFragment {

	private final DataflowInPort inFrame;
	private final DataflowInPort inJoints;
	private final DataflowOutPort outJoints;

	public RobotArmInvKinematicsFragment(SoftRobotRobotArmDriver robot, DeviceParameterBag parameters)
			throws MappingException {
		super("ArmKinematics.InvKinematics");
		try {
			final InvKin kin = add(new InvKin(robot.getDeviceName()));

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

			kin.getInHintJoints().connectTo(lastarray);

			inFrame = addInPort(new RelationDataflow(robot.getBase(), robot.getFlange()), true, kin.getInFrame());
			kin.getInFrame().setDebug(2);

			inJoints = addInPort(new JointsDataflow(robot.getJointCount(), robot), true, dasinp);

			DoubleArrayGet[] dag = new DoubleArrayGet[robot.getJointCount()];
			OutPort[] dagoutp = new OutPort[robot.getJointCount()];

			for (int i = 0; i < robot.getJointCount(); i++) {
				dag[i] = new DoubleArrayGet(robot.getJointCount(), i);
				dagoutp[i] = dag[i].getOutValue();
				dag[i].getInArray().connectTo(kin.getOutJoints());
				lastarray = kin.getOutJoints();
				add(dag[i]);
			}

			outJoints = addOutPort(new JointsDataflow(robot.getJointCount(), robot), true, dagoutp);
		} catch (RPIException e) {
			throw new MappingException(e);
		}

	}

	@Override
	public DataflowInPort getInFrame() {
		return inFrame;
	}

	@Override
	public DataflowOutPort getOutJoints() {
		return outJoints;
	}

	@Override
	public DataflowInPort getInHintJoints() {
		return inJoints;
	}
}
