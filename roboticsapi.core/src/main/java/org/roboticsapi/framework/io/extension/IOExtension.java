/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.extension;

import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.framework.io.FieldbusCoupler;
import org.roboticsapi.framework.io.analog.AnalogInput;
import org.roboticsapi.framework.io.analog.AnalogOutput;
import org.roboticsapi.framework.io.digital.DigitalInput;
import org.roboticsapi.framework.io.digital.DigitalOutput;

public class IOExtension extends AbstractRoboticsObjectBuilder {

	public IOExtension() {
		super(AnalogOutput.class, AnalogInput.class, DigitalInput.class, DigitalOutput.class, FieldbusCoupler.class);
	}

}
