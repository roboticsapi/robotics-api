/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

/**
 * A Robotics API world model {@link Relation} that describes the geometric
 * relationship between two {@link Frame}s.
 */
public abstract class GeometricRelation extends Relation {

	protected GeometricRelation(Frame from, Frame to) {
		super(from, to);
	}

	/**
	 * Provides information whether the geometric relationship between the
	 * {@link Frame}s has a constant {@link Transformation}.
	 *
	 * @return <code>true</code> if the described {@link Transformation} is
	 *         constant.
	 */
	public boolean isConstant() {
		return getTransformation().isConstant();
	}

	/**
	 * Gets a {@link RealtimeTransformation} that delivers the
	 * {@link Transformation} between "from" and "to" {@link Frame}.
	 *
	 * @return {@link RealtimeTransformation} delivering the current
	 *         {@link Transformation}
	 */
	protected abstract RealtimeTransformation getTransformation();

	/**
	 * Gets a {@link RealtimeTwist} which delivers the velocity of this Relation's
	 * "to" frame relative to its "from" Frame.
	 *
	 * The velocity is measured using the "from" Frame's {@link Orientation} and the
	 * "to" Frame's {@link Point} as Pivot Point (see {@link RealtimeVelocity} and
	 * {@link Twist}).
	 *
	 * @return {@link RealtimeTwist} delivering velocity of "to" Frame relative to
	 *         "from" Frame, using "from" as {@link Orientation} and "to" as pivot
	 *         {@link Point}
	 */
	protected abstract RealtimeTwist getTwist();

	/**
	 * Retrieves the {@link RealtimeTransformation} describing the current geometric
	 * relationship between the two {@link Frame}s in the given
	 * {@link FrameTopology}.
	 *
	 * @param topology frame topology to use
	 * @return {@link RealtimeTransformation} between the two Frames
	 */
	public RealtimeTransformation getRealtimeTransformation(FrameTopology topology) {
		return topology.getRealtimeTransformation(this);
	}

	/**
	 * Retrieves the {@link RealtimeTransformation} describing the current geometric
	 * relationship to the given {@link Frame} in the given {@link FrameTopology}.
	 *
	 * @param to       {@link Frame} to give the {@link RealtimeTransformation} for,
	 *                 must be {@link Relation#getFrom()} or
	 *                 {@link Relation#getTo()}
	 * @param topology frame topology to use
	 * @return {@link RealtimeTransformation} to the given {@link Frame}
	 */
	public RealtimeTransformation getRealtimeTransformationTo(Frame to, FrameTopology topology) {
		RealtimeTransformation ret = topology.getRealtimeTransformation(this);
		if (getTo() != to) {
			return ret.invert();
		} else {
			return ret;
		}
	}

	protected boolean checkTransformation(Transformation trans, FrameTopology situation) {
		try {
			Transformation t = getFrom().getTransformationTo(getTo(), World.getCommandedTopology().without(this));
			if (t != null && !trans.isEqualTransformation(t)) {
				return false;
			}
		} catch (TransformationException e) {
			// exception is thrown if no transformation was found
		}
		return true;
	}
}
