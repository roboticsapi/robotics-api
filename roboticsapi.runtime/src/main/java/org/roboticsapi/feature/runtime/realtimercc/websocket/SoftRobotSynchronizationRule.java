/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.websocket;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule;

public class SoftRobotSynchronizationRule implements NetSynchronizationRule {

	SynchronizationRuleStatus status = SynchronizationRuleStatus.ACTIVE;
	List<SynchronizationRuleListener> listeners = new ArrayList<SynchronizationRuleListener>();

	protected void changeStatus(SynchronizationRuleStatus newStatus) {
		if (status != newStatus) {
			status = newStatus;
			for (SynchronizationRuleListener listener : listeners) {
				listener.ruleStatusChanged(status);
			}
		}
	}

	@Override
	public void addStatusListener(SynchronizationRuleListener listener) {
		listeners.add(listener);
		listener.ruleStatusChanged(status);
	}

	@Override
	public void removeStatusListener(SynchronizationRuleListener listener) {
		listeners.remove(listener);
	}

}
