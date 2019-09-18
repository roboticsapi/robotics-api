/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class ActionResult implements RealtimeValueSource {
	private List<RealtimeValueAliasFactory> aliasFactories = new ArrayList<RealtimeValueAliasFactory>();
	private List<RealtimeValueFragmentFactory> fragmentFactories = new ArrayList<RealtimeValueFragmentFactory>();

	public ActionResult(Action action, RealtimeBoolean completion) {
		addRealtimeValueAliasFactory(new ActionStateMapper<Action.CompletedRealtimeBoolean>(
				Action.CompletedRealtimeBoolean.class, action, completion));
	}

	public ActionResult(Action action, RealtimeBoolean completion, ActionResult innerResult) {
		this(action, completion);
		aliasFactories.addAll(innerResult.getRealtimeValueAliasFactories());
		fragmentFactories.addAll(innerResult.getRealtimeValueFragmentFactories());
	}

	public void addRealtimeValueAliasFactory(RealtimeValueAliasFactory factory) {
		aliasFactories.add(factory);
	}

	public void addRealtimeValueFragmentFactory(RealtimeValueFragmentFactory factory) {
		fragmentFactories.add(factory);
	}

	@Override
	public List<RealtimeValueFragmentFactory> getRealtimeValueFragmentFactories() {
		return fragmentFactories;
	}

	@Override
	public List<RealtimeValueAliasFactory> getRealtimeValueAliasFactories() {
		return aliasFactories;
	}

	public void addRealtimeValueSource(RealtimeValueSource innerResult) {
		fragmentFactories.addAll(innerResult.getRealtimeValueFragmentFactories());
		aliasFactories.addAll(innerResult.getRealtimeValueAliasFactories());
	}
}
