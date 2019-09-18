/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class DoubleStateVariableExpression implements StateVariableExpression {
	public static final DoubleStateVariableExpression ZERO = new DoubleStateVariableExpression(0);
	public static final DoubleStateVariableExpression ONE = new DoubleStateVariableExpression(1);

	private final RealtimeDouble value;

	public DoubleStateVariableExpression(RealtimeDouble value) {
		this.value = value;
	}

	public DoubleStateVariableExpression(double value) {
		this(RealtimeDouble.createFromConstant(value));
	}

	@Override
	public List<StateVariable> getUsedVariables() {
		return new ArrayList<>();
	}

	@Override
	public RealtimeDouble getRealtimeValue(Map<StateVariable, RealtimeDouble> variables) {
		return value;
	}

}
