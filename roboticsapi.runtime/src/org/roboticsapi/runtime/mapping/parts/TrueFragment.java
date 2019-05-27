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

public class TrueFragment extends NetFragment {

	private DataflowOutPort trueOut;

	public TrueFragment() {
		this(null);
	}

	public TrueFragment(String fragmentName) {
		super(fragmentName != null ? fragmentName : "True");

		BooleanValue value = add(new BooleanValue(true));
		setTrueOut(addOutPort(new StateDataflow(), true, value.getOutValue()));
	}

	private void setTrueOut(DataflowOutPort trueOut) {
		this.trueOut = trueOut;
	}

	public DataflowOutPort getTrueOut() {
		return trueOut;
	}
}
