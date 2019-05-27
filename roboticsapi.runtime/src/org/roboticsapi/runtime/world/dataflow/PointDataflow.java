/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.world.Frame;

public class PointDataflow extends VectorDataflow {

	private final Frame reference;

	public PointDataflow(Frame reference) {
		this.reference = reference;
	}

	public Frame getReference() {
		return reference;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}

		if (other instanceof PointDataflow) {
			PointDataflow o = (PointDataflow) other;

			if (o != null && getReference() != null && o.getReference() != null && getReference() != o.getReference()) {
				return false;
			}

		}

		return true;
	}

	@Override
	public String toString() {
		return "VectorDataflow<" + (getReference() != null ? getReference().getName() : "null") + ">";
	}

}
