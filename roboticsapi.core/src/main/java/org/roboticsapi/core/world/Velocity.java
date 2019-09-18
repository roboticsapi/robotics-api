/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

public class Velocity {

	private final Frame reference;
	private final Point pivotPoint;
	private final Orientation orientation;
	private final Twist twist;

	/**
	 * Instantiates a new referenced twist.
	 *
	 * @param referenceFrame the Reference Frame
	 * @param pivotPoint     the Pivot Point, or null if the moving frame gives the
	 *                       pivot point
	 * @param orientation    the Orientation, or null if the moving frame gives the
	 *                       orientation
	 * @param twist          the twist of the velocity
	 */
	public Velocity(Frame referenceFrame, Point pivotPoint, Orientation orientation, Twist twist) {
		this.reference = referenceFrame;
		this.pivotPoint = pivotPoint == null ? new Point(null, Vector.getNullVector()) : pivotPoint;
		this.orientation = orientation == null ? new Orientation(null, Rotation.getIdentity()) : orientation;
		this.twist = twist;
	}

	/**
	 * Instantiates a new referenced twist, using the reference frame for
	 * orientation and the moving frame as pivot point
	 *
	 * @param referenceFrame the Reference Frame
	 * @param twist          The twist of the velocity
	 */
	public Velocity(Frame referenceFrame, Twist twist) {
		this(referenceFrame, null, referenceFrame.asOrientation(), twist);
	}

	public Frame getReferenceFrame() {
		return reference;
	}

	public Point getPivotPoint() {
		return pivotPoint;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Twist getTwist() {
		return twist;
	}

	public Twist getTwistForRepresentation(Orientation newOrientation, Point newPivot) throws TransformationException {
		Twist ret = getTwist();
		if (newPivot == null) {
			newPivot = new Point(null, Vector.ZERO);
		}
		if (!getPivotPoint().isEqualPoint(newPivot)) {
			if (newPivot.getReferenceFrame() == null && pivotPoint.getReferenceFrame() == null
					&& orientation.getReferenceFrame() == null) {
				Vector oldToNew = newPivot.getVector().add(pivotPoint.getVector().invert());
				ret = ret.changePivot(orientation.getRotation().invert().apply(oldToNew));
			} else if (newPivot.getReferenceFrame() == null || pivotPoint.getReferenceFrame() == null
					|| orientation.getReferenceFrame() == null) {
				throw new TransformationException("Cannot convert from local to global point");
			} else {
				Vector oldToNew = orientation.asPose()
						.getTransformationTo(newPivot.asPose(), World.getCommandedTopology().withoutDynamic())
						.getTranslation()
						.add(orientation.asPose()
								.getTransformationTo(pivotPoint.asPose(), World.getCommandedTopology().withoutDynamic())
								.getTranslation().invert());
				ret = ret.changePivot(oldToNew);
			}
		}
		if (!getOrientation().isEqualOrientation(newOrientation)) {
			if (orientation.getReferenceFrame() == null && newOrientation.getReferenceFrame() == null) {
				ret = ret.changeOrientation(newOrientation.getRotation().invert().multiply(orientation.getRotation()));
			} else if (orientation.getReferenceFrame() == null || newOrientation.getReferenceFrame() == null) {
				throw new TransformationException("Cannot convert from local to global orientation");
			} else {
				ret = ret.changeOrientation(newOrientation.asPose()
						.getTransformationTo(orientation.asPose(), World.getCommandedTopology().withoutDynamic())
						.getRotation());
			}
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Velocity)) {
			return false;
		}
		Velocity vel = (Velocity) obj;
		if (!((orientation == null && vel.orientation == null) || (orientation.equals(vel.orientation)))) {
			return false;
		}
		if (!((pivotPoint == null && vel.pivotPoint == null) || (pivotPoint.equals(vel.pivotPoint)))) {
			return false;
		}
		if (!((twist == null && vel.twist == null) || (twist.equals(vel.twist)))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(
				HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, orientation), pivotPoint), reference), twist);
	}

	@Override
	public String toString() {
		return "Velocity(O:" + orientation + ", P:" + pivotPoint + ", R:" + reference + ", " + twist + ")";
	}

	public RealtimeVelocity asRealtimeValue() {
		return RealtimeVelocity.createFromTwist(reference, pivotPoint, orientation,
				RealtimeTwist.createFromConstant(twist));
	}

	public Twist getTwistForRepresentation(Orientation newOrientation, Point newPivot, Pose pose,
			FrameTopology topology) throws TransformationException {
		Twist ret = getTwist();
		if (newPivot == null) {
			newPivot = new Point(null, Vector.getNullVector());
		}
		if (!getPivotPoint().isEqualPoint(newPivot)) {
			if (newPivot.getReferenceFrame() == null && pivotPoint.getReferenceFrame() == null
					&& orientation.getReferenceFrame() == null) {
				Vector oldToNew = newPivot.getVector().add(pivotPoint.getVector().invert());
				ret = ret.changePivot(orientation.getRotation().invert().apply(oldToNew));
			} else if (pose == null && (newPivot.getReferenceFrame() == null || pivotPoint.getReferenceFrame() == null
					|| orientation.getReferenceFrame() == null)) {
				throw new TransformationException("Cannot convert from local to global point");
			} else {
				Pose ori = pose != null ? pose.getLocalOrientationAsPose(orientation) : orientation.asPose();
				Pose newPP = pose != null ? pose.getLocalPointAsPose(newPivot) : newPivot.asPose();
				Pose oldPP = pose != null ? pose.getLocalPointAsPose(pivotPoint) : pivotPoint.asPose();
				Vector oldToNew = ori.getTransformationTo(newPP, topology).getTranslation()
						.add(ori.getTransformationTo(oldPP, topology).getTranslation().invert());
				ret = ret.changePivot(oldToNew);
			}
		}

		if (!getOrientation().isEqualOrientation(newOrientation)) {
			if (orientation.getReferenceFrame() == null && newOrientation.getReferenceFrame() == null) {
				ret = ret.changeOrientation(newOrientation.getRotation().invert().multiply(orientation.getRotation()));
			} else if (pose == null
					&& (orientation.getReferenceFrame() == null || newOrientation.getReferenceFrame() == null)) {
				throw new TransformationException("Cannot convert from local to global orientation");
			} else {
				Pose newOriP = pose != null ? pose.getLocalOrientationAsPose(newOrientation) : newOrientation.asPose();
				Pose oldOriP = pose != null ? pose.getLocalOrientationAsPose(orientation) : orientation.asPose();
				ret = ret.changeOrientation(newOriP.getTransformationTo(oldOriP, topology).getRotation());
			}
		}
		return ret;
	}

	public Velocity convertToRepresentation(Point newPivot, Orientation newOrientation, Pose currentPose,
			FrameTopology topology) throws TransformationException {
		return new Velocity(reference, newPivot, newOrientation,
				getTwistForRepresentation(newOrientation, newPivot, currentPose, topology));
	}

	public Velocity reinterpretToRepresentation(Frame newReference, Point newPivot, Orientation newOrientation) {
		return new Velocity(newReference, newPivot, newOrientation, twist);
	}

}
