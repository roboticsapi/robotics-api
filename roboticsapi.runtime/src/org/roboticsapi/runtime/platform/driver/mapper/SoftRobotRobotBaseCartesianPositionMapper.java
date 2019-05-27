/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver.mapper;

import org.roboticsapi.cartesianmotion.FrameProjector;
import org.roboticsapi.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianPositionActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.core.primitives.DoubleAverage;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DerivedActuatorDriverMapperResult;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;
import org.roboticsapi.runtime.platform.primitives.BaseMonitor;
import org.roboticsapi.runtime.platform.primitives.OmnidirectionalBaseController;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistAdd;
import org.roboticsapi.runtime.world.primitives.TwistConditional;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.TwistIsNull;
import org.roboticsapi.runtime.world.primitives.TwistPre;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.world.primitives.VectorToXYZ;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

public class SoftRobotRobotBaseCartesianPositionMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotBaseDriver, CartesianPositionActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final SoftRobotRobotBaseDriver device,
			CartesianPositionActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		final NetFragment fragment = new NetFragment("Robot base");
		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		// apply projector
		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);

		DataflowThroughOutPort position = fragment.addThroughPort(true, actionResult.getOutPort().getType());

		if (proj != null && proj.getProjector() != null) {
			try {
				DataflowThroughOutPort toProject = fragment.addThroughPort(true,
						new RelationDataflow(device.getReferenceFrame(), device.getMovingFrame()));
				FrameProjector projector = proj.getProjector();
				RelationSensor toProjectSensor = RelationDataflow.createRelationSensor(toProject, device.getRuntime());
				RelationSensor projected = projector.project(toProjectSensor, parameters);
				SensorMapperResult<Transformation> mappedSensor = runtime.getMapperRegistry()
						.mapSensor(device.getRuntime(), projected, fragment, new SensorMappingContext());
				fragment.connect(actionResult.getOutPort(), toProject.getInPort());
				fragment.connect(mappedSensor.getSensorPort(), position.getInPort());
			} catch (TransformationException e) {
				throw new MappingException(e);
			}
		} else {
			fragment.connect(actionResult.getOutPort(), position.getInPort());
		}

		Frame odometryFrame = device.getOdometryFrame();
		Frame odometryOrigin = device.getOdometryOrigin();

		DataflowOutPort resultTransform = fragment.addConverterLink(position,
				new RelationDataflow(odometryOrigin, odometryFrame));

		// calculate feedback term
		OmnidirectionalBaseController rmpCtrl = fragment
				.add(new OmnidirectionalBaseController(0.3, 0.3, 0.3, 3.0, 3.0, 3.0));
		rmpCtrl.getInDest().setDebug(10);
		rmpCtrl.getInPos().setDebug(10);
		rmpCtrl.getInVel().setDebug(10);

		VelocityDataflow velocityDataflowType = new VelocityDataflow(odometryFrame, odometryOrigin,
				odometryFrame.getPoint(), odometryOrigin.getOrientation());

		DataflowInPort inGoal = fragment.addInPort(new RelationDataflow(odometryOrigin, odometryFrame), true,
				rmpCtrl.getInDest());
		fragment.connect(resultTransform, inGoal);

		BaseMonitor rmpMon = fragment.add(new BaseMonitor(device.getDeviceName()));
		rmpCtrl.getInPos().connectTo(rmpMon.getOutPos());

		TwistPre lastTwist = fragment.add(new TwistPre());
		fragment.connect(rmpCtrl.getOutVel(), lastTwist.getInValue());

		TwistIsNull isFirst = fragment.add(new TwistIsNull());
		fragment.connect(lastTwist.getOutValue(), isFirst.getInValue());

		TwistConditional twist = fragment.add(new TwistConditional());

		DataflowInPort falseIn = fragment.addInPort(velocityDataflowType, true, twist.getInFalse());
		DataflowOutPort lastOut = fragment.addOutPort(new VelocityDataflow(odometryFrame, odometryOrigin,
				odometryFrame.getPoint(), odometryFrame.getOrientation()), true, lastTwist.getOutValue());
		fragment.connect(lastOut, falseIn);

		twist.getInTrue().connectTo(rmpMon.getOutVel());
		twist.getInCondition().connectTo(isFirst.getOutValue());
		rmpCtrl.getInVel().connectTo(twist.getOutValue());

		// add feedforward and feedback term
		TwistAdd resultVel = fragment.add(new TwistAdd());
		fragment.connect(rmpCtrl.getOutVel(), resultVel.getInFirst());
		resultVel.getInFirst().setDebug(10);
		resultVel.getInSecond().setDebug(10);

		// smooth commanded velocity

		TwistFromVelocities nullTwist = fragment.add(new TwistFromVelocities(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

		TwistIsNull goalNull = fragment.add(new TwistIsNull());
		fragment.connect(resultTransform, fragment.addInPort(velocityDataflowType, false, goalNull.getInValue()));

		TwistConditional goalUnlessNull = fragment.add(new TwistConditional());
		fragment.connect(goalNull.getOutValue(), goalUnlessNull.getInCondition());
		fragment.connect(nullTwist.getOutValue(), goalUnlessNull.getInTrue());
		fragment.connect(resultTransform, fragment.addInPort(velocityDataflowType, false, goalUnlessNull.getInFalse()));

		TwistToVelocities goalVel = fragment.add(new TwistToVelocities());
		fragment.connect(goalUnlessNull.getOutValue(), goalVel.getInValue());

		VectorToXYZ goalDir = fragment.add(new VectorToXYZ());
		VectorToXYZ goalRot = fragment.add(new VectorToXYZ());
		DoubleAverage xSmooth = fragment.add(new DoubleAverage(0.1));
		DoubleAverage ySmooth = fragment.add(new DoubleAverage(0.1));
		DoubleAverage aSmooth = fragment.add(new DoubleAverage(0.1));
		VectorFromXYZ smoothDir = fragment.add(new VectorFromXYZ(0.0, 0.0, 0.0));
		VectorFromXYZ smoothRot = fragment.add(new VectorFromXYZ(0.0, 0.0, 0.0));
		TwistFromVelocities goalVelSmooth = fragment.add(new TwistFromVelocities());

		fragment.connect(goalVel.getOutTransVel(), goalDir.getInValue());
		fragment.connect(goalVel.getOutRotVel(), goalRot.getInValue());
		fragment.connect(goalDir.getOutX(), xSmooth.getInValue());
		fragment.connect(goalDir.getOutY(), ySmooth.getInValue());
		fragment.connect(goalRot.getOutZ(), aSmooth.getInValue());
		fragment.connect(xSmooth.getOutValue(), smoothDir.getInX());
		fragment.connect(ySmooth.getOutValue(), smoothDir.getInY());
		fragment.connect(aSmooth.getOutValue(), smoothRot.getInZ());
		fragment.connect(smoothDir.getOutValue(), goalVelSmooth.getInTransVel());
		fragment.connect(smoothRot.getOutValue(), goalVelSmooth.getInRotVel());
		fragment.connect(goalVelSmooth.getOutValue(), resultVel.getInSecond());

		// map driver for velocity set point
		final ActuatorDriverMapperResult mappedDriver = runtime.getMapperRegistry().mapActuatorDriver(runtime, device,
				new CartesianVelocityActionResult(
						fragment.addOutPort(velocityDataflowType, false, resultVel.getOutValue())),
				parameters, ports.cancel, ports.override);
		fragment.add(mappedDriver.getNetFragment());

		return new DerivedActuatorDriverMapperResult(mappedDriver, fragment);
	}
}
