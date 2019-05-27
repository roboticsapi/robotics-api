/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class FalseFragment extends NetFragment {

	private DataflowOutPort falseOut;

	public FalseFragment() {
		this(null);
	}

	public FalseFragment(String fragmentName) {
		super(fragmentName != null ? fragmentName : "False");

		BooleanValue value = add(new BooleanValue(false));
		setFalseOut(addOutPort(new StateDataflow(), true, value.getOutValue()));
	}

	private void setFalseOut(DataflowOutPort falseOut) {
		this.falseOut = falseOut;
	}

	public DataflowOutPort getFalseOut() {
		return falseOut;
	}
}
