/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.AllJointVelocitiesScaledMotion;
import org.roboticsapi.multijoint.action.FollowJointGoal;
import org.roboticsapi.multijoint.action.FollowJointVelocity;
import org.roboticsapi.multijoint.action.HoldJointPosition;
import org.roboticsapi.multijoint.action.JointErrorCorrection;
import org.roboticsapi.multijoint.action.JointReset;
import org.roboticsapi.multijoint.action.JointSuperposition;
import org.roboticsapi.multijoint.action.LimitedJerkPTP;
import org.roboticsapi.multijoint.action.PTP;
import org.roboticsapi.multijoint.action.PTPFromMotion;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotJointDriverJointGoalMapper;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotJointDriverJointPositionMapper;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotJointDriverJointResetMapper;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotMultiJointDeviceDriverJointGoalMapper;
import org.roboticsapi.runtime.multijoint.driver.mapper.SoftRobotMultiJointDeviceDriverJointPositionMapper;
import org.roboticsapi.runtime.multijoint.mapper.AllJointVelocitiesScaledMotionMapper;
import org.roboticsapi.runtime.multijoint.mapper.ExecutableJointMotionMapper;
import org.roboticsapi.runtime.multijoint.mapper.FollowJointGoalMapper;
import org.roboticsapi.runtime.multijoint.mapper.FollowJointVelocityMapper;
import org.roboticsapi.runtime.multijoint.mapper.HoldJointPositionMapper;
import org.roboticsapi.runtime.multijoint.mapper.JointErrorCorrectionMapper;
import org.roboticsapi.runtime.multijoint.mapper.JointGoalActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointResetActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointResetMapper;
import org.roboticsapi.runtime.multijoint.mapper.JointSuperpositionMapper;
import org.roboticsapi.runtime.multijoint.mapper.PTPFromMotionMapper;
import org.roboticsapi.runtime.multijoint.mapper.PTPMapper;
import org.roboticsapi.runtime.multijoint.mapper.SoftRobotJointSensorMapper;

public final class SoftRobotMultiJointExtension extends AbstractSoftRobotRoboticsBuilder {

	public SoftRobotMultiJointExtension() {
		super(SoftRobotJointDriver.class, SoftRobotMultiJointDeviceDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return null;
	}

	@Override
	protected void onRoboticsObjectAvailable(RoboticsObject object) {
		super.onRoboticsObjectAvailable(object);
		if (object instanceof MultiJointDevice) {
			// final MultiJointDevice mjd = (MultiJointDevice)
			// object;
			// TODO: add DeviceInterfaces
		}
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {
		MapperRegistry mapperregistry = runtime.getMapperRegistry();

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, PTP.class, new PTPMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, PTPFromMotion.class, new PTPFromMotionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, LimitedJerkPTP.class,
				new ExecutableJointMotionMapper<LimitedJerkPTP>());
		// mapperregistry.registerActionMapper(SoftRobotRuntime.class,
		// PTP.class,
		// new ExecutableJointMotionMapper<PTP>());
		// mapperregistry.registerActionMapper(SoftRobotRuntime.class,
		// PTPFromMotion.class,
		// new ExecutableJointMotionMapper<PTPFromMotion>());

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, JointErrorCorrection.class,
				new JointErrorCorrectionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, FollowJointVelocity.class,
				new FollowJointVelocityMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, AllJointVelocitiesScaledMotion.class,
				new AllJointVelocitiesScaledMotionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, FollowJointGoal.class, new FollowJointGoalMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, HoldJointPosition.class,
				new HoldJointPositionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, JointSuperposition.class,
				new JointSuperpositionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, JointReset.class, new JointResetMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotMultiJointDeviceDriver.class,
				JointPositionActionResult.class,
				new SoftRobotMultiJointDeviceDriverJointPositionMapper<SoftRobotMultiJointDeviceDriver>());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotJointDriver.class,
				JointPositionActionResult.class, new SoftRobotJointDriverJointPositionMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotMultiJointDeviceDriver.class,
				JointGoalActionResult.class,
				new SoftRobotMultiJointDeviceDriverJointGoalMapper<SoftRobotMultiJointDeviceDriver>());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotJointDriver.class,
				JointGoalActionResult.class, new SoftRobotJointDriverJointGoalMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotJointDriver.class,
				JointResetActionResult.class, new SoftRobotJointDriverJointResetMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SoftRobotJointDriver.SoftRobotJointSensor.class,
				new SoftRobotJointSensorMapper());

	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
		// TODO remove mappers???
	}
}
