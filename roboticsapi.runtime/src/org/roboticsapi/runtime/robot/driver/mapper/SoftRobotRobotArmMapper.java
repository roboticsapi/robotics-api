/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotMultiJointMapper;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;

public interface SoftRobotRobotArmMapper extends SoftRobotMultiJointMapper {

	public abstract InvKinematicsFragment createInvKinematicsFragment(SoftRobotRobotArmDriver robot,
			DeviceParameterBag parameters) throws MappingException;

	public static abstract class VelKinematicsFragment extends NetFragment {
		public VelKinematicsFragment(String name) {
			super(name);
		}

		public abstract DataflowInPort getInJoints();

		public abstract DataflowInPort getInJointVels();

		public abstract DataflowOutPort getOutVelocity();
	}

	public static abstract class InvKinematicsFragment extends NetFragment {
		public InvKinematicsFragment(String name) {
			super(name);
		}

		public abstract DataflowInPort getInFrame();

		public abstract DataflowInPort getInHintJoints();

		public abstract DataflowOutPort getOutJoints();

	}

	public static abstract class NullspaceInvKinematicsFragment extends InvKinematicsFragment {
		public NullspaceInvKinematicsFragment(String name) {
			super(name);
		}

		public abstract DataflowInPort getInNullspaceJoints();
	}

	public static abstract class KinematicsFragment extends NetFragment {
		public KinematicsFragment(String name) {
			super(name);
		}

		public abstract DataflowInPort getInJoints();

		public abstract DataflowOutPort getOutFrame();
	}

}