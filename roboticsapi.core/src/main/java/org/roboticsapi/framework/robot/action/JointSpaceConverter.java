/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.ModifiedAction;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.framework.cartesianmotion.action.CartesianPositionAction;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.action.JointSpaceAction;

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

	@Override
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return getInnerAction().getCompletedState(scope);
	}

}
