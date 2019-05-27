/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.Sensor;

public class Assignment<T> {

	private final Sensor<T> source;
	private final PersistContext<T> target;

	public Assignment(Sensor<T> source, PersistContext<T> target) {
		this.source = source;
		this.target = target;
	}

	public Sensor<T> getSource() {
		return source;
	}

	public PersistContext<T> getTarget() {
		return target;
	}

}
