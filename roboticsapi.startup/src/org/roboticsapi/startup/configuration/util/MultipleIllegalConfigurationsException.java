/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A container exception for multiple {@code IllegalConfigurationException}s.
 */
public class MultipleIllegalConfigurationsException extends IllegalConfigurationException {

	private static final long serialVersionUID = -1417204466308611167L;
	List<IllegalConfigurationException> errors = new ArrayList<IllegalConfigurationException>();

	/**
	 * Construct a new {@code MultipleIllegalConfigurationsException}.
	 * 
	 * @param errors a list of {@code IllegalConfigurationException}s.
	 */
	public MultipleIllegalConfigurationsException(List<IllegalConfigurationException> errors) {
		super("", "Configuration of " + errors.size() + " objects failed: " + getFailingConfigsString(errors));
		this.errors.addAll(errors);
	}

	/**
	 * Construct a new {@code MultipleIllegalConfigurationsException}.
	 * 
	 * @param errors an array of {@code IllegalConfigurationException}s.
	 */
	public MultipleIllegalConfigurationsException(IllegalConfigurationException... errors) {
		this(Arrays.asList(errors));
	}

	/**
	 * Returns a list of {@code IllegalConfigurationException}s that occurred during
	 * loading multiple configurations.
	 * 
	 * @return a list of {@code IllegalConfigurationException}s.
	 */
	public List<IllegalConfigurationException> getErrors() {
		return errors;
	}

	public static String getFailingConfigsString(List<IllegalConfigurationException> failingConfigs) {
		StringBuilder sb = new StringBuilder();
		for (IllegalConfigurationException e : failingConfigs) {
			String name = e.getConfiguration() != null ? e.getConfiguration().getName() : "<unnamed>";

			sb.append(name).append(" (");
			if (e.getKey() != null && !"".equals(e.getKey())) {
				sb.append("Key ").append(e.getKey()).append("; ");
			}
			sb.append(e.getMessage()).append(")");

			sb.append("\n");
		}

		String str = sb.toString();
		return str.substring(0, str.length() - 1);
	}

	public String getFailingConfigsString() {
		return getFailingConfigsString(getErrors());
	}
}
