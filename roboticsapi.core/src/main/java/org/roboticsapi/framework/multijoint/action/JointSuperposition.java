/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.ModifiedAction;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class JointSuperposition<A extends Action & ProcessAction & JointSpaceAction> extends ModifiedAction<A>
		implements JointSpaceAction {

	private final Map<Integer, RealtimeDouble> superpositions = new HashMap<Integer, RealtimeDouble>();

	public JointSuperposition(A superposedAction, int jointNr, RealtimeDouble superposition) {
		super(superposedAction);
		superpositions.put(jointNr, superposition);
	}

	public RealtimeDouble getSuperposition(int jointNr) {
		return superpositions.get(jointNr);
	}

	@Override
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return getInnerAction().getCompletedState(scope);
	}

}
