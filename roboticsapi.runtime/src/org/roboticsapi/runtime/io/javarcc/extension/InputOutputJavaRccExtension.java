/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.javarcc.extension;

import org.roboticsapi.runtime.io.javarcc.interfaces.JInputOutputInterface;
import org.roboticsapi.runtime.io.javarcc.primitiives.JInBool;
import org.roboticsapi.runtime.io.javarcc.primitiives.JInReal;
import org.roboticsapi.runtime.io.javarcc.primitiives.JOutBool;
import org.roboticsapi.runtime.io.javarcc.primitiives.JOutBoolSensor;
import org.roboticsapi.runtime.io.javarcc.primitiives.JOutReal;
import org.roboticsapi.runtime.io.javarcc.primitiives.JOutRealSensor;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;

public class InputOutputJavaRccExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {
		ep.registerPrimitive("IO::InBool", JInBool.class);
		ep.registerPrimitive("IO::InReal", JInReal.class);
		ep.registerPrimitive("IO::OutBool", JOutBool.class);
		ep.registerPrimitive("IO::OutReal", JOutReal.class);
		ep.registerPrimitive("IO::OutBoolSensor", JOutBoolSensor.class);
		ep.registerPrimitive("IO::OutRealSensor", JOutRealSensor.class);

		ep.registerInterface("io", JInputOutputInterface.class);
	}
}
