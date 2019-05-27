/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.ActiveFragment;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class ActiveNetFragment extends NetFragment {

	private final DataflowOutPort activePort;

	public ActiveNetFragment(String name, DataflowOutPort activePort) {
		super(name);
		this.activePort = activePort;
	}

	public DataflowOutPort getActivePort() {
		return activePort;
	}

	@Override
	public void addToFragment(Fragment fragment) throws MappingException {
		try {
			ActiveFragment child = new ActiveFragment();
			fragment.add(child);
			child.getInActive().connectTo(activePort.getPorts().get(0));
			super.addToFragment(child);
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}
}
