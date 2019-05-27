/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.device;

import java.util.Map;

import javax.tools.Tool;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractDevice;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;

public abstract class AbstractPhysicalDevice<DD extends DeviceDriver> extends AbstractDevice<DD>
		implements PhysicalObject {

	private Frame base;

	@Override
	public Frame getBase() {
		return base;
	}

	@Override
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.base = base;
	}

	@Override
	protected final void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("base", base);
		checkNoParent("base", base);

		validateDeviceConfigurationProperties();
	}

	protected void validateDeviceConfigurationProperties() throws ConfigurationException {

	}

	@Override
	protected final void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		base = fill("base", base, new Frame(getName() + " Base"), createdObjects);

		fillAutomaticDeviceProperties(createdObjects);
	}

	/**
	 * Fills automatic configuration properties of derived {@link Device}s.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #fillAutomaticConfigurationProperties(Map)
	 */
	protected void fillAutomaticDeviceProperties(Map<String, RoboticsObject> createdObjects) {
	}

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		clearAutomaticDeviceProperties(createdObjects);

		base = clear("base", base, createdObjects);
	}

	/**
	 * Clears automatic configuration properties of derived {@link Tool}s.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #clearAutomaticConfigurationProperties(Map)
	 */
	protected void clearAutomaticDeviceProperties(Map<String, RoboticsObject> createdObjects) {
	}

}
