/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

/**
 * Representation of a position and orientation in space, relative to a
 * reference {@link Frame} and expressed in a certain {@link Orientation}.
 */
public class Pose {

	private final Orientation orientation;
	private final Frame reference;
	private final Transformation transformation;

	/**
	 * Creates a new {@link Pose} as the position and orientation reached when
	 * starting at the given {@link Frame} and traveling the given
	 * {@link Transformation} along the given {@link Orientation}.
	 *
	 * @param orientation    {@link Orientation} to interpret the
	 *                       {@link Transformation} in
	 * @param reference      Reference {@link Frame} for the {@link Pose}
	 * @param transformation {@link Transformation} to move relative to the
	 *                       reference
	 */
	public Pose(Orientation orientation, Frame reference, Transformation transformation) {
		this.orientation = orientation == null ? new Orientation(null, Rotation.getIdentity()) : orientation;
		this.reference = reference;
		this.transformation = transformation;
	}

	/**
	 * Creates a new {@link Pose} as the position and orientation reached when
	 * starting at the given {@link Frame} and traveling the given
	 * {@link Transformation} (along the {@link Orientation} of the given reference
	 * {@link Frame}).
	 *
	 * @param reference      Reference {@link Frame} for the {@link Pose}
	 * @param transformation {@link Transformation} to move relative to the
	 *                       reference
	 */
	public Pose(Frame reference, Transformation transformation) {
		this(reference.asOrientation(), reference, transformation);
	}

	/**
	 * Creates a new {@link Pose} at the position and orientation of the given
	 * {@link Frame}.
	 *
	 * @param reference Reference {@link Frame} for the {@link Pose}
	 */
	public Pose(Frame reference) {
		this(reference, Transformation.IDENTITY);
	}

	/**
	 * Gets the reference {@link Frame} of this {@link Pose}
	 *
	 * @return reference {@link Frame}
	 */
	public Frame getReference() {
		return reference;
	}

	/**
	 * Gets the {@link Transformation} of the {@link Pose} (relative to its
	 * reference {@link Frame} and expressed in its {@link Orientation}.
	 *
	 * @return transformation of the {@link Pose}
	 */
	public Transformation getTransformation() {
		return transformation;
	}

	/**
	 * Gets the {@link Transformation} of the pose (relative to its reference
	 * {@link Frame} expressed in the given {@link Orientation}.
	 *
	 * @param orientation orientation to get the {@link Transformation} to
	 * @return transformation of the {@link Pose} relative to its reference
	 *         {@link Frame}, expressed in the given {@link Orientation}
	 * @throws TransformationException if the orientation cannot be converted
	 */
	public Transformation getTransformationForRepresentation(Orientation orientation) throws TransformationException {
		if (this.orientation.isEqualOrientation(orientation)) {
			return transformation;
		}
		Rotation oriChange = getLocalOrientationAsPose(orientation)
				.getCommandedTransformationTo(getLocalOrientationAsPose(this.orientation)).getRotation();
		Transformation ori = new Transformation(oriChange, Vector.getNullVector());
		return ori.multiply(transformation.multiply(ori.invert()));
	}

	/**
	 * Gets the {@link Orientation} this {@link Pose}'s {@link Transformation} is
	 * expressed in. Note: This is not the orientation part of this {@link Pose},
	 * but the orientation this {@link Pose}'s {@link Transformation} is to be
	 * interpreted in.
	 *
	 * @return {@link Orientation} this {@link Pose}'s {@link Transformation} is
	 *         expressed in
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Provides the {@link Orientation} described by this {@link Pose}. Note: This
	 * is not the {@link Orientation} the {@link Pose}'s {@link Transformation} is
	 * to be interpreted in, but the {@link Orientation} of the place described by
	 * this {@link Pose}.
	 *
	 * @return {@link Orientation} described by this {@link Pose}
	 */
	public Orientation asOrientation() throws TransformationException {
		return new Orientation(getReference(),
				getTransformationForRepresentation(reference.asOrientation()).getRotation());
	}

