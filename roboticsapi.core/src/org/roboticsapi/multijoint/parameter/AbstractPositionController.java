/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.parameter;

import org.roboticsapi.core.util.HashCodeUtil;

public class AbstractPositionController implements PositionController {

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PositionController;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.SEED, getClass());
	}
}
