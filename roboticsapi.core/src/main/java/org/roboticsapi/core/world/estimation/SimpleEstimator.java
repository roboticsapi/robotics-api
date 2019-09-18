/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import java.util.List;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.observation.FramePoseObservation;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class SimpleEstimator extends AbstractEstimator {
	@Override
	protected Estimation buildEstimation(FramePoseObservation observation, Frame from, Frame to,
			List<Relation> relations, List<Relation> deps) throws TransformationException {
		Frame observedFrame = observation.getTo();
		RealtimePose pose = observation.getPose();
		RealtimePoseCoincidence coincidence = new RealtimePoseCoincidence(pose, observedFrame.asRealtimePose());
		RealtimeTransformation trans = coincidence.getTransformation(from.asRealtimePose(), to.asRealtimePose(),
				getTopology());
		RealtimeTransformation transformationSmoothed = trans.isConstant() ? trans : trans.slidingAverage(0.1);
		RealtimeTwist velocity = coincidence.getRealtimeTwist(from.asRealtimePose(), to.asRealtimePose(),
				observation.getTwist(), getTopology());
		Estimation ret = new Estimation(from, to, transformationSmoothed, velocity, observation, relations, deps);
		return ret;
	}
}
