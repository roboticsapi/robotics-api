/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import org.roboticsapi.runtime.mapping.net.DataflowThroughInPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class SoftRobotCommandPorts extends NetFragment {
	public SoftRobotCommandPorts() {
		super("Early mapping command ports");
		outStart = new DataflowThroughOutPort(true, inStart = new DataflowThroughInPort(new EventDataflow()));
		outStop = new DataflowThroughOutPort(true, inStop = new DataflowThroughInPort(new EventDataflow()));
		outCancel = new DataflowThroughOutPort(true, inCancel = new DataflowThroughInPort(new StateDataflow()));
		outOverride = new DataflowThroughOutPort(true, inOverride = new DataflowThroughInPort(new DoubleDataflow()));
		addInPort(inStart);
		addOutPort(outStart);
		addInPort(inStop);
		addOutPort(outStop);
		addInPort(inCancel);
		addOutPort(outCancel);
		addInPort(inOverride);
		addOutPort(outOverride);
	}

	public DataflowThroughOutPort outStart;
	public DataflowThroughInPort inStart;

	public DataflowThroughOutPort outStop;
	public DataflowThroughInPort inStop;

	public DataflowThroughOutPort outCancel;
	public DataflowThroughInPort inCancel;

	public DataflowThroughOutPort outOverride;
	public DataflowThroughInPort inOverride;
}