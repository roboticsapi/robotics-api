/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.fieldbus.ethercat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.driver.GenericLoadable;

public final class EthercatDriver extends AbstractSoftRobotDeviceDriver implements GenericLoadable {

	private String ethernetDevice;
	private int timeout = 50, mailboxTimeout = 4000;

	public EthercatDriver() {
		super();
	}

	public final String getEthernetDevice() {
		return ethernetDevice;
	}

	@ConfigurationProperty(Optional = false)
	public final void setEthernetDevice(String ethernetdevice) {
		immutableWhenInitialized();
		this.ethernetDevice = ethernetdevice;
	}

	public int getTimeout() {
		return timeout;
	}

	@ConfigurationProperty(Optional = true)
	public void setTimeout(int timeout) {
		immutableWhenInitialized();
		this.timeout = timeout;
	}

	public int getMailboxTimeout() {
		return mailboxTimeout;
	}

	@ConfigurationProperty(Optional = true)
	public void setMailboxTimeout(int mailboxTimeout) {
		immutableWhenInitialized();
		this.mailboxTimeout = mailboxTimeout;
	}

	@Override
	public String getDeviceType() {
		return "ethercat";
	}

	@Override
	public final boolean build() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("ethernetdevice", getEthernetDevice());
		parameters.put("ethercattimeout", String.valueOf(getTimeout()));
		parameters.put("mailboxtimeout", String.valueOf(getMailboxTimeout()));

		return loadDeviceDriver(parameters);
	}

	@Override
	public final void delete() {
		deleteDeviceDriver();
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return true; // TODO: No interfaces available until now
	}

}
