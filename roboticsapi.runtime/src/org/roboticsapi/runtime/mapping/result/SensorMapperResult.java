/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result;

import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public interface SensorMapperResult<T> extends MapperResult {

	public interface SensorUpdateListener<T> extends SensorListener<T> {
		void updatePerformed();
	}

	DataflowOutPort getSensorPort() throws MappingException;

	DataflowOutPort getSensorTimePort() throws MappingException;

	List<DataflowOutPort> getStatePort(SensorState state) throws MappingException;

	void addListener(Command command, SensorListener<T> listener) throws MappingException;

	void addUpdateListener(Command command, SensorUpdateListener<T> listener) throws MappingException;

	void assign(Command command, PersistContext<T> target) throws MappingException;

}
