/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public class TestRoboticsEntity extends AbstractEntity {

	private final boolean init;
	private final boolean initError;
	private final boolean uninit;
	private final boolean uninitError;

	public TestRoboticsEntity(boolean init, boolean initError, boolean uninit, boolean uninitError) {
		this.init = init;
		this.initError = initError;
		this.uninit = uninit;
		this.uninitError = uninitError;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		if (!init) {
			throw new ConfigurationException("", "");
		}
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();

		if (initError) {
			throw new RoboticsException("");
		}
	}

	@Override
	protected void validateBeforeUninitialization() throws InitializationException {
		if (!uninit) {
			throw new InitializationException("");
		}

		super.validateBeforeUninitialization();
	}

	@Override
	protected void beforeUninitialization() throws RoboticsException {

		super.beforeUninitialization();

		if (uninitError) {
			throw new RoboticsException("");
		}
	}

}
