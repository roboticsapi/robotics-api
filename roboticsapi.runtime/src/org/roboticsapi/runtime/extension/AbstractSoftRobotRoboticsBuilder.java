/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.runtime.SoftRobotRuntime;

public abstract class AbstractSoftRobotRoboticsBuilder extends AbstractRoboticsObjectBuilder
		implements RoboticsObjectListener {

	public AbstractSoftRobotRoboticsBuilder(Class<?>... classes) {
		super(classes);
	}

	@Override
	public final void onAvailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof SoftRobotRuntime) {
			SoftRobotRuntime runtime = (SoftRobotRuntime) roboticsObject;

			String[] extensions = getRuntimeExtensions();

			if (extensions != null) {
				for (String s : extensions) {
					runtime.addExtension(s);
				}
			}
			onRuntimeAvailable(runtime);
		}
		onRoboticsObjectAvailable(roboticsObject);
	}

	protected abstract void onRuntimeAvailable(SoftRobotRuntime runtime);

	protected abstract String[] getRuntimeExtensions();

	@Override
	public final void onUnavailable(RoboticsObject roboticsObject) {
		if (roboticsObject instanceof SoftRobotRuntime) {
			SoftRobotRuntime runtime = (SoftRobotRuntime) roboticsObject;

			String[] extensions = getRuntimeExtensions();

			if (extensions != null) {
				for (String s : extensions) {
					runtime.removeExtension(s);
				}
			}
			onRuntimeUnavailable(runtime);
		}
		onRoboticsObjectUnavailable(roboticsObject);
	}

	protected abstract void onRuntimeUnavailable(SoftRobotRuntime runtime);

	protected void onRoboticsObjectAvailable(RoboticsObject roboticsObject) {
	}

	protected void onRoboticsObjectUnavailable(RoboticsObject roboticsObject) {
	}

}
