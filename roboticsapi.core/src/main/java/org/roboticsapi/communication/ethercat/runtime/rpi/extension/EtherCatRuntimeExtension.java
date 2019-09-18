/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.communication.ethercat.runtime.rpi.extension;

import org.roboticsapi.communication.ethercat.runtime.rpi.driver.EtherCatGenericDriver;
import org.roboticsapi.communication.ethercat.runtime.rpi.driver.EtherCatSoemDriver;
import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;

public final class EtherCatRuntimeExtension extends RpiExtension {

	public EtherCatRuntimeExtension() {
		super(EtherCatSoemDriver.class, EtherCatGenericDriver.class);
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
