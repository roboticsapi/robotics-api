/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.List;

import org.roboticsapi.core.State;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;

public abstract class AbstractStatePortFactory<T extends State> implements StatePortFactory {

	private NetFragment fragment;

	@SuppressWarnings("unchecked")
	@Override
	public List<DataflowOutPort> createStatePort(State state) throws MappingException {
		return createTypedStatePort((T) state);
	}

	@Override
	public void setNetFragment(NetFragment fragment) {
		this.fragment = fragment;

	}

	public abstract List<DataflowOutPort> createTypedStatePort(T state) throws MappingException;

	protected NetFragment getFragment() {
		return fragment;
	}

}
