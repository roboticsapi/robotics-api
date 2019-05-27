/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.cartesianmotion.action.HybridMoveAction;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.AndFragment;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.fragment.MergeTransformationFragment;
import org.roboticsapi.runtime.world.fragment.SplitTransformationFragment;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.world.Frame;

public class HybridMoveMapper implements ActionMapper<SoftRobotRuntime, HybridMoveAction> {

	private interface Selector {

		DataflowOutPort select(SplitTransformationFragment add);

	}

	private class InnerActionPorts {
		public DataflowOutPort valuePort;
		public List<DataflowOutPort> completedPort;

		public InnerActionPorts(DataflowOutPort valuePort2, DataflowOutPort completedPort2) {
			valuePort = valuePort2;
			completedPort = new ArrayList<DataflowOutPort>();
			completedPort.add(completedPort2);
		}

		public InnerActionPorts(DataflowOutPort valuePort2, List<DataflowOutPort> completedPort2) {
			valuePort = valuePort2;
			completedPort = completedPort2;
		}

	}

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, HybridMoveAction action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		MotionCenterParameter mcp = parameters.get(MotionCenterParameter.class);

		if (mcp == null) {
			throw new MappingException("No MotionCenterParameter was supplied!");
		}

		NetFragment fragment = new NetFragment("HybridMove");

		InnerActionPorts xPorts = mapInnerAction(runtime, action.getxAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutX();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort xPort = xPorts.valuePort;

		InnerActionPorts yPorts = mapInnerAction(runtime, action.getyAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutY();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort yPort = yPorts.valuePort;

		InnerActionPorts zPorts = mapInnerAction(runtime, action.getzAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutZ();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort zPort = zPorts.valuePort;

		InnerActionPorts aPorts = mapInnerAction(runtime, action.getaAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutA();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort aPort = aPorts.valuePort;

		InnerActionPorts bPorts = mapInnerAction(runtime, action.getbAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutB();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort bPort = bPorts.valuePort;

		InnerActionPorts cPorts = mapInnerAction(runtime, action.getcAction(), new Selector() {

			@Override
			public DataflowOutPort select(SplitTransformationFragment add) {
				return add.getOutC();
			}
		}, parameters, ports, fragment, action.getBase(), mcp.getMotionCenter());
		DataflowOutPort cPort = cPorts.valuePort;

		DataflowOutPort resultTransformation = fragment
				.add(new MergeTransformationFragment(xPort, yPort, zPort, aPort, bPort, cPort)).getOutTransformation();

		DataflowOutPort resultPort = fragment.reinterpret(resultTransformation,
				new RelationDataflow(action.getBase(), mcp.getMotionCenter()));

		List<DataflowOutPort> completedPorts = new ArrayList<DataflowOutPort>();
		completedPorts.addAll(xPorts.completedPort);
		completedPorts.addAll(yPorts.completedPort);
		completedPorts.addAll(zPorts.completedPort);
		completedPorts.addAll(aPorts.completedPort);
		completedPorts.addAll(bPorts.completedPort);
		completedPorts.addAll(cPorts.completedPort);

		fragment.add(new AndFragment(new StateDataflow(), completedPorts));
		DataflowOutPort completedPort = null;

		return new BaseActionMapperResult(action, fragment, new CartesianPositionActionResult(resultPort),
				completedPort);
	}

	private InnerActionPorts mapInnerAction(SoftRobotRuntime runtime, Action action, Selector selector,
			DeviceParameterBag parameters, ActionMappingContext ports, NetFragment fragment, Frame base,
			Frame motionCenter) throws MappingException {

		ActionMapperResult mappedInner = runtime.getMapperRegistry().mapAction(runtime, action, parameters,
				ports.cancelPort, ports.overridePort, ports.actionPlans);
		fragment.add(mappedInner.getNetFragment());
		mappedInner.getActionResult().getOutPort();
		DataflowOutPort innerResult = fragment.addConverterLink(mappedInner.getActionResult().getOutPort(),
				new RelationDataflow(base, motionCenter));
		DataflowOutPort valuePort = selector.select(fragment.add(new SplitTransformationFragment(innerResult)));
		List<DataflowOutPort> completedPort = mappedInner.getStatePort(action.getCompletedState());
		return new InnerActionPorts(valuePort, completedPort);

	}

}
