/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.io.FieldbusCoupler;
import org.roboticsapi.io.activity.AnalogOutputInterfaceImpl;
import org.roboticsapi.io.activity.DigitalOutputInterfaceImpl;
import org.roboticsapi.io.analog.AnalogInput;
import org.roboticsapi.io.analog.AnalogOutput;
import org.roboticsapi.io.digital.DigitalInput;
import org.roboticsapi.io.digital.DigitalOutput;

public class IOActivityExtension extends AbstractRoboticsObjectBuilder implements RoboticsObjectListener {

	public IOActivityExtension() {
		super(AnalogOutput.class, AnalogInput.class, DigitalInput.class, DigitalOutput.class, FieldbusCoupler.class);
	}

	@Override
	public void onAvailable(final RoboticsObject builtObject) {
		if (builtObject instanceof DigitalOutput) {
			((DigitalOutput) builtObject)
					.addInterfaceFactory(new SingleDeviceInterfaceFactory<DigitalOutputInterfaceImpl>() {

						@Override
						protected DigitalOutputInterfaceImpl build() {
							return new DigitalOutputInterfaceImpl((DigitalOutput) builtObject);
						}
					});
		}

		if (builtObject instanceof AnalogOutput) {
			((AnalogOutput) builtObject)
					.addInterfaceFactory(new SingleDeviceInterfaceFactory<AnalogOutputInterfaceImpl>() {

						@Override
						protected AnalogOutputInterfaceImpl build() {
							return new AnalogOutputInterfaceImpl((AnalogOutput) builtObject);
						}
					});
		}

	}

	@Override
	public void onUnavailable(RoboticsObject object) {

	}

}
