/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.extension;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;

public abstract class RuntimeExtension<T extends RoboticsRuntime> implements RoboticsObjectListener {

	private final Class<T> type;
	private final List<String> runtimeExtensions = new ArrayList<String>();

	protected RuntimeExtension(Class<T> type, String... extensions) {
		this.type = type;

		for (String e : extensions) {
			runtimeExtensions.add(e);
		}
	}

	@Override
	public final void onAvailable(RoboticsObject object) {
		if (type.isInstance(object)) {
			RoboticsRuntime runtime = type.cast(object);

			for (String s : this.runtimeExtensions) {
				runtime.addExtension(s);
			}
		}
	}

	@Override
	public final void onUnavailable(RoboticsObject object) {
		// TODO Unload/remove runtime extension

	}

}
