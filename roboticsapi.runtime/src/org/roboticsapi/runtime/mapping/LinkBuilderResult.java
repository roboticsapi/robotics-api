/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;

/**
 * The result of a link builder (net fragment converting from input to result
 * data type)
 */
public class LinkBuilderResult {
	final private NetFragment fragment;
	final private DataflowOutPort result;
	final private DataflowInPort input;

	/**
	 * Creates a new Link builder result
	 * 
	 * @param fragment net fragment for the link builder
	 * @param input    input port
	 * @param result   result port
	 */
	public LinkBuilderResult(NetFragment fragment, DataflowInPort input, DataflowOutPort result) {
		this.fragment = fragment;
		this.input = input;
		this.result = result;
	}

	/**
	 * Retrieves the net fragment for the link
	 */
	public NetFragment getNetFragment() {
		return fragment;
	}

	/**
	 * Retrieves the result port of the link
	 */
	public DataflowOutPort getResultPort() {
		return result;
	}

	/**
	 * Retrieves the input port of the link
	 */
	public DataflowInPort getInputPort() {
		return input;
	}
}
