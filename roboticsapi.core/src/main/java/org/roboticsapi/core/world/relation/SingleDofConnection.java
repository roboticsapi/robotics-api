/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.StateVariable;

/**
 * The connection described and controlled by a joint
 */
public abstract class SingleDofConnection extends DynamicConnection {
	protected StateVariable position = addPositionStateVariable("pos"), velocity = addVelocityStateVariable("vel");

	protected SingleDofConnection(Frame from, Frame to) {
		super(from, to);
		position.addFlowExpression(velocity.asExpression());
	}

}