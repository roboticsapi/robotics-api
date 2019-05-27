/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

public class Twist {

	private final Vector transVel;
	private final Vector rotVel;

	public Twist() {
		this(new Vector(), new Vector());
	}

	public Twist(double transX, double transY, double transZ, double rotX, double rotY, double rotZ) {
		this(new Vector(transX, transY, transZ), new Vector(rotX, rotY, rotZ));
	}

	public Twist(Vector transVel, Vector rotVel) {
		this.transVel = transVel;
		this.rotVel = rotVel;

	}

	public Twist(Transformation current, Transformation next, double timeDelta) {
		transVel = next.getTranslation().sub(current.getTranslation()).scale(1f / timeDelta);
		Rotation rot = current.getRotation().invert().multiply(next.getRotation());
		rotVel = current.getRotation().apply(rot.getAxis().scale(rot.getAngle() * 1f / timeDelta));
	}

	public Vector getTransVel() {
		return transVel;
	}

	public Vector getRotVel() {
		return rotVel;
	}

	/**
	 * Returns a new Twist that equals this Twist, expressed relative to a Frame
	 * with different orientation.
	 * 
	 * @param rotationDifference the rotation difference from this Twist's Frame to
	 *                           the new Frame
	 * @return the Twist relative to the new Frame
	 */
	public Twist changeOrientation(Rotation rotationDifference) {
		Rotation rotDiff = rotationDifference.invert();
		return new Twist(rotDiff.apply(transVel), rotDiff.apply(rotVel));
	}

	/**
	 * Returns a new Twist that expresses the same twist, expressed with another
	 * pivot point
	 * 
	 * @param pivotDifference the difference from this Twist's pivot point to the
	 *                        new pivot point (expressed in the same base as the
	 *                        twist)
	 * @return the Twist around another pivot point
	 */
	public Twist changePivot(Vector pivotDifference) {
		return new Twist(getTransVel().add(getRotVel().cross(pivotDifference)), getRotVel());
	}

	@Override
	public String toString() {
		return "Twist<T:" + getTransVel() + ",R:" + getRotVel() + ">";
	}

	public boolean isEqualTwist(Twist twist) {
		return getTransVel().isEqualVector(twist.getTransVel()) && getRotVel().isEqualVector(twist.getRotVel());
	}
}
