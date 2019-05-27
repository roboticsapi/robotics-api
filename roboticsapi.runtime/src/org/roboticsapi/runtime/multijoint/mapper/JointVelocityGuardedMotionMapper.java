/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.multijoint.action.JointVelocityGuardedMotion;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.mapper.DoubleMinFragment;
import org.roboticsapi.runtime.core.mapper.ScaleToMaxFragment;
import org.roboticsapi.runtime.core.mapper.WrappedActionMapper;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoubleSquareRoot;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.JointVelDataflow;
import org.roboticsapi.runtime.multijoint.JointVelsDataflow;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class JointVelocityGuardedMotionMapper<A extends JointVelocityGuardedMotion>
		extends WrappedActionMapper<A> {

	@Override
	protected ActionMapperResult getWrapperMapperResult(SoftRobotRuntime runtime, A action,
			ActionMapperResult mappedInnerAction, DeviceParameterBag parameters, ActionMappingContext ports)
			throws MappingException {
		NetFragment fragment = new NetFragment("Joint velocity guarding");

		fragment.add(mappedInnerAction.getNetFragment());

		List<DataflowOutPort> jointCmdVel = new ArrayList<DataflowOutPort>();
		HashMap<JointDriver, List<DataflowOutPort>> jointScalingMultiFactors = new HashMap<JointDriver, List<DataflowOutPort>>();

		int k = 0;

		DataflowOutPort jointCmdPos = fragment.addConverterLink(mappedInnerAction.getActionResult().getOutPort(),
				new JointsDataflow(action.getMultiJointDevice().getJointCount(),
						action.getMultiJointDevice().getDriver()));

		DataflowOutPort jointCmdVels = fragment.addConverterLink(jointCmdPos, new JointVelsDataflow(
				action.getMultiJointDevice().getJointCount(), action.getMultiJointDevice().getDriver()));

		for (Joint j : action.getMultiJointDevice().getJoints()) {

			// DataflowOutPort cmdPos = fragment.addConverterLink(
			// mappedInnerAction.getActionResult().getOutPort(),
			// new JointDataflow(j));
			JointDriver driver = j.getDriver();

			DataflowOutPort cmdPos = fragment.addConverterLink(jointCmdPos, new JointDataflow(driver));

			DataflowOutPort cmdVel = fragment.addConverterLink(jointCmdVels, new JointVelDataflow(driver));

			jointCmdVel.add(cmdVel);

			// scale velocity to maximum joint velocity
			JointParameters jointParameters = parameters.get(JointDeviceParameters.class).getJointParameters(k);
			double maxVel = jointParameters.getMaximumVelocity();
			ScaleToMaxFragment maxVelScaler = fragment.add(new ScaleToMaxFragment(maxVel, -maxVel, cmdVel));

			// scale velocity to respect joint end positions
			ScaleToMaxFragment maxPosVelScaler = fragment.add(new ScaleToMaxFragment(
					calculateMaxEndPositionSafeVelocity(fragment, cmdPos, jointParameters.getMaximumPosition() - 0.01,
							jointParameters.getMaximumAcceleration()),
					calculateMinEndPositionSafeVelocity(fragment, cmdPos, jointParameters.getMinimumPosition() + 0.01,
							jointParameters.getMaximumAcceleration()),
					cmdVel));

			if (jointScalingMultiFactors.get(driver) == null) {
				jointScalingMultiFactors.put(driver, new ArrayList<DataflowOutPort>());
			}
			jointScalingMultiFactors.get(driver).add(maxVelScaler.getOutScalingFactor());

			jointScalingMultiFactors.get(driver).add(maxPosVelScaler.getOutScalingFactor());

			k++;
		}

		// we take the minimum of all scaling factors
		List<DataflowOutPort> jointScalingFactors = new ArrayList<DataflowOutPort>();
		for (Joint j : action.getMultiJointDevice().getJoints()) {
			List<DataflowOutPort> list = jointScalingMultiFactors.get(j.getDriver());

			jointScalingFactors.add(fragment.add(new DoubleMinFragment(list)).getOutMin());
		}

		// concrete guarding method based on scaling factors is determined by
		// subclasses
		List<DataflowOutPort> jointGuardedVel = createJointVelocityGuarding(action, fragment, jointCmdVel,
				jointScalingFactors);

		// datatype conversions
		ComposedDataflowOutPort composedJointGuardedVel = fragment.addOutPort(new ComposedDataflowOutPort(true,
				jointGuardedVel.toArray(new DataflowOutPort[jointGuardedVel.size()])));

		DataflowOutPort jointGuardedVels = fragment.reinterpret(composedJointGuardedVel,
				getJointVelsDataflowType(action.getMultiJointDevice().getDriver()));

		DataflowOutPort jointGuardedPos = fragment.addConverterLink(jointGuardedVels,
				getJointsDataflowType(action.getMultiJointDevice().getDriver()));
		return new JointVelocityGuardedMotionMapperResult(action, fragment, createActionResult(jointGuardedPos),
				mappedInnerAction.getStatePort(action.getWrappedAction().getCompletedState()));

	}

	protected abstract ActionResult createActionResult(DataflowOutPort outPort);

	private DataflowOutPort calculateMaxEndPositionSafeVelocity(NetFragment fragment, DataflowOutPort cmdPosition,
			double maxPosition, double maxAcceleration) throws MappingException {
		DoubleMultiply invertCmdPos = fragment.add(new DoubleMultiply());
		fragment.connect(cmdPosition, fragment.addInPort(new DoubleDataflow(), true, invertCmdPos.getInFirst()));
		invertCmdPos.setSecond(-1d);

		DoubleAdd delta = fragment.add(new DoubleAdd());
		delta.setFirst(maxPosition);
		try {
			delta.getInSecond().connectTo(invertCmdPos.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}
		DataflowOutPort deltaPort = fragment.addOutPort(new DoubleDataflow(), true, delta.getOutValue());

		return calculateVelocity(fragment, deltaPort, maxAcceleration);

	}

	private DataflowOutPort calculateMinEndPositionSafeVelocity(NetFragment fragment, DataflowOutPort cmdPosition,
			double minPosition, double maxAcceleration) throws MappingException {

		DoubleAdd delta = fragment.add(new DoubleAdd());
		fragment.connect(cmdPosition, fragment.addInPort(new DoubleDataflow(), true, delta.getInFirst()));
		delta.setSecond(-minPosition);
		DataflowOutPort deltaPort = fragment.addOutPort(new DoubleDataflow(), true, delta.getOutValue());

		DataflowOutPort velocityAbs = calculateVelocity(fragment, deltaPort, maxAcceleration);

		DoubleMultiply invert = fragment.add(new DoubleMultiply());
		fragment.connect(velocityAbs, fragment.addInPort(new DoubleDataflow(), true, invert.getInFirst()));
		invert.setSecond(-1d);

		return fragment.addOutPort(new DoubleDataflow(), true, invert.getOutValue());
	}

	private DataflowOutPort calculateVelocity(NetFragment fragment, DataflowOutPort deltaPort, double maxAcceleration)
			throws MappingException {

		try {
			DoubleMultiply mult2 = fragment.add(new DoubleMultiply());
			mult2.setFirst(2d);
			fragment.connect(deltaPort, fragment.addInPort(new DoubleDataflow(), true, mult2.getInSecond()));

			DoubleMultiply multA = fragment.add(new DoubleMultiply());
			multA.getInFirst().connectTo(mult2.getOutValue());
			multA.setSecond(maxAcceleration);

			DoubleGreater greaterZero = fragment.add(new DoubleGreater());
			greaterZero.setSecond(0d);
			greaterZero.getInFirst().connectTo(multA.getOutValue());

			DoubleConditional ifGreaterZero = fragment.add(new DoubleConditional());
			ifGreaterZero.getInCondition().connectTo(greaterZero.getOutValue());
			ifGreaterZero.getInTrue().connectTo(multA.getOutValue());
			ifGreaterZero.setFalse(0d);

			DoubleSquareRoot root = fragment.add(new DoubleSquareRoot());
			root.getInValue().connectTo(ifGreaterZero.getOutValue());
			return fragment.addOutPort(new DoubleDataflow(), true, root.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	private DataflowType getJointsDataflowType(MultiJointDeviceDriver multiJointDeviceDriver) {
		return new JointsDataflow(multiJointDeviceDriver.getJointCount(), multiJointDeviceDriver);
	}

	private DataflowType getJointVelsDataflowType(MultiJointDeviceDriver multiJointDeviceDriver) {
		return new JointVelsDataflow(multiJointDeviceDriver.getJointCount(), multiJointDeviceDriver);
	}

	protected abstract List<DataflowOutPort> createJointVelocityGuarding(A action, NetFragment fragment,
			List<DataflowOutPort> jointCmdVel, List<DataflowOutPort> jointScalingFactors) throws MappingException;

	protected class JointVelocityGuardedMotionMapperResult extends BaseActionMapperResult {

		public JointVelocityGuardedMotionMapperResult(Action action, NetFragment fragment, ActionResult result,
				List<DataflowOutPort> completedPort) {
			super(action, fragment, result, completedPort);
		}

	}

}
