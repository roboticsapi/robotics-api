/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Twist;

/**
 * {@link Sensor} measuring the relative velocity (as {@link Twist}) of a
 * {@link Frame}.
 * 
 * A Twist value has to be interpreted w.r.t. the following parameters, which
 * are also part of the VelocitySensor's definition:
 * 
 * <ul>
 * <li><b>Moving Frame</b>: The Frame whose relative velocity is expressed,</li>
 * <li><b>Reference Frame</b>: the Frame relative to which the Moving Frame
 * moves (this Frame may itself be moving),</li>
 * <li><b>Pivot Point</b>: the point in space that is interpreted as a
 * rotational center,</li>
 * <li><b>Orientation</b>: the orientation that determines the axes for
 * translational and rotational velocity.</li>
 * </ul>
 */
public abstract class VelocitySensor extends Sensor<Twist> {

	private final Frame moving;
	private final Frame reference;
	private final Point pivotPoint;
	private final Orientation orientation;

	/**
	 * Instantiates a new velocity sensor.
	 * 
	 * @param runtime        the {@link RoboticsRuntime} this VelocitySensor runs on
	 * @param movingFrame    the Moving Frame
	 * @param referenceFrame the Reference Frame
	 * @param pivotPoint     the Pivot Point
	 * @param orientation    the Orientation
	 */
	public VelocitySensor(RoboticsRuntime runtime, Frame movingFrame, Frame referenceFrame, Point pivotPoint,
			Orientation orientation) {
		super(runtime);
		this.moving = movingFrame;
		this.reference = referenceFrame;
		this.pivotPoint = pivotPoint;
		this.orientation = orientation;
	}

	/**
	 * Gets the Moving Frame this VelocitySensor works with.
	 * 
	 * @return the moving frame
	 */
	public Frame getMovingFrame() {
		return moving;
	}

	/**
	 * Gets the Reference Frame this VelocitySensor works with.
	 * 
	 * @return the reference frame
	 */
	public Frame getReferenceFrame() {
		return reference;
	}

	/**
	 * Inverts this {@link VelocitySensor}, i.e. creates a new VelocitySensor that
	 * measures the velocity of this sensor's Reference Frame relative to this
	 * sensor's Moving Frame w.r.t. the same Pivot Point and Orientation.
	 * 
	 * @return a new VelocitySensor that represents the inverted version of this
	 *         VelocitySensor
	 */
	public VelocitySensor invert() {
		return new InvertedVelocitySensor(this);
	}

	/**
	 * Adds the given {@link VelocitySensor} to this VelocitySensor, i.e. creates a
	 * new VelocitySensor that measures the velocity of the given sensor's Moving
	 * Frame relative to this sensor's Reference Frame w.r.t. this sensor's Pivot
	 * Point and Orientation.
	 * 
	 * @param other the VelocitySensor to add to this VelocitySensor
	 * @return a new VelocitySensor measuring the total velocity
	 */
	public VelocitySensor add(VelocitySensor other) {
		return new AddedVelocitySensor(this, other);
	}

	/**
	 * Changes the Pivot Point of this {@link VelocitySensor}, i.e. creates a new
	 * VelocitySensor that measures the velocity of this sensor's Moving Frame
	 * relative to this sensor's Reference Frame w.r.t. the given new Pivot Point
	 * and this sensor's Orientation.
	 * 
	 * @param newPivot the new Pivot Point
	 * @return a new VelocitySensor measuring the velocity w.r.t. the new Pivot
	 *         Point
	 */
	public VelocitySensor changePivotPoint(Point newPivot) {
		if (getPivotPoint().isEqualPoint(newPivot)) {
			return this;
		} else {
			return new PivotAdaptedVelocitySensor(this, newPivot);
		}
	}

	/**
	 * Changes the Orientation of this {@link VelocitySensor}, i.e. creates a new
	 * VelocitySensor that measures the velocity of this sensor's Moving Frame
	 * relative to this sensor's Reference Frame w.r.t. this sensor's Pivot Point
	 * and the given new Orientation.
	 * 
	 * @param newOrientation the new Orientation
	 * @return a new VelocitySensor measuring the velocity w.r.t. the new
	 *         Orientation
	 */
	public VelocitySensor changeOrientation(Orientation newOrientation) {
		if (getOrientation().isEqualOrientation(newOrientation)) {
			return this;
		} else {
			return new OrientationAdaptedVelocitySensor(this, newOrientation);
		}
	}

	public DirectionSensor getTranslationVelocitySensor() {
		return new DirectionSensor(new TransVelVectorFromVelocitySensor(this), orientation);
	}

	public DirectionSensor getRotationVelocitySensor() {
		return new DirectionSensor(new RotVelVectorFromVelocitySensor(this), orientation);
	}

	/**
	 * Gets the Pivot Point of this {@link VelocitySensor}.
	 * 
	 * @return the Pivot Point
	 */
	public Point getPivotPoint() {
		return pivotPoint;
	}

	/**
	 * Gets the Orientation of this {@link VelocitySensor}.
	 * 
	 * @return the Orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.sensor.Sensor#getDefaultValue()
	 */
	@Override
	protected Twist getDefaultValue() {
		return new Twist();
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && pivotPoint.equals(((VelocitySensor) other).pivotPoint)
				&& reference.equals(((VelocitySensor) other).reference)
				&& moving.equals(((VelocitySensor) other).moving)
				&& orientation.equals(((VelocitySensor) other).orientation);
	}

	@Override
	public int hashCode() {
		return classHash(pivotPoint, reference, moving, orientation);
	}

	/**
	 * A velocity sensor for a constant velocity
	 * 
	 * @param movingFrame    moving frame of velocity
	 * @param referenceFrame reference frame of velocity
	 * @param pivotPoint     pivot point of velocity
	 * @param orientation    orientation of velocity
	 * @param constantValue  constant velocity value
	 * @return a constant velocity sensor
	 */
	public static VelocitySensor fromConstant(Twist constantValue, Frame movingFrame, Frame referenceFrame,
			Point pivotPoint, Orientation orientation) {
		return new ConstantVelocitySensor(movingFrame, referenceFrame, pivotPoint, orientation, constantValue);
	}

	/**
	 * Combines a velocity sensor from translational/rotational motion directions
	 * 
	 * @param translationVelocitySensor tranlational motion direction (speed)
	 * @param rotationVelocitySensor    rotational motion description (direction =
	 *                                  axis, length = speed)
	 * @param movingFrame               moving frame of the velocity
	 * @param referenceFrame            reference frame of the velocity
	 * @param pivotPoint                pivot point of the velocity
	 * @return the combined velocity sensor
	 */
	public static VelocitySensor fromComponents(DirectionSensor translationVelocitySensor,
			DirectionSensor rotationVelocitySensor, Frame movingFrame, Frame referenceFrame, Point pivotPoint) {
		return new VelocityFromComponentsSensor(translationVelocitySensor, rotationVelocitySensor, movingFrame,
				referenceFrame, pivotPoint);
	}

}
