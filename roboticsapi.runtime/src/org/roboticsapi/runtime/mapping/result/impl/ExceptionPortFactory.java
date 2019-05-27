/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.List;

import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public interface ExceptionPortFactory {
	public List<DataflowOutPort> createExceptionPort(CommandRealtimeException exception) throws MappingException;
}
