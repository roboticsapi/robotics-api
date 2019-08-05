/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.softrobot;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.extension.Extension;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.javarcc.primitives.CoreExtension;
import org.roboticsapi.runtime.extension.SoftRobotRuntimeExtension;
import org.roboticsapi.runtime.generic.RuntimeSetup;
import org.roboticsapi.runtime.io.extension.SoftRobotIOExtension;
import org.roboticsapi.runtime.io.javarcc.extension.InputOutputJavaRccExtension;
import org.roboticsapi.runtime.world.extension.RPIWorldExtension;
import org.roboticsapi.runtime.world.javarcc.primitives.WorldExtension;

public final class SoftRobotRuntimeSetup implements RuntimeSetup {

	private final List<Extension> extensions = new ArrayList<Extension>();

	public SoftRobotRuntimeSetup() {
		extensions.add(new SoftRobotRuntimeExtension());
		extensions.add(new SoftRobotIOExtension());
		extensions.add(new RPIWorldExtension());
		extensions.add(new CoreExtension());
		extensions.add(new WorldExtension());
		extensions.add(new InputOutputJavaRccExtension());
	}

	@Override
	public SoftRobotRuntime createRuntime() throws InitializationException {
		return createRuntime(null);
	}

	@Override
	public SoftRobotRuntime createRuntime(String name) throws InitializationException {
		SoftRobotRuntime rt = new SoftRobotRuntime();
		rt.setName("Runtime" + (name != null ? name : ""));
		return rt;
	}

	@Override
	public void destroyRuntime(RoboticsRuntime rt) throws RoboticsException {
	}

	@Override
	public void registerExtensions(RoboticsContext context) {
		for (Extension e : extensions) {
			context.addExtension(e);
		}
	}

	@Override
	public void unregisterExtensions(RoboticsContext context) {
		for (Extension e : extensions) {
			context.removeExtension(e);
		}
	}

}
