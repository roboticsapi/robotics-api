/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.world.Transformation;

public final class JavaTransformationSensor extends TransformationFromComponentsSensor {

	public JavaTransformationSensor(RoboticsRuntime runtime) {
		super(new DoubleFromJavaSensor(0), new DoubleFromJavaSensor(0), new DoubleFromJavaSensor(0),
				new DoubleFromJavaSensor(0), new DoubleFromJavaSensor(0), new DoubleFromJavaSensor(0));
	}

	public void setTransformation(Transformation transformation) {
		setX(transformation.getTranslation().getX());
		setY(transformation.getTranslation().getY());
		setZ(transformation.getTranslation().getZ());
		setA(transformation.getRotation().getA());
		setB(transformation.getRotation().getB());
		setC(transformation.getRotation().getC());
	}

	public void setX(double x) {
		((DoubleFromJavaSensor) getX()).setValue(x);
	}

	public void setY(double y) {
		((DoubleFromJavaSensor) getY()).setValue(y);
	}

	public void setZ(double z) {
		((DoubleFromJavaSensor) getZ()).setValue(z);
	}

	public void setA(double a) {
		((DoubleFromJavaSensor) getA()).setValue(a);
	}

	public void setB(double b) {
		((DoubleFromJavaSensor) getB()).setValue(b);
	}

	public void setC(double c) {
		((DoubleFromJavaSensor) getC()).setValue(c);
	}
}
