/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.facet.runtime.rpi.Primitive;

public abstract class RealtimeValueConsumerFragment extends DependentFragment implements RealtimeValueSource {
	List<RealtimeValueFragmentFactory> fragmentFactories = new ArrayList<RealtimeValueFragmentFactory>();
	List<RealtimeValueAliasFactory> aliasFactories = new ArrayList<RealtimeValueAliasFactory>();

	public RealtimeValueConsumerFragment() {
	}

	public RealtimeValueConsumerFragment(Primitive... children) {
		for (Primitive prim : children)
			add(prim);
	}

	public <U> void addRealtimeValueFragmentFactory(RealtimeValueFragmentFactory producer) {
		fragmentFactories.add(producer);
	}

	public <U> void addRealtimeValueAliasFactory(RealtimeValueAliasFactory producer) {
		aliasFactories.add(producer);
	}

	@Override
	public List<RealtimeValueFragmentFactory> getRealtimeValueFragmentFactories() {
		return fragmentFactories;
	}

	@Override
	public List<RealtimeValueAliasFactory> getRealtimeValueAliasFactories() {
		return aliasFactories;
	}

	public void addWithDependencies(RealtimeValueConsumerFragment innerFragment) {
		add(innerFragment);
		for (RealtimeValueAliasFactory factory : innerFragment.getRealtimeValueAliasFactories())
			addRealtimeValueAliasFactory(factory);
		for (RealtimeValueFragmentFactory factory : innerFragment.getRealtimeValueFragmentFactories())
			addRealtimeValueFragmentFactory(factory);

		for (Dependency<?> dep : innerFragment.getDependencies()) {
			if (dep.getTimePort() != null) {
				addDependency(dep.getValue(),
						addInPort(dep.getValuePort().getName() + "_" + innerFragment.getName(), dep.getValuePort()),
						addInPort(dep.getTimePort().getName() + "_" + innerFragment.getName(), dep.getTimePort()));
			} else {
				addDependency(dep.getValue(),
						addInPort(dep.getValuePort().getName() + "_" + innerFragment.getName(), dep.getValuePort()));

			}
		}

		for (PortDependency dep : innerFragment.getPortDependencies()) {
			addDependency(addInPort(dep.getInPort().getName() + "_" + innerFragment.getName(), dep.getInPort()),
					dep.getSource());
		}
	}
}
