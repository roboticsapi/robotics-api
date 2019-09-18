/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractStateVariableExpression implements StateVariableExpression {
	private final List<StateVariable> variables;

	public AbstractStateVariableExpression(StateVariable... variables) {
		this.variables = Arrays.asList(variables);
	}

	@Override
	public List<StateVariable> getUsedVariables() {
		return variables;
	}

}
