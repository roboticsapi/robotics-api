/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.runtime.core.primitives.BooleanConditional;
import org.roboticsapi.runtime.core.primitives.BooleanIsNull;
import org.roboticsapi.runtime.core.primitives.BooleanPre;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

/**
 * Fragment for constantly holding an initially read boolean value.
 */
public class BooleanHoldFragment extends NetFragment {

	private final DataflowOutPort outValue;

	public BooleanHoldFragment(DataflowOutPort value) throws MappingException {
		super("Boolean Hold");

		final BooleanConditional cond = add(new BooleanConditional());
		final BooleanPre pre = add(new BooleanPre());
		final BooleanIsNull isNull = add(new BooleanIsNull());

		connect(value, cond.getInTrue(), new StateDataflow());
		connect(pre.getOutValue(), cond.getInFalse());
		connect(pre.getOutValue(), isNull.getInValue());
		connect(isNull.getOutValue(), cond.getInCondition());
		connect(cond.getOutValue(), pre.getInValue());

		outValue = addOutPort(new StateDataflow(), true, cond.getOutValue());
	}

	public DataflowOutPort getOutValue() {
		return outValue;
	}

}
