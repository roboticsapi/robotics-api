/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.world.Transformation;

public enum Dimension {
	X, Y, Z, A, B, C;

	public Transformation createTransformation(Dimension dim, double d) {
		return new Transformation(dim == X ? d : 0, dim == Y ? d : 0, dim == Z ? d : 0, dim == A ? d : 0,
				dim == B ? d : 0, dim == C ? d : 0);

	}
}
