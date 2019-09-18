/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.communication.ethernet.runtime.rpi.driver;

public final class EthernetRealtimeDriver extends EthernetGenericDriver {

	@Override
	public String getRpiDeviceType() {
		return "ethernet_udp_rtnet";
	}
}
