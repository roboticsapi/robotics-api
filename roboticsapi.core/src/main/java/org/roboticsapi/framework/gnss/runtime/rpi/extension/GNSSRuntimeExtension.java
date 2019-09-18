/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.runtime.rpi.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.gnss.runtime.rpi.GNSSGenericDriver;

public class GNSSRuntimeExtension extends RpiExtension {

	public GNSSRuntimeExtension() {
		super(GNSSGenericDriver.class);
	}

	@Override
	protected void registerMappers(MapperRegistry mr) {
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
	}

}
