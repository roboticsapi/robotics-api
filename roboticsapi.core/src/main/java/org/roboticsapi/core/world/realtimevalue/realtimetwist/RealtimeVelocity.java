/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import java.util.Map;

import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Direction;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Orientation;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.realtimevalue.RealtimeDirection;
import org.roboticsapi.core.world.realtimevalue.RealtimeOrientation;
import org.roboticsapi.core.world.realtimevalue.RealtimePoint;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * {@link RealtimeValue} measuring the relative velocity (as {@link Velocity}) of a
 * {@link Frame}.
 *
 * A Twist value has to be interpreted w.r.t. the following parameters, which
 * are also part of the VelocitySensor's definition:
 *
 * <ul>
 * <li><b>Reference Frame</b>: the Frame relative to which the Moving Frame
 * moves (this Frame may itself be moving),</li>
 * <li><b>Pivot Point</b>: the point in space that is interpreted as a
 * rotational center, or null if the moving frame is used,</li>
 * <li><b>Orientation</b>: the orientation that determines the axes for
 * translational and rotational velocity, or null if the moving frame is
 * used.</li>
 * </ul>
 */
public final class RealtimeVelocity extends RealtimeValue<Velocity> {

	@Override
	public final RealtimeVelocity substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeVelocity) substitutionMap.get(this);
		}
		return new RealtimeVelocity(reference, pivotPoint, orientation, twist.substitute(substitutionMap));
	}

	private final Frame reference;
	private final Point pivotPoint;
	private final Orientation orientation;
	private final RealtimeTwist twist;

	/**
	 * Instantiates a new velocity sensor.
	 *
	 * @param referenceFrame the Reference Frame
	 * @param pivotPoint     the Pivot Point, or null if the moving frame gives the
	 *                       pivot point
	 * @param orientation    the Orientation, or null if the moving frame gives the
	 *                       orientation
	 * @param twist          the twist of the velocity
	 */
	RealtimeVelocity(Frame referenceFrame, Point pivotPoint, Orientation orientation, RealtimeTwist twist) {
		super(twist);
		this.reference = referenceFrame;
		this.pivotPoint = pivotPoint == null ? new Point(null, Vector.getNullVector()) : pivotPoint;
		this.orientation = orientation == null ? new Orientation(null, Rotation.getIdentity()) : orientation;
		this.twist = twist;
	}

	/**
	 * Gets the Reference Frame this VelocitySensor works with.
	 *
	 * @return the reference frame
	 */
	public Frame getReferenceFrame() {
		return reference;
	}

	public RealtimeTwist getTwist() {
		return twist;
	}

	public RealtimeTwist getTwistForRepresentation(RealtimeOrientation newOrientation, RealtimePoint newPivot,
			RealtimePose pose, FrameTopology topology) throws TransformationException {
		RealtimeTwist ret = getTwist();
		if (getRuntime() != null) {
			topology = topology.forRuntime(getRuntime());
		}

		if (newPivot == null) {
			newPivot = RealtimePoint.createFromVector(null, RealtimeVector.ZERO);
		}
		if (!getPivotPoint().equals(newPivot)) {
			if (newPivot.getReference() == null && pivotPoint.getReferenceFrame() == null
					&& orientation.getReferenceFrame() == null) {
				RealtimeVector oldToNew = newPivot.getRealtimeVector()
						.add(pivotPoint.getVector().asRealtimeValue().invert());
				ret = ret.changePivotPoint(oldToNew.transform(orientation.getRotation().asRealtimeValue().invert()));
			} else if (pose == null && (newPivot.getReference() == null || pivotPoint.getReferenceFrame() == null
					|| orientation.getReferenceFrame() == null)) {
				throw new TransformationException("Cannot convert from local to global point");
			} else {
				RealtimePose ori = pose != null ? pose.getLocalOrientationAsPose(orientation)
						: orientation.asPose().asRealtimeValue();
				RealtimePose newPP = pose != null ? pose.getLocalPointAsPose(newPivot) : newPivot.asRealtimePose();
				RealtimePose oldPP = pose != null ? pose.getLocalPointAsPose(pivotPoint)
						: pivotPoint.asPose().asRealtimeValue();
				RealtimeVector oldToNew = ori.getRealtimeTransformationTo(newPP, topology).getTranslation()
						.add(ori.getRealtimeTransformationTo(oldPP, topology).getTranslation().invert());
				ret = ret.changePivotPoint(oldToNew);
			}
		}

		if (newOrientation == null) {
			newOrientation = RealtimeOrientation.createFromConstant(null, Rotation.IDENTITY);
		}
		if (!getOrientation().asRealtimeValue().equals(newOrientation)) {
			if (orientation.getReferenceFrame() == null && newOrientation.getReference() == null) {
				ret = ret.changeOrientation(newOrientation.getRotation().invert().multiply(orientation.getRotation()));
			} else if (pose == null
					&& (orientation.getReferenceFrame() == null || newOrientation.getReference() == null)) {
				throw new TransformationException("Cannot convert from local to global orientation");
			} else {
				RealtimePose newOriP = pose != null ? pose.getLocalOrientationAsPose(newOrientation)
						: newOrientation.asRealtimePose();
				RealtimePose oldOriP = pose != null ? pose.getLocalOrientationAsPose(orientation)
						: orientation.asPose().asRealtimeValue();
				RealtimeRotation rot = newOriP.getRealtimeTransformationTo(oldOriP, topology).getRotation();
				ret = ret.changeOrientation(rot);
			}
		}
		return ret;
	}

	public RealtimeDirection getTranslationVelocity() {
		return new RealtimeDirection(getTwist().getTranslationVelocity(), orientation);
	}

	public RealtimeDirection getRotationVelocity() {
		return new RealtimeDirection(getTwist().getRotationVelocity(), orientation);
	}

	/**
	 * Gets the Pivot Point of this {@link RealtimeVelocity}.
	 *
	 * @return the Pivot Point
	 */
	public Point getPivotPoint() {
		return pivotPoint;
	}

	/**
	 * Gets the Orientation of this {@link RealtimeVelocity}.
	 *
	 * @return the Orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && pivotPoint.equals(((RealtimeVelocity) other).pivotPoint)
				&& reference.equals(((RealtimeVelocity) other).reference)
				&& orientation.equals(((RealtimeVelocity) other).orientation);
	}

	@Override
	public int hashCode() {
		return classHash(pivotPoint, reference, orientation);
	}

	/**
	 * A velocity sensor for a constant velocity
	 *
	 * @param referenceFrame reference frame of velocity
	 * @param pivotPoint     pivot point of velocity
	 * @param orientation    orientation of velocity
	 * @param constantValue  constant velocity value
	 * @return a constant velocity sensor
	 */
	public static RealtimeVelocity createFromConstant(Twist constantValue, Frame referenceFrame, Point pivotPoint,
			Orientation orientation) {
		return new RealtimeVelocity(referenceFrame, pivotPoint, orientation,
				RealtimeTwist.createFromConstant(constantValue));
	}

	/**
	 * Combines a velocity sensor from translational/rotational motion directions
	 *
	 * @param translationVelocity tranlational motion direction (speed)
	 * @param rotationVelocity    rotational motion description (direction = axis,
	 *                            length = speed)
	 * @param referenceFrame      reference frame of the velocity
	 * @param pivotPoint          pivot point of the velocity
	 * @return the combined velocity sensor
	 */
	public static RealtimeVelocity createFromLinearAngular(RealtimeDirection translationVelocity,
			RealtimeDirection rotationVelocity, Frame referenceFrame, Point pivotPoint) {

		if (!translationVelocity.getOrientation().isEqualOrientation(rotationVelocity.getOrientation())) {
			throw new IllegalArgumentException("Orientations of both RealtimeDirections must be the same");
		}

		return new RealtimeVelocity(referenceFrame, pivotPoint, translationVelocity.getOrientation(),
				new VectorToRealtimeTwist(translationVelocity.getVector(), rotationVelocity.getVector()));
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(twist);
	}

	@Override
	protected Velocity calculateCheapValue() {
		return wrap(twist.getCheapValue());
	}

	private Velocity wrap(Twist twist) {
		if (twist == null) {
			return null;
		} else {
			return new Velocity(reference, pivotPoint, orientation, twist);
		}
	}

	@Override
	public Observer<?> createObserver(final RealtimeValueListener<Velocity> listener, RealtimeBoolean condition,
			boolean async) {
		return twist.createObserver(new RealtimeValueListener<Twist>() {
			@Override
			public void onValueChanged(Twist newValue) {
				listener.onValueChanged(wrap(newValue));
			}
		}, condition, async);
	}

	@Override
	public String toString() {
		return "RealtimeReferencedTwist(O:" + orientation + ", P:" + pivotPoint + ", R:" + reference + ", " + twist
				+ ")";
	}

	@Override
	public RealtimeBoolean isNull() {
		return twist.isNull();
	}

	@Override
	public boolean isConstant() {
		return twist.isConstant();
	}

	@Override
	public RealtimeVelocity fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeVelocity(reference, pivotPoint, orientation, twist.fromHistory(age, maxAge));
	}

	public RealtimeTwist getTwistForRepresentation(Frame newReference, RealtimePoint newPivot,
			RealtimeOrientation newOrientation, RealtimePose currentPose, FrameTopology topology)
			throws TransformationException {
		if (getRuntime() != null) {
			topology = topology.forRuntime(getRuntime());
		}

		RealtimeTwist twist = getTwistForRepresentation(newReference.asRealtimeOrientation(),
				newReference.asRealtimePoint(), currentPose, topology);

		if (reference != newReference) {
			RealtimeTwist refTwist = RealtimeVelocity
					.createFromTwist(newReference, null, newReference.asOrientation(),
							newReference.getRealtimeTwistOf(reference, topology))
					.getTwistForRepresentation(newReference.asRealtimeOrientation(), newReference.asRealtimePoint(),
							reference.asRealtimePose(), topology);
			twist = refTwist.add(twist);
		}

		twist = new RealtimeVelocity(newReference, newReference.asPoint(), newReference.asOrientation(), twist)
				.getTwistForRepresentation(newOrientation, newPivot, currentPose, topology);

		return twist;
	}

	public RealtimeVelocity convertToRepresentation(Frame newReference, Point newPivot, Orientation newOrientation,
			RealtimePose currentPose, FrameTopology topology) throws TransformationException {
		return new RealtimeVelocity(newReference, newPivot, newOrientation, getTwistForRepresentation(newReference,
				newPivot.asRealtimeValue(), newOrientation.asRealtimeValue(), currentPose, topology));
	}

	public RealtimeVelocity reinterpretToRepresentation(Frame newReference, Point newPivot,
			Orientation newOrientation) {
		return new RealtimeVelocity(newReference, newPivot, newOrientation, twist);
	}

	/**
	 * Inverts this {@link RealtimeVelocity}, i.e. creates a new
	 * RealtimeReferencedTwist that measures the velocity of this sensor's Reference
	 * Frame relative to this sensor's Moving Frame w.r.t. the same Pivot Point and
	 * Orientation.
	 *
	 * @param moving Frame assumed to be moving with the given twist (that will
	 *               become the new reference)
	 * @return a new RealtimeReferencedTwist that represents the inverted version of
	 *         this RealtimeReferencedTwist
	 */
	public RealtimeVelocity invert(Frame moving) {
		Point pp = getPivotPoint();
		Orientation ori = getOrientation();
		if (ori.getReferenceFrame() == null) {
			ori = new Orientation(moving, ori.getRotation());
		}
		if (pp.getReferenceFrame() == null) {
			pp = new Point(moving, pp.getOrientation(), pp.getVector());
		}
		if (ori.getReferenceFrame() == getReferenceFrame()) {
			ori = new Orientation(null, ori.getRotation());
		}
		if (pp.getReferenceFrame() == getReferenceFrame()) {
			pp = new Point(null, pp.getOrientation(), pp.getVector());
		}
		return new RealtimeVelocity(moving, pp, ori, twist.invert());
	}

	/**
	 * Adds the given {@link RealtimeVelocity} to this RealtimeReferencedTwist, i.e.
	 * creates a new RealtimeReferencedTwist that measures the velocity of the given
	 * sensor's Moving Frame relative to this sensor's Reference Frame w.r.t. this
	 * sensor's Pivot Point and Orientation.
	 *
	 * @param other the RealtimeReferencedTwist to add to this
	 *              RealtimeReferencedTwist
	 * @return a new RealtimeReferencedTwist measuring the total velocity
	 */
	public RealtimeVelocity add(RealtimeVelocity other) {
		if (!getPivotPoint().isEqualPoint(other.getPivotPoint())) {
			throw new IllegalArgumentException("Pivot point of both RealtimeVelocities must match");
		}

		if (!getOrientation().isEqualOrientation(other.getOrientation())) {
			throw new IllegalArgumentException("Orientation of both RealtimeVelocities must match");
		}

		if (!(getReferenceFrame().equals(other.getReferenceFrame()))) {
			if (other.getReferenceFrame() == getReferenceFrame()) {
				throw new IllegalArgumentException(
						"RealtimeVelocities1's reference frame must not be equal to RealtimeVelocities2's reference frame");
			}
		}

		return new RealtimeVelocity(getReferenceFrame(), getPivotPoint(), getOrientation(),
				twist.add(other.getTwist()));
	}

	public static RealtimeVelocity createFromLinearAngular(RealtimeDouble vX, RealtimeDouble vY, RealtimeDouble vZ,
			RealtimeDouble omegaX, RealtimeDouble omegaY, RealtimeDouble omegaZ, Frame referenceFrame,
			Point pivotPoint) {
		RealtimeVector transVector = RealtimeVector.createFromXYZ(vX, vY, vZ);

		RealtimeDirection transVel = RealtimeDirection.createFromVector(transVector, referenceFrame.asOrientation());

		RealtimeDirection rotVel = RealtimeDirection
				.createFromConstant(new Direction(referenceFrame.asOrientation(), new Vector()));

		return RealtimeVelocity.createFromLinearAngular(transVel, rotVel, referenceFrame, pivotPoint);
	}

	public static RealtimeVelocity createFromTwist(Frame referenceFrame, Point pivotPoint, Orientation orientation,
			RealtimeTwist twist) {
		return new RealtimeVelocity(referenceFrame, pivotPoint, orientation, twist);
	}
}