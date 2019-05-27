/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.EventHandlerMapperResult;

public class SimpleEventHandlerMapperResult implements EventHandlerMapperResult {
	private final NetFragment fragment;

	public SimpleEventHandlerMapperResult(NetFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public NetFragment getNetFragment() {
		return fragment;
	}

}
