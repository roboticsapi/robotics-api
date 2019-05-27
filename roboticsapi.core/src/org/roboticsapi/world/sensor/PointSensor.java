/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Vector;

public class PointSensor extends Sensor<Point> {

	private final Frame referenceFrame;
	private final VectorSensor vector;

	public PointSensor(VectorSensor vector, Frame referenceFrame) {
		super(selectRuntime(vector));
		addInnerSensors(vector);
		this.vector = vector;

		this.referenceFrame = referenceFrame;
	}

	@Override
	protected Point getDefaultValue() {
		return new Point(getReferenceFrame(), getVectorSensor().getDefaultValue());
	}

	@Override
	protected Point calculateCheapValue() {
		Vector cheapValue = getVectorSensor().getCheapValue();
		return cheapValue == null ? null : new Point(getReferenceFrame(), cheapValue);
	}

	public DoubleSensor getXSensor() {
		return getVectorSensor().getXSensor();
	}

	public DoubleSensor getYSensor() {
		return getVectorSensor().getYSensor();
	}

	public DoubleSensor getZSensor() {
		return getVectorSensor().getZSensor();
	}

	public PointSensor changeReference(Frame newReference) {
		RelationSensor relationSensor = newReference.getRelationSensor(getReferenceFrame());

		return new PointSensor(getVectorSensor().transform(relationSensor.getTransformationSensor()), newReference);
	}

	public PointSensor reinterpret(Frame newReference) {
		return new PointSensor(getVectorSensor(), newReference);
	}

	public static PointSensor fromComponents(Frame reference, DoubleSensor xComponent, DoubleSensor yComponent,
			DoubleSensor zComponent) {
		return new PointSensor(VectorSensor.fromComponents(xComponent, yComponent, zComponent), reference);
	}

	public static PointSensor fromConstant(Frame reference, Vector vector) {
		return new PointSensor(VectorSensor.fromConstant(vector), reference);
	}

	public Frame getReferenceFrame() {
		return referenceFrame;
	}

	public VectorSensor getVectorSensor() {
		return vector;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && referenceFrame.equals(((PointSensor) obj).referenceFrame)
				&& vector.equals(((PointSensor) obj).vector);
	}

	@Override
	public int hashCode() {
		return classHash(referenceFrame, vector);
	}

	@Override
	public boolean isAvailable() {
		return vector.isAvailable();
	}

	@Override
	public String toString() {
		return "point(" + referenceFrame + ", " + vector + ")";
	}
}
