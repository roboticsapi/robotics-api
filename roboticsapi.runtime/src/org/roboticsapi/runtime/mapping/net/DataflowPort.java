/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

/**
 * A high-level data flow port
 */
public abstract class DataflowPort {

	/** type of the data flow */
	private DataflowType type;

	/** this port has to be connected */
	private boolean required;

	private NetFragment parentFragment;

	public NetFragment getParentFragment() {
		return parentFragment;
	}

	void setParentFragment(NetFragment parentFragment) {
		this.parentFragment = parentFragment;
	}

	/**
	 * Retrieves the type of the data flow port
	 * 
	 * @return type of the port
	 */
	public DataflowType getType() {
		return type;
	}

	/**
	 * Retrieves whether the port has to be connected
	 * 
	 * @return true if the port is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the type of the data flow port
	 * 
	 * @param type type of the port
	 */
	protected void setType(final DataflowType type) {
		this.type = type;
	}

	/**
	 * Sets whether the port has to be connected
	 * 
	 * @param required true if the port is required
	 */
	protected void setRequired(final boolean required) {
		this.required = required;
	}
}
