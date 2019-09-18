/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;

public abstract class RpiExtension extends AbstractRoboticsObjectBuilder implements RoboticsObjectListener {

	public RpiExtension(Class<?>... types) {
		super(types);
	}

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof RpiRuntime) {
			registerMappers(((RpiRuntime) object).getMapperRegistry());
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		if (object instanceof RpiRuntime) {
			unregisterMappers(((RpiRuntime) object).getMapperRegistry());
		}
	}

	protected abstract void registerMappers(MapperRegistry mr);

	protected abstract void unregisterMappers(MapperRegistry mr);

}
