/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.observation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.core.world.relation.AbstractConfiguredRelation;

public class FrameObservation extends AbstractConfiguredRelation {

	@Override
	protected Relation createRelation(Frame from, Frame to) {
		return new FramePoseObservation(from, to,
				RealtimePose.createFromTransformation(from,
						RealtimeTransformation.createfromConstant(Transformation.IDENTITY)),
				RealtimeVelocity.createFromTwist(from, null, null, RealtimeTwist.createFromConstant(new Twist())));
	}

}