	/**
	 * Provides the {@link Point} described by this {@link Pose}.
	 *
	 * @return {@link Point} described by this {@link Pose}
	 */
	public Point asPoint() {
		return new Point(getReference(), getOrientation(), getTransformation().getTranslation());
	}

	@Override
	public String toString() {
		return "Pose(R: " + reference
				+ (!reference.asOrientation().isEqualOrientation(orientation) ? ", O: " + orientation + "" : "") + ", "
				+ transformation + ")";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Pose && orientation.equals(((Pose) obj).orientation)
				&& reference.equals(((Pose) obj).reference) && transformation.equals(((Pose) obj).transformation);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, orientation), reference),
				transformation);
	}

	/**
	 * Calculates a {@link Pose} for an {@link Orientation}. A local
	 * {@link Orientation} (with reference <code>null</code>) is interpreted as an
	 * {@link Orientation} that is defined based on this {@link Pose}'s point and
	 * orientation.
	 *
	 * @param orientation (local) {@link Orientation} to calculate a {@link Pose}
	 *                    for.
	 * @return {@link Pose} describing the given {@link Orientation} when
	 *         interpreted in the context of this {@link Pose}.
	 * @throws TransformationException if the {@link Pose} cannot be computed
	 */
	public Pose getLocalOrientationAsPose(Orientation orientation) throws TransformationException {
		if (orientation == null) {
			return this;
		} else if (orientation.getReferenceFrame() == null) {
			return new Pose(orientation, reference,
					transformation.multiply(new Transformation(orientation.getRotation(), Vector.ZERO)));
		} else {
			return orientation.asPose();
		}
	}

	/**
	 * Calculates a {@link Pose} for a {@link Point}. A local {@link Point} (with
	 * reference <code>null</code>) is interpreted as an {@link Point} that is
	 * defined based on this {@link Pose}'s point and orientation.
	 *
	 * @param point (local) {@link Point} to calculate a {@link Pose} for.
	 * @return {@link Pose} describing the given {@link Point} when interpreted in
	 *         the context of this {@link Pose}.
	 * @throws TransformationException if the {@link Pose} cannot be computed
	 */
	public Pose getLocalPointAsPose(Point point) throws TransformationException {
		if (point == null) {
			return this;
		} else if (point.getReferenceFrame() == null) {
			return plus(getLocalOrientationAsPose(point.getOrientation()).asOrientation(), point.getVector());
		} else {
			return point.getReferenceFrame().asPose().plus(point.getVector());
		}
	}

	/**
	 * Converts this {@link Pose} into a constant {@link RealtimePose}.
	 *
	 * @return this {@link Pose} as a constant {@link RealtimePose}.
	 */
	public RealtimePose asRealtimeValue() {
		return RealtimePose.createFromTransformation(orientation, reference,
				RealtimeTransformation.createfromConstant(transformation));
	}

	/* former frame stuff */

	/**
	 * Creates a new Pose that is displaced by the given values relative to this
	 * Pose.
	 *
	 * @param x translation along x-axis of this Pose
	 * @param y translation along y-axis of this Pose
	 * @param z translation along z-axis of this Pose
	 * @param a rotation around z-axis of this Pose
	 * @param b rotation around y-axis of this Pose
	 * @param c rotation around x-axis of this Pose
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 */
	public final Pose plus(double x, double y, double z, double a, double b, double c) throws TransformationException {
		return plus(new Vector(x, y, z), new Rotation(a, b, c));
	}

	/**
	 * Creates a new Pose that is displaced by the given values relative to this
	 * Pose.
	 *
	 * @param x translation along x-axis of this Pose
	 * @param y translation along y-axis of this Pose
	 * @param z translation along z-axis of this Pose
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 *
	 */
	public final Pose plus(double x, double y, double z) throws TransformationException {
		return plus(x, y, z, 0, 0, 0);
	}

	/**
	 * Creates a new Pose that is displaced by the given {@link Vector} relative to
	 * this Pose.
	 *
	 * @param translation the translation vector
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 */
	public final Pose plus(Vector translation) throws TransformationException {
		return plus(translation, Rotation.IDENTITY);
	}

	/**
	 * Creates a new Pose that is rotated by the given {@link Rotation} relative to
	 * this Pose.
	 *
	 * @param rotation the Rotation
	 * @return the newly constructed Frame
	 * @throws TransformationException
	 */
	public final Pose plus(Rotation rotation) throws TransformationException {
		return plus(Vector.getNullVector(), rotation);
	}

	/**
	 * Creates a new Pose that is displaced by the given Vector and the given
	 * {@link Rotation} relative to this Pose.
	 *
	 * @param translation the translation vector
	 * @param rotation    the rotation
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 */
	public final Pose plus(Vector translation, Rotation rotation) throws TransformationException {
		return plus(new Transformation(rotation, translation));
	}

	/**
	 * Creates a new Pose that is displaced by the given {@link Transformation}
	 * relative to this Pose.
	 *
	 * @param transformation the Transformation between this Pose and the new Pose
	 * @return the newly constructed Pose
	 */
	public Pose plus(Transformation transformation) {
		try {
			return plus(asOrientation(), transformation);
		} catch (TransformationException e) {
			// May never happen... (hopefully)
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Creates a new Pose that is displaced by the given {@link Vector} relative to
	 * this Pose. The displacement is interpreted with respect to the given
	 * {@link Orientation}.
	 *
	 * Example usage: lwr.getFlange().asPose().plus(lwr.getBase().getOrientation(),
	 * new Vector(1, 0, 0));
	 *
	 * This creates a new Pose, attached to lwr's flange, which is displaced by 1m
	 * in x direction of lwr's base. Note that the orientation is taken at creation
	 * time of the Pose, so after moving the flange the Pose will no longer point in
	 * the base X direction.
	 *
	 * @param translation the translational displacement
	 * @param reference   the reference orientation for applying the displacement
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 * @throws RoboticsException       thrown if the effective displacement could
	 *                                 not be calculated
	 */
	public Pose plus(Orientation orientation, Vector translation) throws TransformationException {
		return plus(orientation, new Transformation(Rotation.getIdentity(), translation));
	}

	/**
	 * Creates a new Pose that is displaced by the given {@link Transformation}
	 * relative to this Pose. The Transformation is interpreted with respect to the
	 * given {@link Orientation}.
	 *
	 * Note that the orientation is taken at creation time of the Pose.
	 *
	 * @param transformation the displacement
	 * @param reference      the reference orientation for applying the displacement
	 * @return the newly constructed Pose
	 * @throws TransformationException
	 * @throws RoboticsException       thrown if the effective displacement could
	 *                                 not be calculated
	 */
	public Pose plus(Orientation orientation, Transformation transformation) throws TransformationException {

		return new Pose(this.orientation, reference,
				this.transformation.multiply(new Pose(orientation, getReference(), transformation)
						.getTransformationForRepresentation(asOrientation())));
	}

	/**
	 * Creates a new Pose that is displaced by the given translation values relative
	 * to this Pose. The displacement is interpreted with respect to the given
	 * {@link Orientation}.
	 *
	 * Example usage: lwr.getFlange().plus( lwr.getBase().getOrientation(), 1, 0,
	 * 0);
	 *
	 * This creates a new Frame, attached to lwr's flange, which is displaced by 1m
	 * in x direction of lwr's base. Note that the Orientation is evaluated at
	 * creation time of this Pose.
	 *
	 * @param x         translation in x direction
	 * @param y         translation in y direction
	 * @param z         translation in z direction
	 * @param reference the reference orientation for applying the displacement
	 * @return the newly constructed Frame
	 * @throws RoboticsException thrown if the effective displacement could not be
	 *                           calculated
	 */
	public final Pose plus(Orientation reference, double x, double y, double z) throws RoboticsException {
		return plus(reference, new Vector(x, y, z));
	}

	/**
	 * Returns a new Pose that "snapshots" (i.e., holds constant) the current
	 * (commanded) geometric state of this Pose with respect to a given reference
	 * Frame at the current point in time.
	 *
	 * Use this method to conserve the state of Pose that are changing over time
	 * (like the robot flange frame).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * target values are considered instead of actual, measured values.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this Pose with respect to the given
	 *         reference frame.
	 * @throws RoboticsException
	 */
	public Pose snapshot(final Frame referenceFrame) throws RoboticsException {
		return new Pose(referenceFrame,
				referenceFrame.asPose().getTransformationTo(this, World.getCommandedTopology()));
	}

	/**
	 * Returns a new Pose that "snapshots" (i.e., holds constant) the current
	 * (measured) geometric state of this Pose with respect to a given reference
	 * Frame at the current point in time.
	 *
	 * Use this method to conserve the state of frames that are changing over time
	 * (like the robot flange frame).
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this Pose with respect to the given
	 *         reference frame.
	 * @throws RoboticsException
	 */
	public Pose snapshotMeasured(final Frame referenceFrame) throws RoboticsException {
		return new Pose(referenceFrame, referenceFrame.asPose().getTransformationTo(this, World.getMeasuredTopology()));
	}

	/**
	 * Retrieves the commanded transformation to the given Pose
	 *
	 * @param to target pose
	 * @return transformation from this Pose to the given Pose
	 * @throws TransformationException
	 */
	public Transformation getCommandedTransformationTo(Pose to) throws TransformationException {
		return getTransformationTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves the measured transformation to the given Pose.
	 *
	 * Uses World.getMeasuredTopology() to determine the transformation.
	 *
	 * @param to target pose
	 * @return transformation from this Pose to the given Pose
	 * @throws TransformationException
	 */
	public Transformation getMeasuredTransformationTo(Pose to) throws TransformationException {
		return getTransformationTo(to, World.getMeasuredTopology());
	}

	/**
	 * Retrieves the commanded transformation to the given Pose. Same as
	 * getCommandedTransformationTo(Pose to).
	 *
	 * @param to target pose
	 * @return transformation from this Pose to the given Pose
	 * @throws TransformationException
	 */
	public Transformation getTransformationTo(Pose to) throws TransformationException {
		return getCommandedTransformationTo(to);
	}

	/**
	 * Retrieves the transformation to the given Pose in the given FrameTopology
	 *
	 * @param to       target pose
	 * @param topology frame topology to use
	 * @return transformation from this Pose to the given Pose
	 * @throws TransformationException
	 */
	public Transformation getTransformationTo(Pose to, FrameTopology topology) throws TransformationException {
		return getTransformationForRepresentation(reference.asOrientation()).invert()
				.multiply(getReference().getTransformationTo(to.getReference(), topology))
				.multiply(to.getTransformationForRepresentation(to.getReference().asOrientation()));
	}

	public Transformation getTransformationForRepresentation(Frame newReference, FrameTopology topology)
			throws TransformationException {
		return getTransformationForRepresentation(newReference.asOrientation(), newReference, topology);
	}

	public Transformation getTransformationForRepresentation(Orientation newOrientation, Frame newReference,
			FrameTopology topology) throws TransformationException {
		Transformation trans = transformation;

		if (!reference.asOrientation().equals(orientation)) {
			Rotation oriChange = getLocalOrientationAsPose(orientation).getCommandedTransformationTo(reference.asPose())
					.getRotation();
			Transformation ori = new Transformation(oriChange, Vector.getNullVector());
			trans = ori.multiply(trans.multiply(ori.invert()));
		}

		if (newReference != reference) {
			trans = newReference.getTransformationTo(reference, topology).multiply(trans);
		}

		if (!newOrientation.equals(newReference.asOrientation())) {
			Rotation oriChange = reference.asPose()
					.getCommandedTransformationTo(getLocalOrientationAsPose(newOrientation)).getRotation();
			Transformation ori = new Transformation(oriChange, Vector.getNullVector());
			trans = ori.multiply(trans.multiply(ori.invert()));
		}

		return trans;
	}

	public Pose convertToRepresentation(Orientation newOrientation, Frame newReference, FrameTopology topology)
			throws TransformationException {
		return new Pose(newOrientation, newReference,
				getTransformationForRepresentation(newOrientation, newReference, topology));
	}

	public Pose reinterpretToRepresentation(Orientation newOrientation, Frame newReference, FrameTopology topology) {
		return new Pose(newOrientation, newReference, transformation);
	}

}
