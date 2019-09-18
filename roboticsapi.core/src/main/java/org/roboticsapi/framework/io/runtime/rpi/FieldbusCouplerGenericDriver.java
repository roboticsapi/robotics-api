/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi;

import java.util.Map;

import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractDeviceDriver;
import org.roboticsapi.framework.io.FieldbusCoupler;
import org.roboticsapi.framework.io.FieldbusCouplerDriver;
import org.roboticsapi.framework.io.analog.AnalogInputDriver;
import org.roboticsapi.framework.io.analog.AnalogOutputDriver;
import org.roboticsapi.framework.io.digital.DigitalInputDriver;
import org.roboticsapi.framework.io.digital.DigitalOutputDriver;

/**
 * {@link FieldbusCouplerDriver} implementation for the SoftRobot RCC.
 */
public class FieldbusCouplerGenericDriver extends AbstractDeviceDriver<FieldbusCoupler>
		implements FieldbusCouplerDriver {

	private final MultiDependency<DigitalInputDriver> digitalInputs = createMultiDependency("digitalInputs",
			() -> getDevice().getNumberOfDigitalInputs(), (nr) -> new DigitalInputGenericDriver(nr, getRuntime(),
					getRpiDeviceName(), getDevice().getDigitalInput(nr)));
	private final MultiDependency<AnalogInputDriver> analogInputs = createMultiDependency("analogInputs",
			() -> getDevice().getNumberOfAnalogInputs(),
			(nr) -> new AnalogInputGenericDriver(nr, getRuntime(), getRpiDeviceName(), getDevice().getAnalogInput(nr)));
	private final MultiDependency<DigitalOutputDriver> digitalOutputs = createMultiDependency("digitalOutputs",
			() -> getDevice().getNumberOfDigitalOutputs(), (nr) -> new DigitalOutputGenericDriver(nr, getRuntime(),
					getRpiDeviceName(), getDevice().getDigitalOutput(nr)));
	private final MultiDependency<AnalogOutputDriver> analogOutputs = createMultiDependency("analogOutputs",
			() -> getDevice().getNumberOfAnalogOutputs(), (nr) -> new AnalogOutputGenericDriver(nr, getRuntime(),
					getRpiDeviceName(), getDevice().getAnalogOutput(nr)));

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return interfaces.containsKey("io");
	}

}
