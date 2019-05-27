/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.RotationComponentSensor.RotationComponent;

public abstract class RotationSensor extends Sensor<Rotation> {

	public RotationSensor(RoboticsRuntime runtime) {
		super(runtime);
	}

	@Override
	protected Rotation getDefaultValue() {
		return new Rotation();
	}

	public DoubleSensor getASensor() {
		return new RotationComponentSensor(this, RotationComponent.A);
	}

	public DoubleSensor getBSensor() {
		return new RotationComponentSensor(this, RotationComponent.B);
	}

	public DoubleSensor getCSensor() {
		return new RotationComponentSensor(this, RotationComponent.C);
	}

	public DoubleSensor getQuaternionXSensor() {
		return new RotationComponentSensor(this, RotationComponent.QuaternionX);
	}

	public DoubleSensor getQuaternionYSensor() {
		return new RotationComponentSensor(this, RotationComponent.QuaternionY);
	}

	public DoubleSensor getQuaternionZSensor() {
		return new RotationComponentSensor(this, RotationComponent.QuaternionZ);
	}

	public DoubleSensor getQuaternionWSensor() {
		return new RotationComponentSensor(this, RotationComponent.QuaternionW);
	}

	public RotationSensor multiply(RotationSensor rotationSensor) {
		return new MultipliedRotationSensor(this, rotationSensor);
	}

	public RotationSensor multiply(Rotation rotation) {
		return new MultipliedRotationSensor(this, RotationSensor.fromConstant(rotation));
	}

	public RotationSensor multiply(TransformationSensor transformationSensor) {
		return multiply(transformationSensor.getRotationSensor());
	}

	public VectorSensor getAxisSensor() {
		return new AxisFromRotationSensor(this);
	}

	public DoubleSensor getAngleSensor() {
		return new AngleFromRotationSensor(this);
	}

	public BooleanSensor equals(RotationSensor otherSensor, double delta) {
		return otherSensor.invert().multiply(this).getAngleSensor().equals(0, delta);
	}

	public RotationSensor invert() {
		return new InvertedRotationSensor(this);
	}

	public static RotationSensor fromABC(DoubleSensor aComponent, DoubleSensor bComponent, DoubleSensor cComponent) {
		return new RotationFromABCSensor(aComponent, bComponent, cComponent);
	}

	public static RotationSensor fromConstant(Rotation orientation) {
		return new ConstantRotationSensor(orientation);
	}

	public static RotationSensor fromAxisAngle(VectorSensor axis, DoubleSensor angle) {
		return new RotationFromAxisAngleSensor(axis, angle);
	}

	public static RotationSensor fromAxisAngle(Vector axis, DoubleSensor angle) {
		return new RotationFromAxisAngleSensor(VectorSensor.fromConstant(axis), angle);
	}

	public static RotationSensor fromQuaternion(DoubleSensor x, DoubleSensor y, DoubleSensor z, DoubleSensor w) {
		return new RotationFromQuaternionSensor(x, y, z, w);
	}

	public RotationSensor slidingAverage(double duration) {
		return new SlidingAverageRotationSensor(this, duration);
	}

}
