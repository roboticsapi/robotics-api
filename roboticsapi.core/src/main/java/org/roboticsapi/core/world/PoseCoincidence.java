/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class PoseCoincidence {

	private final Pose a;
	private final Pose b;

	/**
	 * Class describing that two {@link Pose}s describe the same position in space
	 *
	 * @param a first Pose for the place in space
	 * @param b second Pose for the place in space
	 */
	public PoseCoincidence(Pose a, Pose b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Calculates the transformation from the first Pose to the second, assuming
	 * that the PoseCoincidence is true
	 *
	 * @param from start pose
	 * @param to   goal pose
	 * @return Transformation between start and goal pose, assuming the defined pose
	 *         coincidence
	 * @throws TransformationException
	 */
	public Transformation getTransformation(Pose from, Pose to) throws TransformationException {
		Frame fRef = from.getReference();
		Frame tRef = to.getReference();
		Frame aRef = a.getReference();
		Frame bRef = b.getReference();

		if (fRef.getRelationsTo(aRef, World.getCommandedTopology().without(bRef, tRef)) != null
				&& tRef.getRelationsTo(bRef, World.getCommandedTopology().without(aRef, fRef)) != null) {
			// f -> a -> b -> t
			Transformation t1 = from.getTransformationForRepresentation(fRef.asOrientation()).invert();
			Transformation t2 = fRef.getTransformationTo(aRef);
			Transformation t3 = a.getTransformationForRepresentation(aRef.asOrientation());
			Transformation t4 = b.getTransformationForRepresentation(bRef.asOrientation()).invert();
			Transformation t5 = bRef.getTransformationTo(tRef);
			Transformation t6 = to.getTransformationForRepresentation(tRef.asOrientation());
			return t1.multiply(t2).multiply(t3).multiply(t4).multiply(t5).multiply(t6);
		} else if (fRef.getRelationsTo(bRef, World.getCommandedTopology().without(bRef, tRef)) != null
				&& tRef.getRelationsTo(aRef, World.getCommandedTopology().without(bRef, fRef)) != null) {
			// f -> b -> a -> t
			Transformation t1 = from.getTransformationForRepresentation(fRef.asOrientation()).invert();
			Transformation t2 = fRef.getTransformationTo(bRef);
			Transformation t3 = b.getTransformationForRepresentation(bRef.asOrientation());
			Transformation t4 = a.getTransformationForRepresentation(aRef.asOrientation()).invert();
			Transformation t5 = aRef.getTransformationTo(tRef);
			Transformation t6 = to.getTransformationForRepresentation(tRef.asOrientation());
			return t1.multiply(t2).multiply(t3).multiply(t4).multiply(t5).multiply(t6);
		} else {
			throw new TransformationException("Transformation between " + from + " and " + to
					+ " is not covered by the defined PoseCoincidence between " + a + " and " + b + ".");
		}
	}

	public Pose getPose(Frame reference, Pose goal) throws TransformationException {
		return new Pose(reference, getTransformation(reference.asPose(), goal));
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
	public Twist getTwist(Pose reference, Pose moving, Velocity velocity) throws TransformationException {
		RealtimeTwist realtimeResult = new RealtimePoseCoincidence(a.asRealtimeValue(), b.asRealtimeValue())
				.getRealtimeTwist(reference.asRealtimeValue(), moving.asRealtimeValue(), velocity.asRealtimeValue(),
						World.getCommandedTopology().withoutDynamic());
		try {
			return realtimeResult.getCurrentValue();
		} catch (RealtimeValueReadException e) {
			throw new TransformationException(e);
		}
	}

	public Velocity getVelocity(Frame reference, Pose moving, Velocity velocity) throws TransformationException {
		return new Velocity(reference, null, reference.asOrientation(), getTwist(reference.asPose(), moving, velocity));
	}

}
