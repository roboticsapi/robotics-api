/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.driver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.OnlineObject.OperationStateListener;
import org.roboticsapi.core.util.RAPILogger;

public final class DeviceBasedInstantiator<T extends Device> implements OperationStateListener {

	final private T device;
	final private DeviceBasedLoadable<T> driver;
	final private boolean neverDelete;
	private boolean canBuild = false;
	private boolean wasBuilt = false;
	private final Map<OnlineObject, Boolean> presentObjects = new HashMap<OnlineObject, Boolean>();

	public DeviceBasedInstantiator(T device, DeviceBasedLoadable<T> driver) {
		this(device, driver, false);
	}

	public DeviceBasedInstantiator(T device, DeviceBasedLoadable<T> driver, boolean neverDelete) {
		super();

		this.device = device;
		this.driver = driver;
		this.neverDelete = neverDelete;
	}

	@Override
	public void operationStateChanged(OnlineObject object, OperationState newState) {
		RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL, object + " new OperationState " + newState);

		if (newState == OperationState.ABSENT) {
			canBuild = true;

			if (driver.checkDependentObjects()) {
				RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL, driver.toString() + " checked dependent objects: "
						+ Arrays.toString(driver.getDependentObjects().toArray()) + ", will build now");
				wasBuilt = driver.build(device);
			} else {
				RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL, driver.toString() + " checked dependent objects: "
						+ Arrays.toString(driver.getDependentObjects().toArray()) + ", will await presence");
				awaitPresent(driver.getDependentObjects());
			}

		}

		if (newState == OperationState.NEW) {
			canBuild = false;
			if (wasBuilt && !neverDelete) {
				driver.delete();
			}
		}
	}

	private void awaitPresent(List<OnlineObject> dependentObjects) {
		for (OnlineObject o : dependentObjects) {

			presentObjects.put(o, o.isPresent());

			o.addOperationStateListener(new OperationStateListener() {

				@Override
				public void operationStateChanged(OnlineObject object, OperationState newState) {
					if (object.isPresent()) {
						Boolean wasPresent = presentObjects.get(object);
						presentObjects.put(object, Boolean.TRUE);

						if (!wasPresent) {
							tryBuild();
						}
					} else {
						presentObjects.put(object, Boolean.FALSE);
					}

				}
			});
		}

	}

	private void tryBuild() {
		Map<OnlineObject, Boolean> presentObjectsCopy = new HashMap<OnlineObject, Boolean>(presentObjects);
		for (OnlineObject o : presentObjectsCopy.keySet()) {
			if (!presentObjectsCopy.get(o)) {
				return;
			}
		}

		if (canBuild) {
			wasBuilt = driver.build(device);
		}
	}

	@Override
	public String toString() {
		return "DeviceBasedInstantiator[" + device.getClass().getSimpleName() + "," + driver.getClass().getSimpleName()
				+ "]";
	}

}
