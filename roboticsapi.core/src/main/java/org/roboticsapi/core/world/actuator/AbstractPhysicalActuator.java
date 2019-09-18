/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.actuator;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;

public abstract class AbstractPhysicalActuator<DD extends ActuatorDriver> extends AbstractActuator<DD>
		implements PhysicalObject {

	private Dependency<Frame> base;

	public AbstractPhysicalActuator() {
		base = createDependency("base", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Base");
			}
		});
	}

	@Override
	public final Frame getBase() {
		return base.get();
	}

}
