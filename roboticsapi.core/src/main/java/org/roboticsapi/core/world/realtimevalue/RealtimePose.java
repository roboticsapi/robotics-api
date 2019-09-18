/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue;

import java.util.Map;

import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Orientation;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * Representation of a (time-variable) position and orientation in space,
 * relative to a reference {@link Frame} and expressed in a certain
 * {@link Orientation}. ({@link RealtimeValue} version of {@link Pose})
 */
public final class RealtimePose extends RealtimeValue<Pose> {
	@Override
	public final RealtimePose substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimePose) substitutionMap.get(this);
		}
		return new RealtimePose(orientation, reference, transformation.substitute(substitutionMap));
	}

	private final Orientation orientation;
	private final Frame reference;
	private final RealtimeTransformation transformation;

	/**
	 * Creates a new {@link RealtimePose} as the position and orientation reached
	 * when starting at the given {@link Frame} and traveling the given
	 * {@link RealtimeTransformation} along the given {@link Orientation}.
	 *
	 * @param orientation    {@link Orientation} to interpret the
	 *                       {@link Transformation} in
	 * @param reference      Reference {@link Frame} for the {@link Pose}
	 * @param transformation {@link RealtimeTransformation} to move relative to the
	 *                       reference
	 */
	RealtimePose(Orientation orientation, Frame reference, RealtimeTransformation transformation) {
		super(transformation);
		this.orientation = orientation == null ? new Orientation(null, Rotation.getIdentity()) : orientation;
		this.reference = reference;
		this.transformation = transformation;
	}

	/**
	 * Creates a new {@link RealtimePose} as the position and orientation reached
	 * when starting at the given {@link Frame} and traveling the given
	 * {@link RealtimeTransformation} (along the {@link Orientation} of the given
	 * reference {@link Frame}).
	 *
	 * @param reference      Reference {@link Frame} for the {@link Pose}
	 * @param transformation {@link RealtimeTransformation} to move relative to the
	 *                       reference
	 */
	RealtimePose(Frame reference, RealtimeTransformation transformation) {
		this(reference.asOrientation(), reference, transformation);
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
	 * Gets the {@link RealtimeTransformation} of the {@link Pose} (relative to its
	 * reference {@link Frame} and expressed in its {@link Orientation}.
	 *
	 * @return transformation of the {@link RealtimePose}
	 */
	public RealtimeTransformation getTransformation() {
		return transformation;
	}

	/**
	 * Gets the {@link RealtimeTransformation} of the pose (relative to its
	 * reference {@link Frame} expressed in the given {@link Orientation}.
	 *
	 * @param orientation orientation to get the {@link Transformation} to
	 * @return transformation of the {@link Pose} relative to its reference
	 *         {@link Frame}, expressed in the given {@link Orientation}
	 * @throws TransformationException if the orientation cannot be converted
	 * @deprecated does not specify the {@link FrameTopology} to use for the
	 *             computation; use
	 *             {@link RealtimePose#getTransformationForRepresentation(RealtimeOrientation, FrameTopology)}
	 *             instead
	 */
	@Deprecated
	public RealtimeTransformation getTransformationForRepresentation(Orientation orientation)
			throws TransformationException {
		return getTransformationForRepresentation(orientation, getReference(), World.getCommandedTopology());
	}

	/**
	 * Gets the {@link RealtimeTransformation} of the pose (relative to its
	 * reference {@link Frame} expressed in the given {@link RealtimeOrientation}.
	 *
	 * @param orientation orientation to get the {@link Transformation} to
	 * @return transformation of the {@link Pose} relative to its reference
	 *         {@link Frame}, expressed in the given {@link RealtimeOrientation}
	 * @throws TransformationException if the orientation cannot be converted
	 * @deprecated does not specify the {@link FrameTopology} to use for the
	 *             computation; use
	 *             {@link RealtimePose#getTransformationForRepresentation(RealtimeOrientation, FrameTopology)}
	 *             instead
	 */
	@Deprecated
	public RealtimeTransformation getTransformationForRepresentation(RealtimeOrientation orientation)
			throws TransformationException {
		return getTransformationForRepresentation(orientation, World.getCommandedTopology());
	}

	/**
	 * Gets the {@link RealtimeTransformation} of the pose (relative to its
	 * reference {@link Frame} expressed in the given {@link RealtimeOrientation}.
	 *
	 * @param orientation orientation to get the {@link Transformation} to
	 * @param topology    frame topology to use to convert between the orientations
	 * @return transformation of the {@link Pose} relative to its reference
	 *         {@link Frame}, expressed in the given {@link RealtimeOrientation}
	 * @throws TransformationException if the orientation cannot be converted
	 */
	public RealtimeTransformation getTransformationForRepresentation(RealtimeOrientation orientation,
			FrameTopology topology) throws TransformationException {
		if (getRuntime() != null) {
			topology = topology.forRuntime(getRuntime());
		}

		if (orientation.equals(getOrientation().asRealtimeValue())) {
			return transformation;
		}

		RealtimeTransformation orientationChange = getLocalOrientationAsPose(getOrientation().asRealtimeValue())
				.getRealtimeTransformationTo(getLocalOrientationAsPose(orientation), topology);
		return transformation.changeOrientation(orientationChange.getRotation());
	}

	/**
	 * Gets the {@link RealtimeTransformation} of the pose (relative to the given
	 * reference {@link Frame}, expressed in the given {@link Frame} as
	 * {@link Orientation}.
	 *
	 * @param reference reference frame for the {@link Transformation}
	 * @param topology  frame topology to use to convert between the orientations
	 * @return transformation of the {@link Pose} relative to the given reference
	 *         {@link Frame}, expressed in the reference {@link Orientation}
	 * @throws TransformationException if the orientation cannot be converted
	 */
	public RealtimeTransformation getTransformationForRepresentation(Frame reference, FrameTopology topology)
			throws TransformationException {
		return getTransformationForRepresentation(reference.asOrientation(), reference, topology);
	}

	@Override
	public boolean isAvailable() {
		return transformation.isAvailable();
	}

	@Override
	protected Pose calculateCheapValue() {
		return wrap(transformation.getCheapValue());
	}

	private Pose wrap(Transformation newValue) {
		if (newValue == null) {
			return null;
		} else {
			return new Pose(orientation, reference, newValue);
		}
	}

	/**
	 * Provides the {@link RealtimeOrientation} described by this
	 * {@link RealtimePose}. Note: This is not the {@link RealtimeOrientation} the
	 * {@link Pose}'s {@link RealtimeTransformation} is to be interpreted in, but
	 * the {@link RealtimeOrientation} of the place described by this
	 * {@link RealtimePose}.
	 *
	 * @return {@link RealtimeOrientation} described by this {@link Pose}
	 * @deprecated does not specify the {@link FrameTopology} to use for the
	 *             computation; use
	 *             {@link RealtimePose#asRealtimeOrientation(FrameTopology)} instead
	 */
	@Deprecated
	public RealtimeOrientation asRealtimeOrientation() throws TransformationException {
		return new RealtimeOrientation(
				getTransformationForRepresentation(reference.asRealtimeOrientation()).getRotation(), getReference());
	}

	/**
	 * Provides the {@link RealtimeOrientation} described by this
	 * {@link RealtimePose}. Note: This is not the {@link RealtimeOrientation} the
	 * {@link Pose}'s {@link RealtimeTransformation} is to be interpreted in, but
	 * the {@link RealtimeOrientation} of the place described by this
	 * {@link RealtimePose}.
	 *
	 * @param topology {@link FrameTopology} to use to convert the reference and
	 *                 orientation
	 * @return {@link RealtimeOrientation} described by this {@link Pose}
	 */
	public RealtimeOrientation asRealtimeOrientation(FrameTopology topology) throws TransformationException {
		return new RealtimeOrientation(
				getTransformationForRepresentation(reference.asRealtimeOrientation(), topology).getRotation(),
				getReference());
	}

	/**
	 * Provides the {@link RealtimePoint} described by this {@link RealtimePose}.
	 *
	 * @return {@link RealtimePoint} described by this {@link RealtimePose}
	 */
	public RealtimePoint asRealtimePoint() {
		return new RealtimePoint(getTransformation().getTranslation(), getReference(), getOrientation());
	}

	/**
	 * Gets the {@link Orientation} this {@link RealtimePose}'s
	 * {@link RealtimeTransformation} is expressed in. Note: This is not the
	 * orientation part of this {@link RealtimePose}, but the orientation this
	 * {@link RealtimePose}'s {@link RealtimeTransformation} is to be interpreted
	 * in.
	 *
	 * @return {@link Orientation} this {@link RealtimePose}'s
	 *         {@link RealtimeTransformation} is expressed in
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public boolean isConstant() {
		return transformation.isConstant();
	}

	@Override
	public Observer<?> createObserver(final RealtimeValueListener<Pose> listener, RealtimeBoolean condition,
			boolean async) {
		return getTransformation().createObserver(new RealtimeValueListener<Transformation>() {
			@Override
			public void onValueChanged(Transformation newValue) {
				listener.onValueChanged(wrap(newValue));
			}

		}, condition, async);
	}

	@Override
	public RealtimeBoolean isNull() {
		return transformation.isNull();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && nullOrEqual(orientation, ((RealtimePose) obj).orientation)
				&& nullOrEqual(reference, ((RealtimePose) obj).reference);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), orientation, reference);
	}

	@Override
	public RealtimePose fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimePose(reference, transformation.fromHistory(age, maxAge));
	}

	/**
	 * Calculates a {@link RealtimePose} for an {@link Orientation}. A local
	 * {@link Orientation} (with reference <code>null</code>) is interpreted as an
	 * {@link Orientation} that is defined based on this {@link RealtimePose}'s
	 * point and orientation.
	 *
	 * @param orientation (local) {@link Orientation} to calculate a
	 *                    {@link RealtimePose} for.
	 * @return {@link RealtimePose} describing the given {@link Orientation} when
	 *         interpreted in the context of this {@link RealtimePose}.
	 * @throws TransformationException if the {@link RealtimePose} cannot be
	 *                                 computed
	 */
	public RealtimePose getLocalOrientationAsPose(Orientation orientation) throws TransformationException {
		if (orientation == null) {
			return this;
		} else if (orientation.getReferenceFrame() == null) {
			return new RealtimePose(getOrientation(), getReference(), transformation.multiply(RealtimeTransformation
					.createFromVectorRotation(RealtimeVector.ZERO, orientation.getRotation().asRealtimeValue())));
		} else {
			return new RealtimePose(orientation.getReferenceFrame(), RealtimeTransformation
					.createFromVectorRotation(RealtimeVector.ZERO, orientation.getRotation().asRealtimeValue()));
		}
	}

	/**
	 * Calculates a {@link RealtimePose} for an {@link RealtimeOrientation}. A local
	 * {@link RealtimeOrientation} (with reference <code>null</code>) is interpreted
	 * as an {@link RealtimeOrientation} that is defined based on this
	 * {@link RealtimePose}'s point and orientation.
	 *
	 * @param orientation (local) {@link RealtimeOrientation} to calculate a
	 *                    {@link RealtimePose} for.
	 * @return {@link RealtimePose} describing the given {@link RealtimeOrientation}
	 *         when interpreted in the context of this {@link RealtimePose}.
	 * @throws TransformationException if the {@link RealtimePose} cannot be
	 *                                 computed
	 */
	public RealtimePose getLocalOrientationAsPose(RealtimeOrientation orientation) throws TransformationException {
		if (orientation == null) {
			return this;
		} else if (orientation.getReference() == null) {
			return new RealtimePose(getOrientation(), getReference(), getTransformation().multiply(
					RealtimeTransformation.createFromVectorRotation(RealtimeVector.ZERO, orientation.getRotation())));
		} else {
			return new RealtimePose(orientation.getReference(),
					RealtimeTransformation.createFromVectorRotation(RealtimeVector.ZERO, orientation.getRotation()));
		}
	}

	/**
	 * Calculates a {@link RealtimePose} for a {@link Point}. A local {@link Point}
	 * (with reference <code>null</code>) is interpreted as an {@link Point} that is
	 * defined based on this {@link RealtimePose}'s point and orientation.
	 *
	 * @param point (local) {@link Point} to calculate a {@link RealtimePose} for.
	 * @return {@link RealtimePose} describing the given {@link Point} when
	 *         interpreted in the context of this {@link RealtimePose}.
	 * @throws TransformationException if the {@link RealtimePose} cannot be
	 *                                 computed
	 */
	public RealtimePose getLocalPointAsPose(Point point) throws TransformationException {
		if (point == null) {
			return this;
		} else if (point.getReferenceFrame() == null) {
			return plus(getLocalOrientationAsPose(point.getOrientation()).asRealtimeOrientation(),
					RealtimeTransformation.createFromVectorRotation(point.getVector().asRealtimeValue(),
							RealtimeRotation.IDENTITY));
		} else {
			return point.asPose().asRealtimeValue();
		}
	}

	/**
	 * Calculates a {@link RealtimePose} for a {@link RealtimePoint}. A local
	 * {@link RealtimePoint} (with reference <code>null</code>) is interpreted as an
	 * {@link RealtimePoint} that is defined based on this {@link RealtimePose}'s
	 * point and orientation.
	 *
	 * @param point (local) {@link RealtimePoint} to calculate a
	 *              {@link RealtimePose} for.
	 * @return {@link RealtimePose} describing the given {@link RealtimePoint} when
	 *         interpreted in the context of this {@link RealtimePose}.
	 * @throws TransformationException if the {@link RealtimePose} cannot be
	 *                                 computed
	 */
	public RealtimePose getLocalPointAsPose(RealtimePoint point) throws TransformationException {
		if (point == null) {
			return this;
		} else if (point.getReference() == null) {
			return reference.asRealtimePose()
					.plus(getTransformationForRepresentation(reference.asRealtimeOrientation()))
					.plus(point.getRealtimeVector());
		} else {
			return point.getReference().asRealtimePose().plus(point.getRealtimeVector());
		}
	}
	/* former frame stuff */

	/**
	 * Creates a new RealtimePose that is displaced by the given values relative to
	 * this RealtimePose.
	 *
	 * @param x translation along x-axis of this RealtimePose
	 * @param y translation along y-axis of this RealtimePose
	 * @param z translation along z-axis of this RealtimePose
	 * @param a rotation around z-axis of this RealtimePose
	 * @param b rotation around y-axis of this RealtimePose
	 * @param c rotation around x-axis of this RealtimePose
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 */
	public final RealtimePose plus(double x, double y, double z, double a, double b, double c)
			throws TransformationException {
		return plus(new Vector(x, y, z), new Rotation(a, b, c));
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given values relative to
	 * this RealtimePose.
	 *
	 * @param x translation along x-axis of this RealtimePose
	 * @param y translation along y-axis of this RealtimePose
	 * @param z translation along z-axis of this RealtimePose
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 *
	 */
	public final RealtimePose plus(double x, double y, double z) throws TransformationException {
		return plus(x, y, z, 0, 0, 0);
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given {@link Vector}
	 * relative to this RealtimePose.
	 *
	 * @param translation the translation vector
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 */
	public final RealtimePose plus(Vector translation) throws TransformationException {
		return plus(translation, Rotation.getIdentity());
	}

	public final RealtimePose plus(RealtimeVector translation) throws TransformationException {
		return plus(translation, Rotation.getIdentity().asRealtimeValue());
	}

	/**
	 * Creates a new RealtimePose that is rotated by the given {@link Rotation}
	 * relative to this RealtimePose.
	 *
	 * @param rotation the Rotation
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 */
	public final RealtimePose plus(Rotation rotation) throws TransformationException {
		return plus(Vector.getNullVector(), rotation);
	}

	public final RealtimePose plus(RealtimeRotation rotation) throws TransformationException {
		return plus(Vector.getNullVector().asRealtimeValue(), rotation);
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given Vector and the
	 * given {@link Rotation} relative to this RealtimePose.
	 *
	 * @param translation the translation vector
	 * @param rotation    the rotation
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 */
	public final RealtimePose plus(Vector translation, Rotation rotation) throws TransformationException {
		return plus(new Transformation(rotation, translation));
	}

	public final RealtimePose plus(RealtimeVector translation, RealtimeRotation rotation)
			throws TransformationException {
		return plus(RealtimeTransformation.createFromVectorRotation(translation, rotation));
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given
	 * {@link Transformation} relative to this RealtimePose.
	 *
	 * @param transformation the Transformation between this RealtimePose and the
	 *                       new RealtimePose
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException
	 */
	public RealtimePose plus(Transformation transformation) throws TransformationException {
		return plus(asRealtimeOrientation(), transformation.asRealtimeValue());
	}

	public RealtimePose plus(RealtimeTransformation transformation) throws TransformationException {
		return plus(asRealtimeOrientation(), transformation);
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given {@link Vector}
	 * relative to this RealtimePose. The displacement is interpreted with respect
	 * to the given {@link Orientation}.
	 *
	 * Example usage:
	 * lwr.getFlange().asRealtimePose().plus(lwr.getBase().getOrientation(), new
	 * Vector(1, 0, 0));
	 *
	 * This creates a new RealtimePose, attached to lwr's flange, which is displaced
	 * by 1m in x direction of lwr's base. The orientation is taken dynamically, so
	 * after moving the flange the Pose will still point in the base X direction.
	 *
	 * @param translation the translational displacement
	 * @param reference   the reference orientation for applying the displacement
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException thrown if the effective displacement could
	 *                                 not be calculated
	 */
	public RealtimePose plus(Orientation orientation, Vector translation) throws TransformationException {
		return plus(orientation, new Transformation(Rotation.getIdentity(), translation));
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given
	 * {@link Transformation} relative to this RealtimePose. The Transformation is
	 * interpreted with respect to the given {@link Orientation}.
	 *
	 * @param transformation the displacement
	 * @param reference      the reference orientation for applying the displacement
	 * @return the newly constructed RealtimePose
	 * @throws TransformationException thrown if the effective displacement could
	 *                                 not be calculated
	 */

	public RealtimePose plus(Orientation orientation, Transformation transformation) throws TransformationException {
		return plus(orientation, transformation.asRealtimeValue());
	}

	public RealtimePose plus(Orientation orientation, RealtimeTransformation transformation)
			throws TransformationException {
		return plus(orientation.asRealtimeValue(), transformation);
	}

	public RealtimePose plus(RealtimeOrientation orientation, Transformation transformation)
			throws TransformationException {
		return plus(orientation, transformation.asRealtimeValue());
	}

	public RealtimePose plus(RealtimeOrientation orientation, RealtimeTransformation transformation)
			throws TransformationException {
		RealtimePose local = getLocalOrientationAsPose(asRealtimeOrientation(World.getCommandedTopology()));
		RealtimePose requested = getLocalOrientationAsPose(orientation);
		RealtimeTransformation orientationChange = local.getCommandedRealtimeTransformationTo(requested);
		RealtimeTransformation localTrans = transformation.changeOrientation(orientationChange.getRotation());

		return new RealtimePose(this.orientation, reference, this.transformation.multiply(localTrans));
	}

	/**
	 * Creates a new RealtimePose that is displaced by the given translation values
	 * relative to this Pose. The displacement is interpreted with respect to the
	 * given {@link Orientation}.
	 *
	 * Example usage: lwr.getFlange().plus( lwr.getBase().getOrientation(), 1, 0,
	 * 0);
	 *
	 * This creates a new Frame, attached to lwr's flange, which is displaced by 1m
	 * in x direction of lwr's base. Note that the Orientation is taken dynamically,
	 * and will still be in effect when the flange moves.
	 *
	 * @param x         translation in x direction
	 * @param y         translation in y direction
	 * @param z         translation in z direction
	 * @param reference the reference orientation for applying the displacement
	 * @return the newly constructed RealtimePose
	 * @throws RoboticsException thrown if the effective displacement could not be
	 *                           calculated
	 */
	public final RealtimePose plus(Orientation reference, double x, double y, double z) throws RoboticsException {
		return plus(reference, new Vector(x, y, z));
	}

	/**
	 * Returns a new RealtimePose that "snapshots" (i.e., holds constant) the
	 * current (commanded) geometric state of this RealtimePose with respect to a
	 * given reference Frame at the current point in time.
	 *
	 * Use this method to conserve the state of RealtimePoses that are changing over
	 * time (like the robot flange frame).
	 *
	 * 'Commanded' means that, in case of dynamic relations like a robot kinematics,
	 * target values are considered instead of actual, measured values. This method
	 * uses World.getCommandedTopology() to determine the current transformation.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this RealtimePose with respect to the
	 *         given reference frame.
	 * @throws RoboticsException
	 */
	public RealtimePose snapshot(final Frame referenceFrame) throws RoboticsException {
		return new RealtimePose(referenceFrame, referenceFrame.asRealtimePose()
				.getRealtimeTransformationTo(this, World.getCommandedTopology()).getCurrentValue().asRealtimeValue());
	}

	/**
	 * Returns a new RealtimePose that "snapshots" (i.e., holds constant) the
	 * current (measured) geometric state of this Pose with respect to a given
	 * reference Frame at the current point in time.
	 *
	 * Use this method to conserve the state of RealtimePoses that are changing over
	 * time (like the robot flange frame).
	 *
	 * In case of dynamic relations like a robot kinematics, actual measured values
	 * are considered.
	 *
	 * @param referenceFrame the reference frame used for the snapshot.
	 * @return new "snapshotted" version of this RealtimePose with respect to the
	 *         given reference frame.
	 * @throws RoboticsException
	 */
	public RealtimePose snapshotMeasured(final Frame referenceFrame) throws RoboticsException {
		return new RealtimePose(referenceFrame, referenceFrame.asRealtimePose()
				.getRealtimeTransformationTo(this, World.getMeasuredTopology()).getCurrentValue().asRealtimeValue());
	}

	/**
	 * Retrieves the commanded transformation to the given RealtimePose. Same as
	 * getCommandedRealtimeTransformationTo(RealtimePose to).
	 *
	 * @param to target pose
	 * @return transformation from this RealtimePose to the given RealtimePose
	 * @throws TransformationException
	 */
	@Deprecated
	public RealtimeTransformation getRealtimeTransformationTo(RealtimePose to) throws TransformationException {
		return getCommandedRealtimeTransformationTo(to);
	}

	/**
	 * Retrieves the commanded transformation to the given RealtimePose.
	 *
	 * Uses World.getCommandedTopology() to retrieve the transformation.
	 *
	 * @param to target pose
	 * @return transformation from this RealtimePose to the given RealtimePose
	 * @throws TransformationException
	 */
	public RealtimeTransformation getCommandedRealtimeTransformationTo(RealtimePose to) throws TransformationException {
		return getRealtimeTransformationTo(to, World.getCommandedTopology());
	}

	/**
	 * Retrieves the measured transformation to the given RealtimePose.
	 *
	 * Uses World.getMeasuredTopology() to retrieve the transformation.
	 *
	 * @param to target pose
	 * @return transformation from this RealtimePose to the given RealtimePose
	 * @throws TransformationException
	 */
	public RealtimeTransformation getMeasuredRealtimeTransformationTo(RealtimePose to) throws TransformationException {
		return getRealtimeTransformationTo(to, World.getMeasuredTopology());
	}

	/**
	 * Retrieves the transformation to the given RealtimePose in the given
	 * FrameTopology
	 *
	 * @param to       target pose
	 * @param topology frame topology to use
	 * @return transformation from this RealtimePose to the given RealtimePose
	 * @throws TransformationException
	 */
	public RealtimeTransformation getRealtimeTransformationTo(RealtimePose to, FrameTopology topology)
			throws TransformationException {
		return getTransformationForRepresentation(reference.asRealtimeOrientation(), topology).invert()
				.multiply(getReference().getRealtimeTransformationTo(to.getReference(), topology))
				.multiply(to.getTransformationForRepresentation(to.getReference().asRealtimeOrientation(), topology));
	}

	/**
	 * Retrieves the {@link RealtimeVelocity} of this {@link RealtimePose} (relative
	 * to its reference {@link Frame}).
	 *
	 * @return {@link RealtimeVelocity} of this {@link Pose} relative to its
	 *         reference
	 */
	public RealtimeVelocity getRealtimeVelocity() {
		return RealtimeVelocity.createFromTwist(reference, null, orientation, transformation.derive());
	}

	/**
	 * Computes the {@link RealtimeTwist} of another {@link RealtimePose} relative
	 * to this {@link RealtimePose}
	 *
	 * @param other    {@link RealtimePose} to calculate velocity of
	 * @param topology {@link FrameTopology} to use for the computation
	 * @return {@link RealtimeTwist} of the other {@link RealtimePose} relative to
	 *         this {@link RealtimePose} (using this {@link RealtimePose} as
	 *         reference and orientation, but the other RealtimePose as pivot point)
	 * @throws TransformationException if the {@link RealtimeTwist} cannot be
	 *                                 calculated
	 */
	public RealtimeTwist getRealtimeTwistOf(RealtimePose other, FrameTopology topology) throws TransformationException {
		RealtimeTwist t1 = other.getRealtimeVelocity().getTwistForRepresentation(reference, reference.asRealtimePoint(),
				reference.asRealtimeOrientation(), other, topology);
		RealtimeTwist t2 = getRealtimeVelocity().getTwistForRepresentation(reference, reference.asRealtimePoint(),
				reference.asRealtimeOrientation(), this, topology);
		RealtimeVelocity vel = RealtimeVelocity.createFromTwist(reference, reference.asPoint(),
				reference.asOrientation(), t2.invert().add(t1));
		return vel.getTwistForRepresentation(asRealtimeOrientation(topology), null, other, topology);
	}

	/**
	 * Computes the {@link RealtimeTwist} of another {@link RealtimePose} relative
	 * to this {@link RealtimePose} using the 'commanded' topology.
	 *
	 * Uses World.getCommandedTopology() to calculate the twist.
	 *
	 * @param other    {@link RealtimePose} to calculate velocity of
	 * @param topology {@link FrameTopology} to use for the computation
	 * @return {@link RealtimeTwist} of the other {@link RealtimePose} relative to
	 *         this {@link RealtimePose} (using this {@link RealtimePose} as
	 *         reference and orientation, but the other RealtimePose as pivot point)
	 * @throws TransformationException if the {@link RealtimeTwist} cannot be
	 *                                 calculated
	 */
	public RealtimeTwist getCommandedRealtimeTwistOf(RealtimePose other) throws TransformationException {
		return getRealtimeTwistOf(other, World.getCommandedTopology());
	}

	/**
	 * Computes the {@link RealtimeTwist} of another {@link RealtimePose} relative
	 * to this {@link RealtimePose} using the 'measured' topology.
	 *
	 * Uses World.getMeasuredTopology() to calculate the twist.
	 *
	 * @param other    {@link RealtimePose} to calculate velocity of
	 * @param topology {@link FrameTopology} to use for the computation
	 * @return {@link RealtimeTwist} of the other {@link RealtimePose} relative to
	 *         this {@link RealtimePose} (using this {@link RealtimePose} as
	 *         reference and orientation, but the other RealtimePose as pivot point)
	 * @throws TransformationException if the {@link RealtimeTwist} cannot be
	 *                                 calculated
	 */
	public RealtimeTwist getMeasuredRealtimeTwistOf(RealtimePose other) throws TransformationException {
		return getRealtimeTwistOf(other, World.getMeasuredTopology());
	}

	/**
	 * Calculates the {@link RealtimeTransformation} required to reach this
	 * {@link RealtimePose} when starting at the given reference {@link Frame} and
	 * using the given {@link Orientation}.
	 *
	 * @param newOrientation new {@link Orientation} to calculate the
	 *                       {@link RealtimeTransformation} for
	 * @param newReference   new {@link Frame} to use as a reference for this
	 *                       {@link RealtimeTransformation}
	 * @param topology       {@link FrameTopology} to use for conversion
	 * @return new {@link RealtimeTransformation} required to reach this
	 *         {@link RealtimePose} when starting at the given reference
	 *         {@link Frame} and using the given {@link Orientation}.
	 * @throws TransformationException if the {@link Transformation} cannot be
	 *                                 calculated
	 */

	public RealtimeTransformation getTransformationForRepresentation(Orientation newOrientation, Frame newReference,
			FrameTopology topology) throws TransformationException {
		if (getRuntime() != null) {
			topology = topology.forRuntime(getRuntime());
		}

		RealtimeTransformation trans = transformation;

		if (!reference.asOrientation().equals(orientation)) {
			RealtimeTransformation orientationChange = getLocalOrientationAsPose(orientation.asRealtimeValue())
					.getRealtimeTransformationTo(reference.asRealtimePose(), topology);
			trans = trans.changeOrientation(orientationChange.getRotation());
		}

		if (newReference != reference) {
			trans = newReference.getRealtimeTransformationTo(reference, topology).multiply(trans);
		}

		if (!newOrientation.equals(newReference.asOrientation())) {
			RealtimeTransformation orientationChange = getLocalOrientationAsPose(
					newReference.asOrientation().asRealtimeValue())
							.getRealtimeTransformationTo(getLocalOrientationAsPose(newOrientation), topology);
			trans = transformation.changeOrientation(orientationChange.getRotation());
		}

		return trans;
	}

	/**
	 * Creates a new {@link RealtimePose} that denotes the same position and
	 * orientation in space, however with a representation that uses the given meta
	 * data. Note: This method does not change the orientation of the place
	 * described by the Pose, but only the internal representation.
	 *
	 * @param newOrientation new {@link Orientation} to describe the
	 *                       {@link RealtimePose} in
	 * @param newReference   new {@link Frame} to use as a reference for this
	 *                       {@link RealtimePose}
	 * @param topology       {@link FrameTopology} to use for conversion
	 * @return new {@link RealtimePose} describing the same position and
	 *         orientation, but using the new {@link Orientation} and {@link Frame}
	 *         to represent the {@link Transformation}
	 * @throws TransformationException if the {@link Transformation} cannot be
	 *                                 calculated
	 */
	public RealtimePose convertToRepresentation(Orientation newOrientation, Frame newReference, FrameTopology topology)
			throws TransformationException {
		return new RealtimePose(newOrientation, newReference,
				getTransformationForRepresentation(newOrientation, newReference, topology));
	}

	/**
	 * Creates a new {@link RealtimePose} that denotes another position and
	 * orientation in space which is reached when applying the same
	 * {@link Transformation} to another reference {@link Frame} and
	 * {@link Orientation}.
	 *
	 * @param newOrientation new {@link Orientation} to describe the
	 *                       {@link RealtimePose} in
	 * @param newReference   new {@link Frame} to use as a reference for this
	 *                       {@link RealtimePose}
	 * @param topology       {@link FrameTopology} to use for conversion
	 * @return new {@link RealtimePose} describing another position and orientation,
	 *         by using the same {@link Transformation} with another
	 *         {@link Orientation} and reference {@link Frame}.
	 */
	public RealtimePose reinterpretToRepresentation(Orientation newOrientation, Frame newReference,
			FrameTopology topology) {
		return new RealtimePose(newOrientation, newReference, transformation);
	}

	@Override
	public String toString() {
		return "RealtimePose(R: " + getReference()
				+ (!reference.asOrientation().isEqualOrientation(orientation) ? ", O: " + orientation : "") + ", "
				+ getTransformation() + ")";
	}

	public static RealtimePose createFromTransformation(Frame frame, RealtimeTransformation transformation) {
		return new RealtimePose(frame, transformation);
	}

	public static RealtimePose createFromTransformation(Orientation orientation, Frame reference,
			RealtimeTransformation transformation) {
		return new RealtimePose(orientation, reference, transformation);
	}

}
