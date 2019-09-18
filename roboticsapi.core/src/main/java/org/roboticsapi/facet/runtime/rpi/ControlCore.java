/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.List;
import java.util.Map;

/**
 * Interface for a control core
 */
public interface ControlCore {
	/**
	 * Executes the RPI net
	 *
	 * @param net         net to execute
	 * @param description net description
	 * @return net handle to control execution
	 * @throws RpiException if an error occurs
	 */
	public NetHandle load(Fragment net, String description, boolean realtime) throws RpiException;

	/**
	 * Retrieves a list of net handles for all nets on the control core
	 *
	 * @return list of net handles on the control core
	 */
	List<NetHandle> getNetHandles();

	/**
	 * Close connection to control core
	 */
	void shutdown();

	/**
	 * Send a keepalive (ping) message
	 */
	void ping() throws RpiException;

	/**
	 * Checks if the current thread is a event handler thread and thus may not be
	 * blocked
	 *
	 * @throws RpiException if the current thread is an event handler thread
	 */
	void checkBlockEventHandlerThread() throws RpiException;

	/**
	 * Evaluates a fragment (just one cycle)
	 *
	 * @param fragment net to evaluate
	 * @return map containing the out ports and netcomm values
	 * @throws RpiException if an error occurs
	 */
	Map<String, String> eval(Fragment fragment) throws RpiException;

	/**
	 * Starts the given net handles
	 *
	 * @param handles net handles to start
	 * @return true if successful
	 */
	boolean start(List<NetHandle> handles) throws RpiException;

	/**
	 * Schedules the given net handles for the given results
	 *
	 * @param results    net results to start the given nets at
	 * @param stopNets   net handles to stop
	 * @param cancelNets net handles to cancel
	 * @param startNets  net handles to start
	 * @return a NetSynchronizationRule informing about the status of the rule, or
	 *         null if failed
	 */
	NetSynchronizationRule schedule(List<NetResult> results, List<NetHandle> stopNets, List<NetHandle> cancelNets,
			List<NetHandle> startNets) throws RpiException;

	/**
	 * Schedules the given net handles for the given results
	 *
	 * @param results    net results to start the given nets at
	 * @param stopNets   net handles to stop
	 * @param cancelNets net handles to cancel
	 * @param startNets  net handles to start
	 * @param listener   listener to be notified about rule execution
	 */
	void schedule(List<NetResult> results, List<NetHandle> stopNets, List<NetHandle> cancelNets,
			List<NetHandle> startNets, NetSynchronizationRule.SynchronizationRuleListener listener) throws RpiException;

	/**
	 * Adds a device listener that is informed about devices on this Control Core
	 *
	 * @param listener device listener to register
	 */
	void addDeviceListener(DeviceListener listener);

	/**
	 * Removes a previously registered device listener
	 *
	 * @param listener device listener to remove
	 */
	void removeDeviceListener(DeviceListener listener);

	/**
	 * Requests the creation of a device on the control core
	 *
	 * @param name       unique name of the device
	 * @param type       type of the device
	 * @param parameters parameters for device creation
	 */
	void createDevice(String name, String type, RpiParameters parameters);

	/**
	 * Requests the removal of a previously created device
	 * 
	 * @param name unique name of the device
	 */
	void removeDevice(String name);

}
