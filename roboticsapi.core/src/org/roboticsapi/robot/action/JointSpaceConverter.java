/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.action;

import org.roboticsapi.cartesianmotion.action.CartesianPositionAction;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.ModifiedAction;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.JointSpaceAction;

public class JointSpaceConverter<A extends Action & ProcessAction & CartesianPositionAction> extends ModifiedAction<A>
		implements ProcessAction, JointSpaceAction {

	private final MultiJointDevice device;

	public JointSpaceConverter(A innerAction, MultiJointDevice device) {
		super(innerAction);
		this.device = device;
	}

	public MultiJointDevice getDevice() {
		return device;
	}

}
