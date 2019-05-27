/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.world.sensor.JavaTransformationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;

/**
 * A {@link TransformationSensorConnection} whose {@link Transformation} can be
 * altered from the application and is updated also in running {@link Command}s.
 */
public class JavaTransformationSensorConnection extends TransformationSensorConnection {

	/**
	 * Creates a new {@link JavaTransformationSensorConnection} which is valid for
	 * the given {@link RoboticsRuntime}.
	 * 
	 * @param runtime the runtime
	 */
	public JavaTransformationSensorConnection(RoboticsRuntime runtime) {
		super(TransformationSensor.fromJava(runtime), null);

	}

	/**
	 * Sets the {@link Transformation} of this Relation. The Transformation value is
	 * propagated to running {@link Command}s.
	 * 
	 * @param transformation the new Transformation
	 */
	public void setTransformation(Transformation transformation) {
		getTransformationSensor().setTransformation(transformation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.world.TransformationSensorConnection#getTransformationSensor
	 * ()
	 */
	@Override
	public JavaTransformationSensor getTransformationSensor() {
		return (JavaTransformationSensor) super.getTransformationSensor();
	}

	/**
	 * Sets the translation along x axis of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param x the new x
	 */
	public void setX(double x) {
		getTransformationSensor().setX(x);
	}

	/**
	 * Sets the translation along y axis of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param y the new y
	 */
	public void setY(double y) {
		getTransformationSensor().setY(y);
	}

	/**
	 * Sets the translation along z axis of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param z the new z
	 */
	public void setZ(double z) {
		getTransformationSensor().setZ(z);
	}

	/**
	 * Sets the rotation around z axis (A) of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param a the new a
	 */
	public void setA(double a) {
		getTransformationSensor().setA(a);
	}

	/**
	 * Sets the rotation around y axis (B) of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param b the new b
	 */
	public void setB(double b) {
		getTransformationSensor().setB(b);
	}

	/**
	 * Sets the rotation around x axis (C) of this Relation. The new Transformation
	 * value is propagated to running {@link Command}s.
	 * 
	 * @param c the new c
	 */
	public void setC(double c) {
		getTransformationSensor().setC(c);
	}

}
