/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.FrameProjector;
import org.roboticsapi.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanAnd;
import org.roboticsapi.runtime.core.primitives.DoubleEquals;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DerivedActuatorDriverMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.FrameInvert;
import org.roboticsapi.runtime.world.primitives.FrameOTG;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.primitives.FrameTransform;
import org.roboticsapi.runtime.world.primitives.RotationToAxisAngle;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorDot;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianGoalToCartesianVelocityMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, CartesianActuatorDriver, CartesianGoalActionResult> {

	private final double maxTransError;
	private final double maxRotError;
	private final double maxGoalTransVel;
	private final double maxGoalRotVel;

	public CartesianGoalToCartesianVelocityMapper(double maxTransError, double maxRotError, double maxGoalTransVel,
			double maxGoalRotVel) {
		this.maxTransError = maxTransError;
		this.maxRotError = maxRotError;
		this.maxGoalTransVel = maxGoalTransVel;
		this.maxGoalRotVel = maxGoalRotVel;
	}

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final CartesianActuatorDriver driver,
			CartesianGoalActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {
		final NetFragment fragment = new NetFragment("Cartesian Goal to Cartesian Velocity");
		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		// apply projector
		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);

		DataflowThroughOutPort position = fragment.addThroughPort(true, actionResult.getOutPort().getType());

		if (proj != null && proj.getProjector() != null) {
			try {
				DataflowThroughOutPort toProject = fragment.addThroughPort(true,
						new RelationDataflow(driver.getReferenceFrame(), driver.getMovingFrame()));
				FrameProjector projector = proj.getProjector();
				RelationSensor toProjectSensor = RelationDataflow.createRelationSensor(toProject, driver.getRuntime());
				RelationSensor projected = projector.project(toProjectSensor, parameters);
				SensorMapperResult<Transformation> mappedSensor = runtime.getMapperRegistry()
						.mapSensor(driver.getRuntime(), projected, fragment, new SensorMappingContext());
				fragment.connect(actionResult.getOutPort(), toProject.getInPort());
				fragment.connect(mappedSensor.getSensorPort(), position.getInPort());
			} catch (TransformationException e) {
				throw new MappingException(e);
			}
		} else {
			fragment.connect(actionResult.getOutPort(), position.getInPort());
		}

		// compute velocity
		Frame movingFrame = driver.getMovingFrame();
		Frame referenceFrame = driver.getReferenceFrame();

		RelationDataflow relDataflow = new RelationDataflow(referenceFrame, movingFrame);
		VelocityDataflow velDataflow = new VelocityDataflow(movingFrame, referenceFrame, movingFrame.getPoint(),
				referenceFrame.getOrientation());

		RelationSensor curPos = referenceFrame.getRelationSensor(movingFrame);
		VelocitySensor curVel = referenceFrame.getVelocitySensorOf(movingFrame);

		SensorMappingContext context = new SensorMappingContext();
		SensorMapperResult<Transformation> posResult = runtime.getMapperRegistry().mapSensor(runtime, curPos, fragment,
				context);
		SensorMapperResult<Twist> velResult = runtime.getMapperRegistry().mapSensor(runtime, curVel, fragment, context);

		CartesianParameters cp = parameters.get(CartesianParameters.class);

		// override
		DataflowOutPort overridePort = ports.override;

		FrameOTG otg = fragment.add(new FrameOTG());
		otg.getInCurPos().setDebug(2);
		otg.getInCurVel().setDebug(2);
		otg.getInDestPos().setDebug(2);

		DoubleMultiply posVel = fragment.add(new DoubleMultiply(cp.getMaximumPositionVelocity(), 0.0));
		fragment.connect(overridePort, posVel.getInSecond(), new DoubleDataflow());
		fragment.connect(posVel.getOutValue(), otg.getInMaxTransVel());

		DoubleMultiply posAcc = fragment.add(new DoubleMultiply(cp.getMaximumPositionAcceleration(), 0.0));
		fragment.connect(overridePort, posAcc.getInSecond(), new DoubleDataflow());
		fragment.connect(posAcc.getOutValue(), otg.getInMaxTransAcc());

		DoubleMultiply rotVel = fragment.add(new DoubleMultiply(cp.getMaximumRotationVelocity(), 0.0));
		fragment.connect(overridePort, rotVel.getInSecond(), new DoubleDataflow());
		fragment.connect(rotVel.getOutValue(), otg.getInMaxRotVel());

		DoubleMultiply rotAcc = fragment.add(new DoubleMultiply(cp.getMaximumRotationAcceleration(), 0.0));
		fragment.connect(overridePort, rotAcc.getInSecond(), new DoubleDataflow());
		fragment.connect(rotAcc.getOutValue(), otg.getInMaxRotAcc());

		fragment.connect(posResult.getSensorPort(), otg.getInCurPos(), relDataflow);
		fragment.connect(velResult.getSensorPort(), otg.getInCurVel(), velDataflow);

		fragment.connect(position, otg.getInDestPos(), relDataflow);
		DataflowOutPort velocity = fragment.addOutPort(velDataflow, false, otg.getOutVel());

		final ActuatorDriverMapperResult result = runtime.getMapperRegistry().mapActuatorDriver(runtime, driver,
				new CartesianVelocityActionResult(velocity), parameters, ports.cancel, ports.override);
		fragment.add(result.getNetFragment());

		TwistToVelocities vel = fragment.add(new TwistToVelocities());
		fragment.connect(otg.getOutVel(), vel.getInValue());
		VectorDot transLen = fragment.add(new VectorDot());
		fragment.connect(vel.getOutTransVel(), transLen.getInFirst());
		fragment.connect(vel.getOutTransVel(), transLen.getInSecond());

		VectorDot rotLen = fragment.add(new VectorDot());
		fragment.connect(vel.getOutRotVel(), rotLen.getInFirst());
		fragment.connect(vel.getOutRotVel(), rotLen.getInSecond());

		// completion
		FrameInvert inv = fragment.add(new FrameInvert());
		fragment.connect(position, inv.getInValue(), relDataflow);
		FrameTransform trans = fragment.add(new FrameTransform());
		fragment.connect(inv.getOutValue(), trans.getInFirst());
		fragment.connect(posResult.getSensorPort(), trans.getInSecond(), relDataflow);
		FrameToPosRot split = fragment.add(new FrameToPosRot());
		fragment.connect(trans.getOutValue(), split.getInValue());
		RotationToAxisAngle rot = fragment.add(new RotationToAxisAngle());
		fragment.connect(split.getOutRotation(), rot.getInValue());
		VectorDot pos = fragment.add(new VectorDot());
		fragment.connect(split.getOutPosition(), pos.getInFirst());
		fragment.connect(split.getOutPosition(), pos.getInSecond());
		DoubleEquals t = fragment.add(new DoubleEquals(0.0, 0.0, maxTransError * maxTransError));
		fragment.connect(pos.getOutValue(), t.getInFirst());
		DoubleEquals r = fragment.add(new DoubleEquals(0.0, 0.0, maxRotError));
		fragment.connect(rot.getOutAngle(), r.getInFirst());
		DoubleEquals vt = fragment.add(new DoubleEquals(0.0, 0.0, maxGoalTransVel * maxGoalTransVel));
		fragment.connect(rotLen.getOutValue(), vt.getInFirst());
		DoubleEquals vr = fragment.add(new DoubleEquals(0.0, 0.0, maxGoalRotVel * maxGoalRotVel));
		fragment.connect(rotLen.getOutValue(), vt.getInFirst());

		BooleanAnd tr = fragment.add(new BooleanAnd());
		fragment.connect(t.getOutValue(), tr.getInFirst());
		fragment.connect(r.getOutValue(), tr.getInSecond());
		BooleanAnd vtr = fragment.add(new BooleanAnd());
		fragment.connect(vt.getOutValue(), vtr.getInFirst());
		fragment.connect(vr.getOutValue(), vtr.getInSecond());
		BooleanAnd done = fragment.add(new BooleanAnd());
		fragment.connect(tr.getOutValue(), done.getInFirst());
		fragment.connect(vtr.getOutValue(), done.getInSecond());
		final DataflowOutPort completed = fragment.addOutPort(new StateDataflow(), false, done.getOutValue());

		return new DerivedActuatorDriverMapperResult(result, fragment, completed);
	}
}
