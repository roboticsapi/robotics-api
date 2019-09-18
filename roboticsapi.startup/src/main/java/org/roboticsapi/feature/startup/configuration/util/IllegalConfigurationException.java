/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An exception that can occur during the configuration of
 * {@code RoboticsObject}s.
 */
public class IllegalConfigurationException extends RoboticsException {

	private static final long serialVersionUID = 510185172215299688L;

	private final String key;
	private Configuration configuration;

	/**
	 * Constructs a new {@code ConfigurationException}.
	 *
	 * @param key            configuration property key causing the problem
	 * @param innerException the cause
	 */
	public IllegalConfigurationException(String key, Exception innerException) {
		super(innerException);
		this.key = key;
	}

	/**
	 * Constructs a new {@code ConfigurationException}.
	 *
	 * @param key            configuration property key causing the problem
	 * @param message        the problem message
	 * @param innerException the cause
	 */
	public IllegalConfigurationException(String key, String message, Exception innerException) {
		super(message, innerException);
		this.key = key;
	}

	/**
	 * Constructs a new {@code ConfigurationException}.
	 *
	 * @param key     configuration property key causing the problem
	 * @param message the problem message
	 */
	public IllegalConfigurationException(String key, String message) {
		this(key, message, null);
	}

	/**
	 * Constructs a new {@code ConfigurationException}.
	 *
	 * @param key     configuration property key causing the problem
	 * @param message the problem message
	 */
	public IllegalConfigurationException(String object, String type, String key, String message,
			Exception innerException) {
		this(key, message, innerException);
		setConfiguration(new Configuration(object, type));
	}

	/**
	 * Gets the configuration causing the problem
	 *
	 * @return the configuration causing the problem, or {@code null} if unknown.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Sets the configuration causing the problem
	 *
	 * @param configuration the configuration causing the problem
	 */
	protected void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Gets the key of the configuration property causing the problem. Empty for
	 * generic problems.
	 *
	 * @return key of the configuration property causing the problem, or "" for
	 *         generic problems.
	 */
	public String getKey() {
		return key;
	}
}
