/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

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
	 * @throws RPIException if an error occurs
	 */
	public NetHandle load(Fragment net, String description) throws RPIException;

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
	void ping() throws RPIException;

	/**
	 * Checks if the current thread is a event handler thread and thus may not be
	 * blocked
	 * 
	 * @throws RPIException if the current thread is an event handler thread
	 */
	void checkBlockEventHandlerThread() throws RPIException;

	/**
	 * Evaluates a fragment (just one cycle)
	 * 
	 * @param fragment net to evaluate
	 * @return map containing the out ports and netcomm values
	 * @throws RPIException if an error occurs
	 */
	Map<String, String> eval(Fragment fragment) throws RPIException;

	public List<String> getDeviceInterfaces(String deviceName);

	public boolean createDevice(String deviceName, String deviceType, Map<String, String> parameters);

	public boolean deleteDevice(String deviceName);

	public boolean loadExtension(String extensionId);
}
