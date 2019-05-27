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

public class NeverActiveStatePortFactory<T extends State> extends AbstractStatePortFactory<T> {

	@Override
	public List<DataflowOutPort> createTypedStatePort(T state) throws MappingException {
		return null;
	}
}
