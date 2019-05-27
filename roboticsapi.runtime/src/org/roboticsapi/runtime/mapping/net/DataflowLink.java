/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import org.roboticsapi.runtime.mapping.MappingException;

/**
 * A link between two data flow ports (usually of not directly compatible type)
 */
public class DataflowLink {

	/** source of the link */
	private DataflowOutPort from;

	/** destination of the link */
	private DataflowInPort to;

	private MappingException incompatibleTypeException;

	/**
	 * Creates a new data flow link
	 * 
	 * @param from source of the link
	 * @param to   destination of the link
	 */
	public DataflowLink(final DataflowOutPort from, final DataflowInPort to) {
		this.from = from;
		this.to = to;
		incompatibleTypeException = new MappingException("Incompatible types: "
				+ (from == null ? "null" : from.getType()) + " -> " + (to == null ? "null" : to.getType()));
	}

	public MappingException getIncompatibleTypeException() {
		return incompatibleTypeException;
	}

	/**
	 * Creates a new, empty data flow link
	 */
	public DataflowLink() {
	}

	/**
	 * Sets the source of the data flow link
	 * 
	 * @param from souce of the link
	 */
	public void setFrom(final DataflowOutPort from) {
		this.from = from;
	}

	/**
	 * Retrieves the source of the data flow link
	 * 
	 * @return source of the link
	 */
	public DataflowOutPort getFrom() {
		return from;
	}

	/**
	 * Sets the destination of the data flow link
	 * 
	 * @param to destination of the link
	 */
	public void setTo(final DataflowInPort to) {
		this.to = to;
	}

	/**
	 * Retrieves the destination of the data flow link
	 * 
	 * @return destination of the link
	 */
	public DataflowInPort getTo() {
		return to;
	}

	@Override
	public String toString() {
		return from + " -> " + to;
	}
}
