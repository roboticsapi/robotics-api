/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.action;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.ModifiedAction;
import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.sensor.DoubleSensor;

public class JointSuperposition<A extends Action & ProcessAction & JointSpaceAction> extends ModifiedAction<A>
		implements JointSpaceAction {

	private final Map<Integer, DoubleSensor> superpositions = new HashMap<Integer, DoubleSensor>();

	public JointSuperposition(A superposedAction, int jointNr, DoubleSensor superposition) {
		super(superposedAction);
		superpositions.put(jointNr, superposition);
	}

	public DoubleSensor getSuperposition(int jointNr) {
		return superpositions.get(jointNr);
	}

}
