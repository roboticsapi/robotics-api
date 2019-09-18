/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

/**
 * Implementation for the differential wheeled platforms.
 */
public abstract class AbstractDifferentialWheeledPlatform extends AbstractPlatform<PlatformDriver>
		implements DifferentialWheeledPlatform {

	/**
	 * Constructor.
	 */
	protected AbstractDifferentialWheeledPlatform() {
		super(2);
	}

	@Override
	public final Frame getReferenceFrame() {
		return getOdometryOrigin();
	}

	@Override
	public final Frame getMovingFrame() {
		return getOdometryFrame();
	}

	@Override
	public final Pose getDefaultMotionCenter() {
		return getOdometryFrame().asPose();
	}

	@Override
	public final Wheel getWheel(int wheelNumber) {
		return super.getWheel(wheelNumber);
	}

	@Override
	public final Wheel getLeftWheel() {
		return getWheel(LEFT_WHEEL);
	}

	@Override
	public final Wheel getRightWheel() {
		return getWheel(RIGHT_WHEEL);
	}

	/**
	 * Defines the odometry frame as motion center parameter and calls
	 * {@link #defineMoreDefaultParameters()} for setting further default
	 * parameters.
	 *
	 * @see MotionCenterParameter
	 */
	@Override
	protected final void defineDefaultParameters() throws InvalidParametersException {
		addDefaultParameters(new MotionCenterParameter(getOdometryFrame()));

		defineMoreDefaultParameters();
	}

	/**
	 * Subclasses can override this method to define more default parameters.
	 *
	 * @throws InvalidParametersException if the defined parameters are invalid.
	 */
	protected void defineMoreDefaultParameters() throws InvalidParametersException {
		// empty implementation
	}

	@Override
	protected final Transformation getMountTransformation(int wheelNumber) {
		switch (wheelNumber) {
		case LEFT_WHEEL:
			return new Transformation(getWheelOrientation(), getLeftWheelTranslation());
		case RIGHT_WHEEL:
			return new Transformation(getWheelOrientation(), getRightWheelTranslation());
		default:
			return null;
		}
	}

	/**
	 * Returns the offset for front wheels (on the x-axis of the platform's base
	 * frame). The offset for rear wheels is the negative front offset by default.
	 *
	 * @return the offset for front wheels
	 */
	protected abstract double getFrontOffset();

	/**
	 * Returns the offset for left wheels (on the y-axis of the platform's base
	 * frame). The offset for right wheels is the negative left offset by default.
	 *
	 * @return the offset for left wheels
	 */
	protected abstract double getLeftOffset();

	/**
	 * Returns the height offset for wheels (on the z-axis of the platform's base
	 * frame).
	 *
	 * @return the height offset
	 */
	protected abstract double getHeightOffset();

	/**
	 * Returns the translation from the base frame to the front left wheel's mount
	 * frame.
	 *
	 * @return the front left wheel's translation
	 */
	protected Vector getLeftWheelTranslation() {
		return new Vector(+getFrontOffset(), +getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the translation from the base frame to the front right wheel's mount
	 * frame.
	 *
	 * @return the front right wheel's translation
	 */
	protected Vector getRightWheelTranslation() {
		return new Vector(+getFrontOffset(), -getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the rotation from the base frame to the wheels' mount frames. The
	 * z-axis should align to the wheels rotational axis and a positive rotation
	 * should move the platform forwards.
	 *
	 * @return the rotation
	 */
	protected abstract Rotation getWheelOrientation();

	@Override
	protected final String createMountName(int wheelNumber) {
		switch (wheelNumber) {
		case LEFT_WHEEL:
			return "Left mount frame";

		case RIGHT_WHEEL:
			return "Right mount frame";

		default:
			return null;
		}
	}

	@Override
	protected final Wheel createWheel(int wheelNumber) {
		switch (wheelNumber) {
		case LEFT_WHEEL:
			return new DifferentialWheel(getName() + " - Left wheel");

		case RIGHT_WHEEL:
			return new DifferentialWheel(getName() + " - Right wheel");

		default:
			return null;
		}
	}

}
