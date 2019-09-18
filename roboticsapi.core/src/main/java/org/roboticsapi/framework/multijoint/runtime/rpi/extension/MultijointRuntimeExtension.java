/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.multijoint.JointSensorInterface;
import org.roboticsapi.framework.multijoint.action.FollowJointGoal;
import org.roboticsapi.framework.multijoint.action.FollowJointVelocity;
import org.roboticsapi.framework.multijoint.action.HoldJointPosition;
import org.roboticsapi.framework.multijoint.action.HoldJointVelocity;
import org.roboticsapi.framework.multijoint.action.JointErrorCorrection;
import org.roboticsapi.framework.multijoint.action.JointJogging;
import org.roboticsapi.framework.multijoint.action.JointReset;
import org.roboticsapi.framework.multijoint.action.JointSuperposition;
import org.roboticsapi.framework.multijoint.action.MoveToJointGoal;
import org.roboticsapi.framework.multijoint.action.PTP;
import org.roboticsapi.framework.multijoint.action.PTPFromMotion;
import org.roboticsapi.framework.multijoint.action.SwitchJointController;
import org.roboticsapi.framework.multijoint.runtime.rpi.JointGenericDriver;
import org.roboticsapi.framework.multijoint.runtime.rpi.JointSensorInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.ExecutableJointMotionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.FollowJointGoalMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.FollowJointVelocityMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.HoldJointPositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.HoldJointVelocityMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointDriverJointPositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointDriverJointResetMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointErrorCorrectionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointGoalToJointPositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointJoggingMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointResetMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointSensorMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointSuperpositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointVelocityGoalToJointVelocityMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.JointVelocityToJointPositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.MoveToJointGoalMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.MultiJointDeviceMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.SwitchControllerJointImpedanceMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.mapper.SwitchControllerPositionMapper;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionGoalActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointResetActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointVelocityGoalActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

public final class MultijointRuntimeExtension extends RpiExtension {

	public MultijointRuntimeExtension() {
		super(JointGenericDriver.class, MultiJointDeviceGenericDriver.class);
	}

	@Override
	public void onAvailable(RoboticsObject object) {
		super.onAvailable(object);
		if (object instanceof JointGenericDriver) {
			((JointGenericDriver) object).getDevice().addInterfaceFactory(JointSensorInterface.class,
					() -> new JointSensorInterfaceImpl((JointGenericDriver) object));
		}
	}

	@Override
	protected void registerMappers(MapperRegistry mr) {

		mr.registerActuatorDriverMapper(JointGenericDriver.class, JointPositionActionResult.class,
				new JointDriverJointPositionMapper());

		mr.registerActuatorDriverMapper(MultiJointDeviceGenericDriver.class, MultiJointActionResult.class,
				new MultiJointDeviceMapper());

		mr.registerActuatorDriverMapper(JointGenericDriver.class, JointResetActionResult.class,
				new JointDriverJointResetMapper());
		mr.registerActuatorDriverMapper(JointGenericDriver.class, JointVelocityActionResult.class,
				new JointVelocityToJointPositionMapper());
		mr.registerActuatorDriverMapper(JointGenericDriver.class, JointPositionGoalActionResult.class,
				new JointGoalToJointPositionMapper(0.001));
		mr.registerActuatorDriverMapper(JointGenericDriver.class, JointVelocityGoalActionResult.class,
				new JointVelocityGoalToJointVelocityMapper(0.001));

		mr.registerActionMapper(PTP.class, new ExecutableJointMotionMapper<PTP>());
		mr.registerActionMapper(PTPFromMotion.class, new ExecutableJointMotionMapper<PTPFromMotion>());

		mr.registerActionMapper(JointErrorCorrection.class, new JointErrorCorrectionMapper());
		mr.registerActionMapper(JointJogging.class, new JointJoggingMapper());
		// mr.registerActionMapper(AllJointVelocitiesScaledMotion.class,
		// new AllJointVelocitiesScaledMotionMapper());
		mr.registerActionMapper(FollowJointGoal.class, new FollowJointGoalMapper());
		mr.registerActionMapper(FollowJointVelocity.class, new FollowJointVelocityMapper());
		mr.registerActionMapper(MoveToJointGoal.class, new MoveToJointGoalMapper());
		mr.registerActionMapper(HoldJointPosition.class, new HoldJointPositionMapper());
		mr.registerActionMapper(HoldJointVelocity.class, new HoldJointVelocityMapper());
		mr.registerActionMapper(JointSuperposition.class, new JointSuperpositionMapper());
		mr.registerActionMapper(JointReset.class, new JointResetMapper());
		mr.registerActionMapper(SwitchJointController.class, new SwitchControllerJointImpedanceMapper());
		mr.registerActionMapper(SwitchJointController.class, new SwitchControllerPositionMapper());

		mr.registerRealtimeValueMapper(new JointSensorMapper());

	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO remove mappers???
	}
}
