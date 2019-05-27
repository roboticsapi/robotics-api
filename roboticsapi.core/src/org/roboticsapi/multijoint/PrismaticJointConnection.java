/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.RotationSensor;
import org.roboticsapi.world.sensor.TransformationFromComponentsSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VectorSensor;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * Relation belonging to a prismatic joint
 */
public class PrismaticJointConnection extends JointConnection {

	protected PrismaticJointConnection(Joint joint, JointDriver jointDriver) {
		super(joint, jointDriver);
	}

	@Override
	public RelationSensor getRelationSensor() {
		VectorSensor pos = VectorSensor.fromComponents(DoubleSensor.fromValue(0), DoubleSensor.fromValue(0),
				jointDriver.getCommandedPositionSensor());
		TransformationSensor trans = new TransformationFromComponentsSensor(pos,
				RotationSensor.fromConstant(new Rotation()));
		return new RelationSensor(trans, getFrom(), getTo());
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		VectorSensor pos = VectorSensor.fromComponents(DoubleSensor.fromValue(0), DoubleSensor.fromValue(0),
				jointDriver.getMeasuredPositionSensor());
		TransformationSensor trans = new TransformationFromComponentsSensor(pos,
				RotationSensor.fromConstant(new Rotation()));
		return new RelationSensor(trans, getFrom(), getTo());
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		VectorSensor transDir = VectorSensor.fromComponents(DoubleSensor.fromValue(0), DoubleSensor.fromValue(0),
				jointDriver.getCommandedVelocitySensor());
		DirectionSensor transVel = new DirectionSensor(transDir, getFrom().getOrientation());
		VectorSensor rotDir = VectorSensor.fromConstant(new Vector());
		DirectionSensor rotVel = new DirectionSensor(rotDir, getFrom().getOrientation());
		return new VelocityFromComponentsSensor(transVel, rotVel, getTo(), getFrom(), getFrom().getPoint());
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		VectorSensor transDir = VectorSensor.fromComponents(DoubleSensor.fromValue(0), DoubleSensor.fromValue(0),
				jointDriver.getMeasuredVelocitySensor());
		DirectionSensor transVel = new DirectionSensor(transDir, getFrom().getOrientation());
		VectorSensor rotDir = VectorSensor.fromConstant(new Vector());
		DirectionSensor rotVel = new DirectionSensor(rotDir, getFrom().getOrientation());
		return new VelocityFromComponentsSensor(transVel, rotVel, getTo(), getFrom(), getFrom().getPoint());
	}

	@Override
	public TransformationSensor getTransformationSensor(DoubleSensor position) {
		return TransformationSensor.fromComponents(
				VectorSensor.fromComponents(DoubleSensor.fromValue(0), DoubleSensor.fromValue(0), position),
				RotationSensor.fromConstant(new Rotation()));
	}
}
