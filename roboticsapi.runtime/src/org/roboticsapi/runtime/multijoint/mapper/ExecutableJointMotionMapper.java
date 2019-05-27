/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.multijoint.action.ExecutableJointMotionPlan;
import org.roboticsapi.multijoint.action.JointMotion;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.DoubleDataflowSensor;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.AbstractStateSinglePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.StatePortFactory;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.multijoint.mapper.fragments.CancellableMotionFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class ExecutableJointMotionMapper<A extends JointMotion<? extends ExecutableJointMotionPlan>>
		implements ActionMapper<SoftRobotRuntime, A> {

	private final class ExecutableJointMotionFragment extends CancellableMotionFragment {
		private final DataflowOutPort resultPort;
		private final ExecutableJointMotionPlan plan;
		private final DoubleDataflowSensor timeSensor;
		private final SoftRobotRuntime runtime;
		private final SensorMappingContext context;

		private ExecutableJointMotionFragment(SoftRobotRuntime runtime, ActionMappingContext ports,
				double cancelVelocity, OverrideParameter ovp, ExecutableJointMotionPlan plan) throws MappingException {
			super("ExecutableJointMotion", ports, cancelVelocity);
			this.runtime = runtime;
			this.plan = plan;
			final Clock clock = add(new Clock(1.0));
			DataflowOutPort overridePort = getInterpolatedOverridePort();

			connect(overridePort, addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
			DataflowOutPort time = addOutPort(new DoubleDataflow(), false, clock.getOutValue());

			context = new SensorMappingContext();
			timeSensor = new DoubleDataflowSensor(time, runtime);
			context.addSensorResult(timeSensor, new DoubleSensorMapperResult(this, time));

			DoubleSensor[] jointSensors = plan.getJointPositionSensorAt(timeSensor);

			ComposedDataflowOutPort joints = new ComposedDataflowOutPort(true);
			for (int i = 0; i < jointSensors.length; i++) {
				SensorMapperResult<Double> joint = runtime.getMapperRegistry().mapSensor(runtime, jointSensors[i], this,
						context);
				joints.addDataflow(joint.getSensorPort());
			}

			resultPort = reinterpret(joints, new JointsDataflow(jointSensors.length, null));

		}

		public DataflowOutPort getResultPort() {
			return resultPort;
		}

		public DataflowOutPort createStatePort(ActionState state) throws MappingException {
			BooleanSensor stateSensor = plan.getStateSensorAt(timeSensor, state);
			if (stateSensor == null) {
				return null;
			}
			SensorMapperResult<Boolean> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime, stateSensor, this,
					context);
			return mappedSensor.getSensorPort();
		}
	}

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, A action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		if (ports.actionPlans.get(action) == null) {
			try {
				action.calculatePlan(ports.actionPlans, parameters);
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
		final ExecutableJointMotionPlan plan = (ExecutableJointMotionPlan) ports.actionPlans.get(action);

		// calculate time to decelerate from maximum velocity
		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);
		if (jointParameters == null) {
			throw new MappingException("No joint device parameters given");
		}
		double decelTime = 0;
		for (int i = 0; i < jointParameters.getJointCount(); i++) {
			JointParameters parameter = jointParameters.getJointParameters(i);
			if (decelTime < parameter.getMaximumVelocity() / parameter.getMaximumAcceleration()) {
				decelTime = parameter.getMaximumVelocity() / parameter.getMaximumAcceleration();
			}
		}

		final ExecutableJointMotionFragment ret = new ExecutableJointMotionFragment(runtime, ports, 1d / decelTime,
				parameters.get(OverrideParameter.class), plan);

		ActionResult result = new JointPositionActionResult(ret.getResultPort());

		StatePortFactory statePortFactory = new AbstractStateSinglePortFactory<ActionState>() {
			@Override
			protected DataflowOutPort createTypedStateSinglePort(ActionState state) throws MappingException {
				return ret.createStatePort(state);
			}
		};
		ActionMapperResult mapperResult = new BaseActionMapperResult(action, ret, result, statePortFactory);
		mapperResult.addActionStatePortFactory(ActionState.class, statePortFactory);

		return mapperResult;
	}
}
