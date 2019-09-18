/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.facet.runtime.rpi.Primitive;

public class ObserverFragment extends RealtimeValueConsumerFragment {

	public ObserverFragment(Primitive... primitives) {
		super(primitives);
	}

}
