/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JInputOutputInterface;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JInBool;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JInReal;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JOutBool;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JOutBoolSensor;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JOutReal;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JOutRealSensor;

public class InputOutputJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("IO::InBool", JInBool.class);
		ep.registerPrimitive("IO::InReal", JInReal.class);
		ep.registerPrimitive("IO::OutBool", JOutBool.class);
		ep.registerPrimitive("IO::OutReal", JOutReal.class);
		ep.registerPrimitive("IO::OutBoolSensor", JOutBoolSensor.class);
		ep.registerPrimitive("IO::OutRealSensor", JOutRealSensor.class);

		ep.registerInterface("io",
				(device) -> device instanceof JInputOutputInterface
						? new RpiParameters().with("anin", "" + ((JInputOutputInterface) device).getNumAnalogIn())
								.with("anout", "" + ((JInputOutputInterface) device).getNumAnalogOut())
								.with("digin", "" + ((JInputOutputInterface) device).getNumDigitalIn()).with("digout",
										"" + ((JInputOutputInterface) device).getNumDigitalOut())
						: null);
	}
}
