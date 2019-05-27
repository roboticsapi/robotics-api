/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.cartesianmotion.action.PathMotion;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CancellableMotionFragment;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.mapping.MappingException;
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
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.TransformationSensor;

public class ExecutablePathMotionMapper<A extends PathMotion<? extends ExecutableCartesianMotionPlan>>
		implements ActionMapper<SoftRobotRuntime, A> {

	private final class ExecutablePathMotionFragment extends CancellableMotionFragment {
		private final DataflowOutPort resultPort;
		private final ExecutableCartesianMotionPlan plan;
		private final DoubleDataflowSensor timeSensor;
		private final SoftRobotRuntime runtime;
		private final SensorMappingContext context;

		private ExecutablePathMotionFragment(SoftRobotRuntime runtime, ActionMappingContext ports,
				double cancelVelocity, OverrideParameter ovp, ExecutableCartesianMotionPlan plan, Frame motionCenter)
				throws MappingException {
			super("ExecutablePathMotion", ports, cancelVelocity);
			this.runtime = runtime;
			this.plan = plan;
			final Clock clock = add(new Clock(1.0));
			DataflowOutPort overridePort = getInterpolatedOverridePort();

			connect(overridePort, addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
			DataflowOutPort time = addOutPort(new DoubleDataflow(), false, clock.getOutValue());

			context = new SensorMappingContext();
			timeSensor = new DoubleDataflowSensor(time, runtime);
			context.addSensorResult(timeSensor, new DoubleSensorMapperResult(this, time));

			TransformationSensor transSensor = plan.getTransformationSensorAt(timeSensor);

			SensorMapperResult<Transformation> trans = runtime.getMapperRegistry().mapSensor(runtime, transSensor, this,
					context);

			resultPort = reinterpret(trans.getSensorPort(), new RelationDataflow(plan.getBaseFrame(), motionCenter));

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

			// TODO: this special treatment is currently necessary due to
			// CancellableMotionFragments handling of completion, combining
			// ordinary and cancel completion. This could surely be done more
			// elegant.
			if (state instanceof Action.CompletedState) {
				setMotionCompletedPort(mappedSensor.getSensorPort());
				return getCompletedPort();
			}

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
		final ExecutableCartesianMotionPlan plan = (ExecutableCartesianMotionPlan) ports.actionPlans.get(action);

		// find motion center
		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);
		if (mp == null) {
			throw new MappingException("No motion center parameters given");
		}

		// calculate time to decelerate from maximum velocity
		CartesianParameters cartesianParameters = parameters.get(CartesianParameters.class);
		if (cartesianParameters == null) {
			throw new MappingException("No cartesian device parameters given");
		}
		double decelTime = 0;
		decelTime = cartesianParameters.getMaximumPositionVelocity()
				/ cartesianParameters.getMaximumPositionAcceleration();
		if (decelTime < cartesianParameters.getMaximumRotationVelocity()
				/ cartesianParameters.getMaximumRotationAcceleration()) {
			decelTime = cartesianParameters.getMaximumRotationVelocity()
					/ cartesianParameters.getMaximumRotationAcceleration();
		}

		final ExecutablePathMotionFragment ret = new ExecutablePathMotionFragment(runtime, ports, 1d / decelTime,
				parameters.get(OverrideParameter.class), plan, mp.getMotionCenter());

		ActionResult result = new CartesianPositionActionResult(ret.getResultPort());

		StatePortFactory statePortFactory = new AbstractStateSinglePortFactory<ActionState>() {
			@Override
			protected DataflowOutPort createTypedStateSinglePort(ActionState state) throws MappingException {
				return ret.createStatePort(state);
			}
		};
		ActionMapperResult mapperResult = new BaseActionMapperResult(action, ret, result, statePortFactory);
		mapperResult.addActionStatePortFactory(ActionState.class, statePortFactory);
		mapperResult.addActionExceptionPort(ActionCancelledException.class, ret.getCancelCompleted());

		return mapperResult;
	}
}
