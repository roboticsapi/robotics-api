/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * An Activity affects certain {@link Device}s and lets them perform some
 * actions.
 */
public interface Activity {

	/**
	 * Gets this {@link Activity}'s name.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Adds a listener that is notified about this {@link Activity}'s status.
	 * 
	 * @param listener the listener
	 */
	public abstract void addStatusListener(ActivityStatusListener listener);

	/**
	 * Removes an ActivityStatusListener.
	 * 
	 * @param listener the listener
	 */
	public abstract void removeStatusListener(ActivityStatusListener listener);

	/**
	 * Asynchronously starts execution of this {@link Activity}. This method returns
	 * as soon as execution is started completely, i.e. the Activity is running.
	 * 
	 * @return this Activity
	 * @throws RoboticsException if an error occurred during starting the Activity
	 */
	public abstract Activity beginExecute() throws RoboticsException;

	/**
	 * Starts execution of this {@link Activity} and waits until execution
	 * completes.
	 * 
	 * Execution is considered complete when either {@link ActivityStatus}
	 * MAINTAINING or COMPLETED has been entered.
	 * 
	 * @throws RoboticsException if an error occurred during execution of the
	 *                           Activity
	 */
	public abstract void execute() throws RoboticsException;

	/**
	 * Specifies that an additional {@link Device} is affected by this
	 * {@link Activity}.
	 * 
	 * @param devices the device that is affected
	 */
	public abstract void addAdditionalAffectedDevice(Device device);

	/**
	 * Specifies that additional {@link Device}s are affected by this
	 * {@link Activity}.
	 * 
	 * @param devices the devices that are affected
	 */
	public abstract void addAdditionalAffectedDevices(List<Device> devices);

	/**
	 * Gets all {@link Device}s that are affected by this {@link Activity}. A
	 * {@link Device} is called 'affected' by a {@link Activity} if it is not
	 * allowed to be involved in the execution of any other {@link Activity} while
	 * the first {@link Activity} is running.
	 * 
	 * @return the affected devices
	 */
	public abstract List<Device> getAffectedDevices();

	/**
	 * Gets the {@link Device}s that are controlled by this {@link Activity}, i.e.
	 * those {@link Device}s that perform actions during execution of the
	 * {@link Activity}.
	 * 
	 * @return the controlled devices
	 */
	public abstract List<Device> getControlledDevices();

	/**
	 * Gets the status of this {@link Activity}.
	 * 
	 * @return the status
	 */
	public abstract ActivityStatus getStatus();

	/**
	 * Waits until this {@link Activity} has completed its execution.
	 * 
	 * This method will wait until either {@link ActivityStatus} MAINTAINING or
	 * COMPLETED has been entered.
	 * 
	 * @throws RoboticsException if any errors occurred during execution (thrown
	 *                           only once)
	 */
	public abstract void endExecute() throws RoboticsException;

	/**
	 * Cancels the execution of this {@link Activity}.
	 * 
	 * @throws RoboticsException if any errors occurred during execution.
	 */
	public abstract void cancelExecute() throws RoboticsException;

	/**
	 * Gets an {@link ActivityProperty} of a certain type regarding a certain
	 * {@link Device} affected by this {@link Activity}, if such exists.
	 * 
	 * @param <T>      the type of {@link ActivityProperty} to retrieve
	 * @param device   the {@link Device} that the property concerns
	 * @param property the property type
	 * @return the requested property, if such exists
	 */
	public abstract <T extends ActivityProperty> T getProperty(Device device, Class<T> property);

	/**
	 * Gets a {@link Future} for an {@link ActivityProperty} of a certain type
	 * regarding a certain {@link Device} affected by this {@link Activity}.
	 * 
	 * @param <T>      the type of {@link ActivityProperty} to retrieve
	 * @param device   the {@link Device} that the property concerns
	 * @param property the property type
	 * @return the {@link Future} for the requested property
	 */
	public abstract <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property);

	/**
	 * Gets the {@link RoboticsException} that occurred during execution of this
	 * Activity, if any.
	 * 
	 * @return any ocurred exception
	 */
	public abstract RoboticsException getException();

	/**
	 * Prepares this {@link Activity} for execution. This method is not intended to
	 * be called by user code.
	 * 
	 * @param prevActivities the {@link Activity}s preceding this {@link Activity}
	 * @return Set of {@link Device}s this Activity is able to immediately take
	 *         control of
	 * @throws RoboticsException             if errors occurred during preparation
	 * @throws ActivityNotCompletedException if one or more {@link Activity}s
	 *                                       affecting one or more of this
	 *                                       {@link Activity}'s {@link Device}s have
	 *                                       not yet completed execution
	 */
	public abstract Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException;

}
