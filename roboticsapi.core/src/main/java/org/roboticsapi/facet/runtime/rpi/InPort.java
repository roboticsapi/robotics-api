/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;

/**
 * An RPI input port
 */
public class InPort {

	/** primitive this port belongs to */
	private Primitive primitive;

	/** port this input port is connected to */
	private OutPort connectedSource = null;

	/** number of values to store for debugging purposes */
	private double debug = 0;

	/** name of the port */
	protected String name = "";

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
	 * @return primitive name
	 * @throws MappingException if the port does not belong to a primitive
	 */
	public String getPrimitiveName() {
		if (primitive == null) {
			return null;
		}
		return primitive.getName();
	}

	public String getPrimitiveType() {
		if (primitive == null) {
			return null;
		}
		return primitive.getType();
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
	 * Connects the in port to a port
	 * 
	 * @param source port to connect to
	 * @throws RpiException
	 */
	public void connectTo(final OutPort source) throws RpiException {
		if (connectedSource != null && connectedSource != source) {
			throw new RpiException("Port is already connected");
		}
		connectedSource = source;
	}

	public void disconnect() {
		connectedSource = null;
	}

	/**
	 * Retrieves the number of seconds to store values for debugging purposes.
	 * 
	 * @return number of seconds to store values for
	 */
	public double getDebug() {
		return debug;
	}

	/**
	 * Sets the number of seconds to store values for debugging purposes.
	 * 
	 * @param debug number of seconds to store values for
	 */
	public void setDebug(double debug) {
		this.debug = debug;
	}

	/**
	 * Retrieves the port this in port is connected to
	 * 
	 * @return connected port
	 */
	public OutPort getConnectedPort() {
		return connectedSource;
	}

	/**
	 * Creates a new in port
	 * 
	 * @param name name of the port
	 */
	public InPort(final String name) {
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
		return getName() + ">" + getPrimitiveName();
	}
}
