/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

public class ConfigurationException extends InitializationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String key;

	public ConfigurationException(String key, String message) {
		super(message);
		this.key = key;
	}

	public ConfigurationException(String key, String message, Exception innerException) {
		super(message, innerException);
		this.key = key;

	}

	public String getKey() {
		return key;
	}

}
