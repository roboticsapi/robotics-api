/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

import java.util.Collection;
import java.util.Map;

import org.roboticsapi.core.exception.InitializationException;

public class ChildObjectsRequiredException extends InitializationException {
	private static final long serialVersionUID = 1026875829761450468L;

	private final Map<String, Configuration> configs;

	public ChildObjectsRequiredException(Map<String, Configuration> configs) {
		super("Requiring child objects: " + getConfigsString(configs));
		this.configs = configs;
	}

	private static String getConfigsString(Map<String, Configuration> configs) {
		String s = "";
		for (Configuration c : configs.values()) {
			s += c.getName() + ",";
		}
		s = s.substring(0, s.length() - 2);
		return s;
	}

	public Configuration getConfiguration(String key) {
		return configs.get(key);
	}

	public Collection<String> getKeys() {
		return configs.keySet();
	}

}
