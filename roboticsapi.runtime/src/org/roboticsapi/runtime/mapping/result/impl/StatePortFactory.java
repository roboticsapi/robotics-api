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

public interface StatePortFactory {

	/**
	 * Creates a {@link DataflowOutPort} for the given {@link State}.
	 * 
	 * @param state the State
	 * @return the port representing the State or null, if not applicable
	 * @throws MappingException if another port has already been registered for this
	 *                          State, or if the port could not be created
	 */
	public List<DataflowOutPort> createStatePort(State state) throws MappingException;

	public void setNetFragment(NetFragment fragment);
}
