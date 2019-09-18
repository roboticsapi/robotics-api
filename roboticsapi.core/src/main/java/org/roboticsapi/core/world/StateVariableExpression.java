/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.List;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * An expression defined on a set of {@link StateVariable}s
 */
public interface StateVariableExpression {

	/**
	 * Provides the {@link StateVariable} used in the expression
	 *
	 * @return List of {@link StateVariable}s
	 */
	List<StateVariable> getUsedVariables();

	/**
	 * Provides the computation rule for this expression, assuming the given
	 * {@link StateVariable} valuation
	 *
	 * @param variables values for the used {@link StateVariable}s
	 * @return {@link RealtimeDouble} describing the value of the expression
	 */
	RealtimeDouble getRealtimeValue(Map<StateVariable, RealtimeDouble> variables);

}
