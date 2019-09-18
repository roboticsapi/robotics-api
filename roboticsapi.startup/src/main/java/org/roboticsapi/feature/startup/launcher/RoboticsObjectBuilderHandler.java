/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.extension.RoboticsObjectBuilder;
import org.roboticsapi.feature.startup.configuration.util.RoboticsObjectFactory;

public final class RoboticsObjectBuilderHandler
		implements ExtensionHandler<RoboticsObjectBuilder>, RoboticsObjectFactory {

	private final List<RoboticsObjectBuilder> builders = new ArrayList<RoboticsObjectBuilder>();
	private final Map<String, List<Callback>> openTypes = new HashMap<String, List<Callback>>();

	@Override
	public synchronized void addExtension(RoboticsObjectBuilder e) {
		builders.add(e);
		String[] types = e.getProvidedTypes();
		for (String type : types) {
			if (openTypes.containsKey(type)) {
				for (Callback callback : new ArrayList<Callback>(openTypes.get(type))) {
					if (build(type, e, callback)) {
						openTypes.get(type).remove(callback);
					}
				}
				if (openTypes.get(type).isEmpty()) {
					openTypes.remove(type);
				}
			}
		}
	}

	@Override
	public synchronized void removeExtension(RoboticsObjectBuilder e) {
		builders.remove(e);
	}

	@Override
	public Class<RoboticsObjectBuilder> getType() {
		return RoboticsObjectBuilder.class;
	}

	@Override
	public synchronized boolean build(String type, Callback callback) {
		for (RoboticsObjectBuilder builder : this.builders) {
			if (builder.canBuild(type)) {
				if (build(type, builder, callback)) {
					return true;
				}
			}
		}

		if (!openTypes.containsKey(type)) {
			openTypes.put(type, new ArrayList<Callback>());
		}
		openTypes.get(type).add(callback);
		return false;
	}

	private boolean build(String type, RoboticsObjectBuilder builder, Callback callback) {
		RoboticsObject object;
		try {
			object = builder.build(type);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		try {
			callback.onBuilt(object);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return true;
	}

}
