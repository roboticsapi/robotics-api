/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.world.Orientation;

public class DirectionDataflow extends VectorDataflow {

	private final Orientation orientation;

	public DirectionDataflow(Orientation orientation) {
		this.orientation = orientation;

	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}

		if (other instanceof DirectionDataflow) {
			DirectionDataflow o = (DirectionDataflow) other;

			if (o.getOrientation() != null && !o.getOrientation().isEqualOrientation(getOrientation())) {
				return false;
			}
		}

		return true;
	}
}
