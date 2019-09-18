/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.configuration.util;

public class MissingObjectRefException extends IllegalConfigurationException {
	private static final long serialVersionUID = 5770703616723499789L;
	private final String objectName;

	public MissingObjectRefException(String key, String objectName, Exception innerException) {
		super(key, "Referenced object " + objectName + " not found.", innerException);
		this.objectName = objectName;
	}

	public MissingObjectRefException(String key, String objectName) {
		super(key, "Referenced object " + objectName + " not found.");
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}

}
