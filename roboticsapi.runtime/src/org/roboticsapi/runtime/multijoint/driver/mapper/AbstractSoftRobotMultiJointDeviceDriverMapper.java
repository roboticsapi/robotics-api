/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointVelsDataflow;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;

public abstract class AbstractSoftRobotMultiJointDeviceDriverMapper<DD extends SoftRobotMultiJointDeviceDriver, AR extends ActionResult>
		implements ActuatorDriverMapper<SoftRobotRuntime, DD, AR>, SoftRobotMultiJointMapper {

	@Override
	public final JointsDataflow getJointsDataflowType(SoftRobotMultiJointDeviceDriver multiJointDriver) {
		return new JointsDataflow(multiJointDriver.getJointCount(), multiJointDriver);
	}

	@Override
	public final JointVelsDataflow getJointVelsDataflowType(SoftRobotMultiJointDeviceDriver multiJointDriver) {
		return new JointVelsDataflow(multiJointDriver.getJointCount(), multiJointDriver);
	}

	@Override
	public final MultiJointMonitorOuts addMultiJointMonitorFragment(SoftRobotRuntime runtime, NetFragment ret,
			SoftRobotMultiJointDeviceDriver r) throws MappingException {

		MapperRegistry mappers = runtime.getMapperRegistry();

		DataflowOutPort[] jointSensorOuts = new DataflowOutPort[r.getJointCount()];
		DataflowOutPort[] jointVelSensorOuts = new DataflowOutPort[r.getJointCount()];

		SensorMappingContext context = new SensorMappingContext();
		for (int i = 0; i < r.getJointCount(); i++) {
			DoubleSensor sensor = r.getJoint(i).getCommandedPositionSensor();
			DoubleSensor velSensor = r.getJoint(i).getCommandedVelocitySensor();

			SensorMapperResult<Double> mappedSensor = mappers.mapSensor(runtime, sensor, ret, context);
			SensorMapperResult<Double> mappedVelSensor = mappers.mapSensor(runtime, velSensor, ret, context);

			jointSensorOuts[i] = mappedSensor.getSensorPort();
			jointVelSensorOuts[i] = mappedVelSensor.getSensorPort();
		}

		ComposedDataflowOutPort composedOutPort = new ComposedDataflowOutPort(true, jointSensorOuts);
		ComposedDataflowOutPort composedVelOutPort = new ComposedDataflowOutPort(true, jointVelSensorOuts);

		NetFragment reinterpret = new NetFragment("Joint joiner");

		ret.add(reinterpret);

		reinterpret.addOutPort(composedOutPort);
		reinterpret.addOutPort(composedVelOutPort);

		return new MultiJointMonitorOuts(ret.reinterpret(composedOutPort, getJointsDataflowType(r)),
				ret.reinterpret(composedVelOutPort, getJointVelsDataflowType(r)));
	}

	@Override
	public final JointsDataflow getJointsDataflowType(final MultiJointDeviceDriver driver) {
		return new JointsDataflow(driver.getJointCount(), driver);
	}

	@Override
	public final JointVelsDataflow getJointVelsDataflowType(MultiJointDeviceDriver driver) {
		return new JointVelsDataflow(driver.getJointCount(), driver);
	}
}
