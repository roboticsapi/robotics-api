/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.CartesianJogging;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.OTG;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;

public class CartesianJoggingMapper implements ActionMapper<SoftRobotRuntime, CartesianJogging> {

	private static final double OTG_MAX_VEL = 6;
	private static final double OTG_MAX_ACC = 12;

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, CartesianJogging action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		NetFragment result = new NetFragment("CartesianJogging Action");
		SensorMappingContext context = new SensorMappingContext();
		SensorMapperResult<Double> xSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getxSpeed(), result,
				context);
		SensorMapperResult<Double> ySensor = runtime.getMapperRegistry().mapSensor(runtime, action.getySpeed(), result,
				context);
		SensorMapperResult<Double> zSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getzSpeed(), result,
				context);

		SensorMapperResult<Double> aSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getaSpeed(), result,
				context);
		SensorMapperResult<Double> bSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getbSpeed(), result,
				context);
		SensorMapperResult<Double> cSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getcSpeed(), result,
				context);

		// result is multiplied by override
		NetFragment ov = result.add(new NetFragment("Override"));

		DataflowOutPort overridePort = ports.overridePort;

		DoubleMultiply ovMultX = ov.add(new DoubleMultiply());
		ov.connect(xSensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultX.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultX.getInSecond()));
		ovMultX.setSecond(1d);
		DataflowOutPort ovMultXOut = ov.addOutPort(new DoubleDataflow(), true, ovMultX.getOutValue());

		DoubleMultiply ovMultY = ov.add(new DoubleMultiply());
		ov.connect(ySensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultY.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultY.getInSecond()));
		ovMultY.setSecond(1d);
		DataflowOutPort ovMultYOut = ov.addOutPort(new DoubleDataflow(), true, ovMultY.getOutValue());

		DoubleMultiply ovMultZ = ov.add(new DoubleMultiply());
		ov.connect(zSensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultZ.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultZ.getInSecond()));
		ovMultZ.setSecond(1d);
		DataflowOutPort ovMultZOut = ov.addOutPort(new DoubleDataflow(), true, ovMultZ.getOutValue());

		DoubleMultiply ovMultA = ov.add(new DoubleMultiply());
		ov.connect(aSensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultA.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultA.getInSecond()));
		ovMultA.setSecond(1d);
		DataflowOutPort ovMultAOut = ov.addOutPort(new DoubleDataflow(), true, ovMultA.getOutValue());

		DoubleMultiply ovMultB = ov.add(new DoubleMultiply());
		ov.connect(bSensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultB.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultB.getInSecond()));
		ovMultB.setSecond(1d);
		DataflowOutPort ovMultBOut = ov.addOutPort(new DoubleDataflow(), true, ovMultB.getOutValue());

		DoubleMultiply ovMultC = ov.add(new DoubleMultiply());
		ov.connect(cSensor.getSensorPort(), ov.addInPort(new DoubleDataflow(), true, ovMultC.getInFirst()));
		ov.connect(overridePort, ov.addInPort(new DoubleDataflow(), true, ovMultC.getInSecond()));
		ovMultC.setSecond(1d);
		DataflowOutPort ovMultCOut = ov.addOutPort(new DoubleDataflow(), true, ovMultC.getOutValue());

		// OTG for smooth acceleration
		NetFragment otgFragment = result.add(new NetFragment("OTG acceleration"));

		double maxTransVel = OTG_MAX_VEL;
		double maxRotVel = OTG_MAX_VEL;
		CartesianParameters cartesianParameter = parameters.get(CartesianParameters.class);
		if (cartesianParameter != null) {
			maxTransVel = cartesianParameter.getMaximumPositionAcceleration();
			maxRotVel = cartesianParameter.getMaximumRotationAcceleration();
		}

		OTG otgX = otgFragment.add(new OTG());
		otgX.setmaxAcc(OTG_MAX_ACC);
		otgX.setmaxVel(maxTransVel);
		otgFragment.connect(ovMultXOut, otgFragment.addInPort(new DoubleDataflow(), true, otgX.getInDestPos()));
		DataflowOutPort otgOutX = otgFragment.addOutPort(new DoubleDataflow(), true, otgX.getOutPos());

		OTG otgY = otgFragment.add(new OTG());
		otgY.setmaxAcc(OTG_MAX_ACC);
		otgY.setmaxVel(maxTransVel);
		otgFragment.connect(ovMultYOut, otgFragment.addInPort(new DoubleDataflow(), true, otgY.getInDestPos()));
		DataflowOutPort otgOutY = otgFragment.addOutPort(new DoubleDataflow(), true, otgY.getOutPos());

		OTG otgZ = otgFragment.add(new OTG());
		otgZ.setmaxAcc(OTG_MAX_ACC);
		otgZ.setmaxVel(maxTransVel);
		otgFragment.connect(ovMultZOut, otgFragment.addInPort(new DoubleDataflow(), true, otgZ.getInDestPos()));
		DataflowOutPort otgOutZ = otgFragment.addOutPort(new DoubleDataflow(), true, otgZ.getOutPos());

		OTG otgA = otgFragment.add(new OTG());
		otgA.setmaxAcc(OTG_MAX_ACC);
		otgA.setmaxVel(maxRotVel);
		otgFragment.connect(ovMultAOut, otgFragment.addInPort(new DoubleDataflow(), true, otgA.getInDestPos()));
		DataflowOutPort otgOutA = otgFragment.addOutPort(new DoubleDataflow(), true, otgA.getOutPos());

		OTG otgB = otgFragment.add(new OTG());
		otgB.setmaxAcc(OTG_MAX_ACC);
		otgB.setmaxVel(maxRotVel);
		otgFragment.connect(ovMultBOut, otgFragment.addInPort(new DoubleDataflow(), true, otgB.getInDestPos()));
		DataflowOutPort otgOutB = otgFragment.addOutPort(new DoubleDataflow(), true, otgB.getOutPos());

		OTG otgC = otgFragment.add(new OTG());
		otgC.setmaxAcc(OTG_MAX_ACC);
		otgC.setmaxVel(maxRotVel);
		otgFragment.connect(ovMultCOut, otgFragment.addInPort(new DoubleDataflow(), true, otgC.getInDestPos()));
		DataflowOutPort otgOutC = otgFragment.addOutPort(new DoubleDataflow(), true, otgC.getOutPos());

		// velocity combining
		NetFragment blocks = result.add(new NetFragment("Target velocity"));
		TwistFromVelocities twist = blocks.add(new TwistFromVelocities());
		VectorFromXYZ vel = blocks.add(new VectorFromXYZ(0d, 0d, 0d));
		VectorFromXYZ rot = blocks.add(new VectorFromXYZ(0d, 0d, 0d));
		twist.getInRotVel().connectTo(rot.getOutValue());
		twist.getInTransVel().connectTo(vel.getOutValue());

		blocks.connect(otgOutX, blocks.addInPort(new DoubleDataflow(), false, vel.getInX()));
		blocks.connect(otgOutY, blocks.addInPort(new DoubleDataflow(), false, vel.getInY()));
		blocks.connect(otgOutZ, blocks.addInPort(new DoubleDataflow(), false, vel.getInZ()));
		blocks.connect(otgOutA, blocks.addInPort(new DoubleDataflow(), false, rot.getInZ()));
		blocks.connect(otgOutB, blocks.addInPort(new DoubleDataflow(), false, rot.getInY()));
		blocks.connect(otgOutC, blocks.addInPort(new DoubleDataflow(), false, rot.getInX()));

		DataflowOutPort directionPort = blocks.addOutPort(new VelocityDataflow(action.getJoggedFrame(),
				action.getReferenceFrame(), action.getPivotPoint(), action.getOrientation()), false,
				twist.getOutValue());

		return new PlannedActionMapperResult(action, result, new CartesianPositionActionResult(directionPort),
				ports.cancelPort, null);
	}
}
