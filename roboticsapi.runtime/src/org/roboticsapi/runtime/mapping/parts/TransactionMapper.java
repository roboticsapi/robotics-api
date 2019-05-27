/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.MapperInterface;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.result.TransactionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public interface TransactionMapper<R extends RoboticsRuntime, C extends Command> extends MapperInterface {
	TransactionMapperResult map(R runtime, C command) throws MappingException, RPIException;
}
