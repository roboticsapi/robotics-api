/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

import java.util.ArrayList;
import java.util.List;

/**
 * An RPI primitive
 */
public class Primitive {
	/** name of the primitive */
	private String name;

	/** parameters of the primitive */
	protected List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

	/** in ports of the primitive */
	protected List<InPort> inPorts = new ArrayList<InPort>();

	/** out ports of the primitive */
	protected List<OutPort> outPorts = new ArrayList<OutPort>();

	/** type of the primitive */
	protected String type;

	/**
	 * Creates a new primitive of a given type
	 * 
	 * @param type type of primitive
	 */
	protected Primitive(final String type) {
		this.type = type;
	}

	/**
	 * Adds in ports to the primitive
	 * 
	 * @param ins in ports to add
	 */
	protected void add(final InPort... ins) {
		for (final InPort in : ins) {
			inPorts.add(in);
			in.addToPrimitive(this);
		}
	}

	/**
	 * Adds out ports to the primitive
	 * 
	 * @param outs out ports to add
	 */
	protected void add(final OutPort... outs) {
		for (final OutPort out : outs) {
			outPorts.add(out);
			out.addToPrimitive(this);
		}
	}

	/**
	 * Adds parameters to the primitive
	 * 
	 * @param params parameters to add
	 */
	protected void add(final Parameter<?>... params) {
		for (final Parameter<?> param : params) {
			parameters.add(param);
		}
	}

	/**
	 * Retrieves the input ports
	 * 
	 * @return input ports
	 */
	public List<InPort> getInPorts() {
		return inPorts;
	}

	/**
	 * Retrieves the output ports
	 * 
	 * @return output ports
	 */
	public List<OutPort> getOutPorts() {
		return outPorts;
	}

	/**
	 * Retrieves the parameters
	 * 
	 * @return parameters
	 */
	public List<Parameter<?>> getParameters() {
		return parameters;
	}

	/**
	 * Retrieves the primitive type
	 * 
	 * @return primitive type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the primitive name
	 * 
	 * @param name primitive name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Retrieves the primitive name
	 * 
	 * @return primitive name
	 */
	public String getName() {
		return name;
	}
}
