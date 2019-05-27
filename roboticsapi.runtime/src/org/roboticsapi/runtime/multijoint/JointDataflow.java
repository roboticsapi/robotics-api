/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint;

import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;

/**
 * A data flow transmitting a single joint value
 */
public class JointDataflow extends DoubleDataflow {

	/** the joint */
	private final JointDriver jointDriver;

	@Override
	public boolean providesValueFor(final DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (other instanceof JointDataflow) {
			final JointDataflow o = (JointDataflow) other;
			if (o.jointDriver != null && jointDriver != null && o.jointDriver != jointDriver) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates a new joint data flow
	 * 
	 * @param jointDriver joint driver for this dataflow
	 */
	public JointDataflow(final JointDriver jointDriver) {
		this.jointDriver = jointDriver;
	}

	public JointDriver getJointDriver() {
		return jointDriver;
	}
}
