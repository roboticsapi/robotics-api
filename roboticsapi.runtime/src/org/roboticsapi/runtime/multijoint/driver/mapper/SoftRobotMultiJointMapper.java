/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.multijoint.JointVelsDataflow;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;

public interface SoftRobotMultiJointMapper {

	public abstract JointsDataflow getJointsDataflowType(SoftRobotMultiJointDeviceDriver multiJointDriver);

	public abstract JointVelsDataflow getJointVelsDataflowType(SoftRobotMultiJointDeviceDriver multiJointDriver);

	public abstract MultiJointMonitorOuts addMultiJointMonitorFragment(SoftRobotRuntime runtime, NetFragment ret,
			SoftRobotMultiJointDeviceDriver r) throws MappingException;

	public abstract JointsDataflow getJointsDataflowType(MultiJointDeviceDriver driver);

	public abstract JointVelsDataflow getJointVelsDataflowType(MultiJointDeviceDriver driver);

	public static final class MultiJointMonitorOuts {
		public final DataflowOutPort jointValues;
		public final DataflowOutPort jointVelValues;

		public MultiJointMonitorOuts(DataflowOutPort jointValues, DataflowOutPort jointVelValues) {
			this.jointValues = jointValues;
			this.jointVelValues = jointVelValues;

		}
	}
}