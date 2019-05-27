/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

/**
 * A data flow port that does not belong to RPI ports, but is directly forwarded
 * to an output port
 */
public class DataflowThroughInPort extends DataflowInPort {
	@Override
	public void connectInNet() {
		// nothing to do here
		return;
	}

	/**
	 * Creates a new data flow through port
	 * 
	 * @param type type of the data flow through port
	 */
	public DataflowThroughInPort(final DataflowType type) {
		super(type);
		ports = null;
	}

	/**
	 * Creates a new data flow through port
	 * 
	 * @param required data flow is required for operation of the primitive
	 * @param type     type of the data flow through port
	 */
	public DataflowThroughInPort(final boolean required, final DataflowType type) {
		super(type);
		setRequired(required);
		ports = null;
	}
}
