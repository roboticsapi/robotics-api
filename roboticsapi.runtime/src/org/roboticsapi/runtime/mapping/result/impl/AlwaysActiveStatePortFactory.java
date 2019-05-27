/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.State;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.parts.TrueFragment;

public class AlwaysActiveStatePortFactory<T extends State> extends AbstractStateSinglePortFactory<T> {

	private DataflowOutPort trueOut;

	@Override
	public DataflowOutPort createTypedStateSinglePort(T state) throws MappingException {
		if (trueOut == null) {
			trueOut = getFragment().add(new TrueFragment()).getTrueOut();
		}
		return trueOut;
	}
}
