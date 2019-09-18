/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc.condition;

import org.roboticsapi.facet.javarcc.JCondition;

public class JTrueCondition implements JCondition {

	@Override
	public String toString() {
		return "tt";
	}

	@Override
	public Boolean isTrue() {
		return true;
	}

	@Override
	public boolean isFinal() {
		return true;
	}

}
