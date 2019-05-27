/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleIsNull;
import org.roboticsapi.runtime.core.primitives.DoublePre;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;

/**
 * Fragment for constantly holding an initially read double value.
 */
public class DoubleHoldFragment extends NetFragment {

	private final DataflowOutPort outValue;

	public DoubleHoldFragment(DataflowOutPort value) throws MappingException {
		super("Double Hold");

		final DoubleConditional cond = add(new DoubleConditional());
		final DoublePre pre = add(new DoublePre());
		final DoubleIsNull isNull = add(new DoubleIsNull());

		connect(value, cond.getInTrue(), new DoubleDataflow());
		connect(pre.getOutValue(), cond.getInFalse());
		connect(pre.getOutValue(), isNull.getInValue());
		connect(isNull.getOutValue(), cond.getInCondition());
		connect(cond.getOutValue(), pre.getInValue());

		outValue = addOutPort(new DoubleDataflow(), true, cond.getOutValue());
	}

	public DataflowOutPort getOutValue() {
		return outValue;
	}

}
