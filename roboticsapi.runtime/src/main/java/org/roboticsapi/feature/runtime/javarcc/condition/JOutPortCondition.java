/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc.condition;

import org.roboticsapi.facet.javarcc.JCondition;
import org.roboticsapi.facet.javarcc.JFragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public class JOutPortCondition implements JCondition {

	private JFragmentOutPort<RPIbool> port;

	public JOutPortCondition(JFragmentOutPort<RPIbool> port) {
		this.port = port;
	}

	@Override
	public Boolean isTrue() {
		return port.getInnerPort().get() == null ? null : port.getInnerPort().get().get();
	}

	@Override
	public boolean isFinal() {
		return port.getPrimitive().getNet().isCompleted() || port.getPrimitive().getNet().isKilled();
	}

}
