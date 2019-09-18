/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

public class RealtimePoseCoincidence {

	private final RealtimePose a;
	private final RealtimePose b;

	/**
	 * Class describing that two {@link RealtimePose}s describe the same position in
	 * space
	 *
	 * @param a first RealtimePose for the place in space
	 * @param b second RealtimePose for the place in space
	 */
	public RealtimePoseCoincidence(RealtimePose a, RealtimePose b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Calculates the transformation from the first RealtimePose to the second,
	 * assuming that the RealtimePoseCoincidence is true
	 *
	 * @param from start pose
	 * @param to   goal pose
	 * @return Transformation between start and goal pose, assuming the defined pose
	 *         coincidence
	 * @throws TransformationException
	 */
	@Deprecated
	public RealtimeTransformation getTransformation(RealtimePose from, RealtimePose to) throws TransformationException {
		return getTransformation(from, to, World.getCommandedTopology());
	}

	@Deprecated
	public RealtimeTransformation getTransformation(Frame from, Frame to) throws TransformationException {
		return getTransformation(from.asRealtimePose(), to.asRealtimePose());
	}

	public RealtimeTransformation getTransformation(Frame from, Frame to, FrameTopology topology)
			throws TransformationException {
		return getTransformation(from.asRealtimePose(), to.asRealtimePose(), topology);
	}

	/**
	 * Calculates the transformation from the first RealtimePose to the second,
	 * assuming that the RealtimePoseCoincidence is true
	 *
	 * @param from     start pose
	 * @param to       goal pose
	 * @param topology Frame topology to use
	 * @return Transformation between start and goal pose, assuming the defined pose
	 *         coincidence
	 * @throws TransformationException
	 */
	public RealtimeTransformation getTransformation(RealtimePose from, RealtimePose to, FrameTopology topology)
			throws TransformationException {
		Frame fRef = from.getReference();
		Frame tRef = to.getReference();
		Frame aRef = a.getReference();
		Frame bRef = b.getReference();

		if (fRef.getRelationsTo(aRef, World.getCommandedTopology().without(bRef, tRef)) != null
				&& tRef.getRelationsTo(bRef, World.getCommandedTopology().without(aRef, fRef)) != null) {
			// f -> a -> b -> t
			RealtimeTransformation t1 = from
					.getTransformationForRepresentation(fRef.asOrientation().asRealtimeValue(), topology).invert();
			RealtimeTransformation t2 = fRef.getRealtimeTransformationTo(aRef, topology);
			RealtimeTransformation t3 = a.getTransformationForRepresentation(aRef.asOrientation().asRealtimeValue(),
					topology);
			RealtimeTransformation t4 = b
					.getTransformationForRepresentation(bRef.asOrientation().asRealtimeValue(), topology).invert();
			RealtimeTransformation t5 = bRef.getRealtimeTransformationTo(tRef, topology);
			RealtimeTransformation t6 = to.getTransformationForRepresentation(tRef.asOrientation().asRealtimeValue(),
					topology);
			return t1.multiply(t2).multiply(t3).multiply(t4).multiply(t5).multiply(t6);
		} else if (fRef.getRelationsTo(bRef, World.getCommandedTopology().without(bRef, tRef)) != null
				&& tRef.getRelationsTo(aRef, World.getCommandedTopology().without(bRef, fRef)) != null) {
			// f -> b -> a -> t
			RealtimeTransformation t1 = from
					.getTransformationForRepresentation(fRef.asOrientation().asRealtimeValue(), topology).invert();
			RealtimeTransformation t2 = fRef.getRealtimeTransformationTo(bRef, topology);
			RealtimeTransformation t3 = b.getTransformationForRepresentation(bRef.asOrientation().asRealtimeValue(),
					topology);
			RealtimeTransformation t4 = a
					.getTransformationForRepresentation(aRef.asOrientation().asRealtimeValue(), topology).invert();
			RealtimeTransformation t5 = aRef.getRealtimeTransformationTo(tRef, topology);
			RealtimeTransformation t6 = to.getTransformationForRepresentation(tRef.asOrientation().asRealtimeValue(),
					topology);
			return t1.multiply(t2).multiply(t3).multiply(t4).multiply(t5).multiply(t6);
		} else {
			throw new TransformationException("Transformation between " + from + " and " + to
					+ " is not covered by the defined RealtimePoseCoincidence.");
		}
	}

	/**
	 * Calculates the RealtimePose relative to the given reference of the given
	 * Pose, assuming that the RealtimePoseCoincidence is true
	 *
	 * @param reference reference frame for the new RealtimePose
	 * @param pose      pose to calculate another representation for
	 * @param topology  topology to use
	 * @return RealtimePose of the given pose relative to the given reference
	 * @throws TransformationException
	 */
	public RealtimePose getRealtimePose(Frame reference, RealtimePose pose, FrameTopology topology)
			throws TransformationException {
		return new RealtimePose(reference, getTransformation(reference.asRealtimePose(), pose, topology));
	}

	/**
	 * Calculates the RealtimeVelocity of the moving Pose relative to the reference
	 * Pose, assuming that the Pose denoted by this RealtimePoseCoincidence is
	 * moving with the given velocity and the RealtimePoseCoincidence is true.
	 *
	 * @param reference Reference pose for the motion
	 * @param moving    Moving pose to calculate the velocity of
	 * @param velocity  velocity of the Pose denoted by this RealtimePoseCoincidence
	 * @param topology  topology to use
	 * @return velocity of moving relative to reference, expressed in the reference
	 *         orientation with moving pivot point.
	 * @throws TransformationException
	 */
	public RealtimeTwist getRealtimeTwist(RealtimePose reference, RealtimePose moving, RealtimeVelocity velocity,
			FrameTopology topology) throws TransformationException {

		Frame rRef = reference.getReference();
		Frame vRef = velocity.getReferenceFrame();
		Frame mRef = moving.getReference();
		Frame aRef = a.getReference();
		Frame bRef = b.getReference();

		if (rRef.getRelationsTo(aRef, topology.without(bRef)) != null
				&& mRef.getRelationsTo(bRef, topology.without(aRef)) != null) {
			// r -> a -> b -> m
			RealtimeTwist t1 = reference.getRealtimeTwistOf(aRef.asRealtimePose(), topology);
			RealtimeTwist t2, t3, t4;
			if (aRef.getRelationsTo(vRef, topology.without(bRef)) != null) {
				t2 = aRef.getRealtimeTwistOf(vRef, topology);
				t3 = velocity.getTwist();
				t4 = b.getRealtimeTwistOf(bRef.asRealtimePose(), topology);
			} else if (bRef.getRelationsTo(vRef, topology.without(aRef)) != null) {
				t2 = a.getRealtimeVelocity().getTwist();
				t3 = velocity.getTwist().invert();
				t4 = vRef.getRealtimeTwistOf(bRef, topology);
			} else {
				throw new TransformationException(
						"Velocity is not based on any of the coincident Pose reference frames.");
			}
			RealtimeTwist t5 = bRef.asRealtimePose().getRealtimeTwistOf(moving, topology);
			return t1.add(t2).add(t3).add(t4).add(t5);
		} else if (rRef.getRelationsTo(bRef, topology.without(aRef)) != null
				&& mRef.getRelationsTo(aRef, topology.without(bRef)) != null) {
			// r -> b -> a -> m
			RealtimeTwist t1 = reference.getRealtimeTwistOf(bRef.asRealtimePose(), topology);
			RealtimeTwist t2, t3, t4;
			if (bRef.getRelationsTo(vRef, topology.without(aRef)) != null) {
				t2 = bRef.getRealtimeTwistOf(vRef, topology);
				t3 = velocity.getTwist();
				t4 = a.getRealtimeVelocity().getTwist().invert();
			} else if (aRef.getRelationsTo(vRef, topology.without(bRef)) != null) {
				t2 = b.getRealtimeVelocity().getTwist();
				t3 = velocity.getTwist().invert();
				t4 = vRef.getRealtimeTwistOf(aRef, topology);
			} else {
				throw new TransformationException(
						"Velocity is not based on any of the coincident Pose reference frames.");
			}
			RealtimeTwist t5 = aRef.asRealtimePose().getRealtimeTwistOf(moving, topology);
			return t1.add(t2).add(t3).add(t4).add(t5);
		} else {
			throw new TransformationException("Velocity between " + reference + " and " + moving
					+ " is not covered by the defined RealtimePoseCoincidence and Velocity " + velocity + ".");
		}
	}

	public RealtimeVelocity getRealtimeVelocity(Frame reference, RealtimePose moving, RealtimeVelocity velocity,
			FrameTopology topology) throws TransformationException {
		return RealtimeVelocity.createFromTwist(reference, null, reference.asOrientation(),
				getRealtimeTwist(reference.asRealtimePose(), moving, velocity, topology));
	}

}
