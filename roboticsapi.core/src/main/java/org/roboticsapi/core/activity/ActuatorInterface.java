/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public interface ActuatorInterface extends DeviceInterface {

	void addDefaultParameters(final DeviceParameters newParameters) throws InvalidParametersException;

	DeviceParameterBag getDefaultParameters();

	void cancel() throws RoboticsException;

	void endExecute() throws RoboticsException;

	Activity sleep(double seconds) throws RoboticsException;

	Activity sleep(RealtimeBoolean waitFor) throws RoboticsException;

	Activity ifElse(String name, RealtimeBoolean condition, Activity ifTrue, Activity ifFalse) throws RoboticsException;

}