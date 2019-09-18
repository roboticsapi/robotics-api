/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule;

public class JSchedulingRule implements NetSynchronizationRule {
	private JCondition condition;
	private List<JNet> killNets = new ArrayList<JNet>();
	private List<JNet> cancelNets = new ArrayList<JNet>();
	private List<JNet> startNets = new ArrayList<JNet>();

	public JSchedulingRule(JCondition condition) {
		this.condition = condition;
	}

	public JSchedulingRule killing(JNet... nets) {
		for (JNet net : nets)
			killNets.add(net);
		return this;
	}

	public JSchedulingRule starting(JNet... nets) {
		for (JNet net : nets)
			startNets.add(net);
		return this;
	}

	public JSchedulingRule cancelling(JNet... nets) {
		for (JNet net : nets)
			cancelNets.add(net);
		return this;
	}

	public List<JNet> getStartNets() {
		return startNets;
	}

	public List<JNet> getKillNets() {
		return killNets;
	}

	public List<JNet> getCancelNets() {
		return cancelNets;
	}

	public JCondition getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		return hashCode() + "[" + condition + ", " + startNets + ", " + cancelNets + ", " + killNets + "]";
	}

	public void markFired() {
		status = SynchronizationRuleStatus.FIRED;
		for (SynchronizationRuleListener listener : listeners)
			listener.ruleStatusChanged(status);
		listeners.clear();
	}

	public void markInactive() {
		status = SynchronizationRuleStatus.INACTIVE;
		for (SynchronizationRuleListener listener : listeners)
			listener.ruleStatusChanged(status);
		listeners.clear();
	}

	public void markFailed() {
		status = SynchronizationRuleStatus.FIREFAILED;
		for (SynchronizationRuleListener listener : listeners)
			listener.ruleStatusChanged(status);
		listeners.clear();
	}

	SynchronizationRuleStatus status = SynchronizationRuleStatus.ACTIVE;
	List<SynchronizationRuleListener> listeners = new ArrayList<SynchronizationRuleListener>();

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