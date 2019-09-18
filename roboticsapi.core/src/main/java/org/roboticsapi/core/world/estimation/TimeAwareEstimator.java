/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.observation.FramePoseObservation;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;

public class TimeAwareEstimator extends AbstractEstimator {
	private final double DURATION = 0.35;
	private final double CONSISTENT_RANGE = 0.05;

	@Override
	protected Estimation buildEstimation(FramePoseObservation observation, Frame from, Frame to,
			List<Relation> relations, List<Relation> deps) throws TransformationException {
		Frame observedFrame = observation.getTo();
		RealtimePose pose = observation.getPose();
		RealtimeVelocity twist = observation.getTwist();

		RealtimePoseCoincidence coincidence = new RealtimePoseCoincidence(pose, observedFrame.asRealtimePose());
		RealtimePose transformation = coincidence.getRealtimePose(from, to.asRealtimePose(), getTopology());
		RealtimeDouble age = RealtimeValue.getConsistentTime(DURATION, CONSISTENT_RANGE,
				collectInnerValues(transformation));
		RealtimeTransformation transformationExtrapolated = transformation.getTransformation()
				.substitute(createSubstitutionForAge(transformation, age));

		RealtimeTwist velocity = null;
		if (twist != null) {
			// find twist if relation is variable
			boolean variable = false;
			for (Relation r : relations) {
				if (r instanceof LogicalRelation && ((LogicalRelation) r).isVariable()) {
					variable = true;
				}
			}
			if (variable) {
				velocity = RealtimeVelocity
						.createFromTwist(from, null, from.asOrientation(),
								coincidence.getRealtimeTwist(from.asRealtimePose(), to.asRealtimePose(), twist,
										getTopology()))
						.getTwistForRepresentation(null, null, to.asRealtimePose(), getTopology());
				velocity = velocity.substitute(createSubstitutionForAge(velocity, age));

				age = RealtimeDouble.createConditional(age.greater(DURATION),
						RealtimeDouble.createFromConstant(DURATION), age);

				transformationExtrapolated = transformationExtrapolated.multiply(
						RealtimeTransformation.createFromVectorRotation(velocity.getTranslationVelocity().scale(age),
								RealtimeRotation.createFromAxisAngle(velocity.getRotationVelocity(),
										velocity.getRotationVelocity().scale(age).getLength())));

				transformationExtrapolated = transformationExtrapolated.slidingAverage(0.05);

				RealtimeTwist t = velocity;
				RealtimeTwist.createFromLinearAngular(t.getTranslationVelocity().slidingAverage(0.05),
						t.getRotationVelocity().slidingAverage(0.05));
				velocity = t;
			} else {
				transformationExtrapolated = transformationExtrapolated.slidingAverage(0.1);
			}
		} else {
			transformationExtrapolated = transformationExtrapolated.slidingAverage(0.1);
		}

		Estimation ret = new Estimation(from, to, transformationExtrapolated, velocity, observation, relations, deps);
		return ret;
	}

	private Map<RealtimeValue<?>, RealtimeValue<?>> createSubstitutionForAge(RealtimeValue<?> realtimeValue,
			RealtimeDouble time) {
		Map<RealtimeValue<?>, RealtimeValue<?>> ret = new HashMap<RealtimeValue<?>, RealtimeValue<?>>();
		Set<RealtimeValue<?>> inners = collectInnerValues(realtimeValue);

		if (time == null || time.isConstant() && time.getCheapValue() == 0) {
			return ret;
		}

		for (RealtimeValue<?> rv : inners) {
			if (rv.isConstant()) {
				continue;
			}
			RealtimeValue<?> atTime = rv.forAge(time, DURATION);
			if (atTime == null) {
				continue;
			}
			ret.put(rv, atTime);
		}
		return ret;
	}

	private Set<RealtimeValue<?>> collectInnerValues(RealtimeValue<?> realtimeValue) {
		Set<RealtimeValue<?>> ret = new HashSet<RealtimeValue<?>>();
		for (RealtimeValue<?> v : realtimeValue.getDependencies()) {
			ret.addAll(collectInnerValues(v));
		}
		if (ret.isEmpty()) {
			ret.add(realtimeValue);
		}
		return ret;
	}

}
