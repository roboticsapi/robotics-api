/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

/**
 * An RPI output port
 */
public class OutPort {

	/** name of the port */
	protected String name;

	/** primitive this port belongs to */
	private Primitive primitive;

	/**
	 * Adds the port to a primitive
	 * 
	 * @param primitive primitive to add in port to
	 */
	public void addToPrimitive(final Primitive primitive) {
		this.primitive = primitive;
	}

	/**
	 * Retrieves the owning primitive's name
	 * 
	 * @return primitive name, or null if no primitive is set
	 */
	public String getPrimitiveName() {
		if (primitive == null) {
			return null;
		}
		return primitive.getName();
	}

	/**
	 * Retrieves the owning primitive
	 * 
	 * @return the owning primitive
	 */
	public Primitive getPrimitive() {
		return primitive;
	}

	/**
	 * Creates a new out port
	 * 
	 * @param name name of the port
	 */
	public OutPort(final String name) {
		this.name = name;
	}

	/**
	 * Retrieves the port's name
	 * 
	 * @return name of the port
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getPrimitiveName() + ">" + getName();
	}

}
