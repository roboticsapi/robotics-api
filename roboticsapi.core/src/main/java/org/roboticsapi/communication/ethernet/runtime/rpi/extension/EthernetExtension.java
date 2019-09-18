/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.communication.ethernet.runtime.rpi.extension;

import org.roboticsapi.communication.ethernet.runtime.rpi.driver.EthernetRealtimeDriver;
import org.roboticsapi.communication.ethernet.runtime.rpi.driver.EthernetStandardDriver;
import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;

public final class EthernetExtension extends RpiExtension {

	public EthernetExtension() {
		super(EthernetRealtimeDriver.class, EthernetStandardDriver.class);
	}

	@Override
	protected void registerMappers(MapperRegistry mr) {
		// none

	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// none

	}
}
