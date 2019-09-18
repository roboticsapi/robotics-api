/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import org.roboticsapi.core.world.AbstractPhysicalObject;

/**
 * Abstract implementation for a {@link Wheel}.
 */
public abstract class AbstractWheel extends AbstractPhysicalObject implements Wheel {

	/**
	 * Constructor.
	 */
	protected AbstractWheel() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param name the wheel's name
	 */
	protected AbstractWheel(String name) {
		this();
		setName(name);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}

}
