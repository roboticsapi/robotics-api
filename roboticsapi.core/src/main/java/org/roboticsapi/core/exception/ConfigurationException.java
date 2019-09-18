/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.RoboticsObject;

public class ConfigurationException extends InitializationException {

	private static final long serialVersionUID = 1L;
	private final String key;
	private final String plainMessage;
	private final RoboticsObject parent;

	public ConfigurationException(RoboticsObject parent, Dependency<?> configurationProperty, String message) {
		this(parent, configurationProperty, message, null);
	}

	public ConfigurationException(RoboticsObject parent, Dependency<?> configurationProperty, String message,
			Throwable innerException) {
		this(parent, configurationProperty.getName(), message, innerException);
	}

	public ConfigurationException(RoboticsObject parent, String configurationPropertyName, String message) {
		this(parent, configurationPropertyName, message, null);
	}

	public ConfigurationException(RoboticsObject parent, String configurationPropertyName, String message,
			Throwable innerException) {
		super("Error in configuration '" + configurationPropertyName + "'"
				+ (parent != null ? " of '" + parent.getClass().getSimpleName() + "'" : "") + ": " + message,
				innerException);
		this.plainMessage = message;
		this.key = configurationPropertyName;
		this.parent = parent;
	}

	public final String getKey() {
		return key;
	}

	public final String getPlainMessage() {
		return plainMessage;
	}

	public final RoboticsObject getParent() {
		return parent;
	}

}
