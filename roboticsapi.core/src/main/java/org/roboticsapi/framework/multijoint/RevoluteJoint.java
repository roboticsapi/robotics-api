/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.core.world.relation.RevoluteConnection;
import org.roboticsapi.core.world.relation.SingleDofConnection;

/**
 * Implementation for a revolute joint controlling the rotation between the
 * fixed and moving frame around the Z axis
 *
 * @param <JD> type of joint driver used
 */
public class RevoluteJoint<JD extends JointDriver> extends AbstractJoint<JD> {

	@Override
	public SingleDofConnection createConnection(Frame from, Frame to) {
		return new RevoluteConnection(from, to);
	}

	@Override
	public RealtimeTransformation getTransformationSensor(RealtimeDouble angle) {
		return RealtimeTransformation.createFromVectorRotation(RealtimeVector.createFromConstant(new Vector()),
				RealtimeRotation.createFromAxisAngle(new Vector(0, 0, 1), angle));
	}

}
