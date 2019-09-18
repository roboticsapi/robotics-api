/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class StaticPosition extends GeometricRelation {

	private final Transformation transformation;

	public StaticPosition(Frame from, Frame to, Transformation transformation) {
		super(from, to);
		this.transformation = transformation;
	}

	@Override
	protected RealtimeTransformation getTransformation() {
		return RealtimeTransformation.createfromConstant(transformation);
	}

	@Override
	protected RealtimeTwist getTwist() {
		return RealtimeTwist.createFromConstant(new Twist());
	}

}
