/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

/**
 * Implementation for the omni-move platforms with four Mecanum wheels.
 */
public abstract class AbstractMecanumPlatform<DD extends PlatformDriver> extends AbstractPlatform<DD>
		implements MecanumPlatform {

	/**
	 * Constructor.
	 */
	protected AbstractMecanumPlatform() {
		super(4);
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
	public final Frame getDefaultMotionCenter() {
		return getOdometryFrame();
	}

	@Override
	public final MecanumWheel getWheel(int wheelNumber) {
		return (MecanumWheel) super.getWheel(wheelNumber);
	}

	@Override
	public final MecanumWheel getFrontLeftWheel() {
		return getWheel(FRONT_LEFT_WHEEL);
	}

	@Override
	public final MecanumWheel getFrontRightWheel() {
		return getWheel(FRONT_RIGHT_WHEEL);
	}

	@Override
	public final MecanumWheel getBackLeftWheel() {
		return getWheel(BACK_LEFT_WHEEL);
	}

	@Override
	public final MecanumWheel getBackRightWheel() {
		return getWheel(BACK_RIGHT_WHEEL);
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
	protected final Connection createMountConnection(int wheelNumber) {
		switch (wheelNumber) {
		case FRONT_LEFT_WHEEL:
			return new StaticConnection(new Transformation(getWheelOrientation(), getFrontLeftWheelTranslation()));

		case FRONT_RIGHT_WHEEL:
			return new StaticConnection(new Transformation(getWheelOrientation(), getFrontRightWheelTranslation()));

		case BACK_LEFT_WHEEL:
			return new StaticConnection(new Transformation(getWheelOrientation(), getBackLeftWheelTranslation()));

		case BACK_RIGHT_WHEEL:
			return new StaticConnection(new Transformation(getWheelOrientation(), getBackRightWheelTranslation()));

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
	protected Vector getFrontLeftWheelTranslation() {
		return new Vector(+getFrontOffset(), +getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the translation from the base frame to the front right wheel's mount
	 * frame.
	 *
	 * @return the front right wheel's translation
	 */
	protected Vector getFrontRightWheelTranslation() {
		return new Vector(+getFrontOffset(), -getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the translation from the base frame to the back left wheel's mount
	 * frame.
	 *
	 * @return the back left wheel's translation
	 */
	protected Vector getBackLeftWheelTranslation() {
		return new Vector(-getFrontOffset(), +getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the translation from the base frame to the back right wheel's mount
	 * frame.
	 *
	 * @return the back right wheel's translation
	 */
	protected Vector getBackRightWheelTranslation() {
		return new Vector(-getFrontOffset(), -getLeftOffset(), getHeightOffset());
	}

	/**
	 * Returns the translation from the base frame to the wheels' mount frames. The
	 * z-axis should align to the wheels rotational axis and a positive rotation
	 * should move the platform forwards.
	 *
	 * @return the rotation
	 */
	protected abstract Rotation getWheelOrientation();

	@Override
	protected final Frame createMountFrame(int wheelNumber) {
		switch (wheelNumber) {
		case FRONT_LEFT_WHEEL:
			return new Frame(getName() + " - Front left mount frame");

		case FRONT_RIGHT_WHEEL:
			return new Frame(getName() + " - Front right mount frame");

		case BACK_LEFT_WHEEL:
			return new Frame(getName() + " - Back left mount frame");

		case BACK_RIGHT_WHEEL:
			return new Frame(getName() + " - Back right mount frame");

		default:
			return null;
		}
	}

	@Override
	protected final MecanumWheel createWheel(int wheelNumber) {
		switch (wheelNumber) {
		case FRONT_LEFT_WHEEL:
			return new MecanumWheel(getName() + " - Front left wheel");

		case FRONT_RIGHT_WHEEL:
			return new MecanumWheel(getName() + " - Front right wheel");

		case BACK_LEFT_WHEEL:
			return new MecanumWheel(getName() + " - Back left wheel");

		case BACK_RIGHT_WHEEL:
			return new MecanumWheel(getName() + " - Back right wheel");

		default:
			return null;
		}
	}

}
