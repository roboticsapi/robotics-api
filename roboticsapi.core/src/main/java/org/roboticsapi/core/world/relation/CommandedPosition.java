/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class CommandedPosition extends GeometricRelation {

	private final RealtimeTransformation transformation;
	private final RealtimeTwist twist;

	public CommandedPosition(Frame from, Frame to, RealtimeTransformation transformation, RealtimeTwist velocity) {
		super(from, to);
		this.transformation = transformation;
		this.twist = velocity;
	}

	@Override
	protected RealtimeTransformation getTransformation() {
		return transformation;
	}

	@Override
	protected RealtimeTwist getTwist() {
		return twist;
	}

}
