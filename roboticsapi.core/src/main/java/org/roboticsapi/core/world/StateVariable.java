/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * A variable used to describe the system state (as in the state space
 * representation of a system model)
 */
public class StateVariable {
	private final String name;
	private final List<StateVariable> dependentVariables = new ArrayList<>();
	private final List<StateVariableExpression> definitionExpressions = new ArrayList<>();
	private final List<StateVariableExpression> flowExpressions = new ArrayList<>();

	/**
	 * Creates a new state variable with the given name
	 *
	 * @param name human readable name of the variable
	 */
	public StateVariable(String name) {
		this.name = name;
	}

	/**
	 * Provides the human readable name of the state variable
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Provides the {@link StateVariable}s that depend on this {@link StateVariable}
	 *
	 * @return List of state variables
	 */
	public List<StateVariable> getDependentVariables() {
		return dependentVariables;
	}

	/**
	 * Provides the expressions defining this {@link StateVariable} (this = expr)
	 *
	 * @return List of expressions
	 */
	public List<StateVariableExpression> getDefinitionExpressions() {
		return definitionExpressions;
	}

	/**
	 * Provides expressions defining the change of this {@link StateVariable} (d/dt
	 * this = expr)
	 *
	 * @return List of expressions
	 */
	public List<StateVariableExpression> getFlowExpressions() {
		return flowExpressions;
	}

	/**
	 * Adds an expression defining this {@link StateVariable} (d/dt this =
	 * expression)
	 *
	 * @param expression expression used to define the {@link StateVariable}
	 */
	public void addFlowExpression(StateVariableExpression expression) {
		flowExpressions.add(expression);
		for (StateVariable v : expression.getUsedVariables()) {
			v.dependentVariables.add(this);
		}
	}

	/**
	 * Adds an expression defining the change of this {@link StateVariable} (d/dt
	 * this = expression)
	 *
	 * @param expression expression to define the change of this
	 *                   {@link StateVariable}
	 */
	public void addDefinitionExpressions(StateVariableExpression expression) {
		definitionExpressions.add(expression);
		for (StateVariable v : expression.getUsedVariables()) {
			v.dependentVariables.add(this);
		}
	}

	public StateVariableExpression asExpression() {
		return new StateVariableExpression() {
			@Override
			public List<StateVariable> getUsedVariables() {
				return Arrays.asList(StateVariable.this);
			}

			@Override
			public RealtimeDouble getRealtimeValue(Map<StateVariable, RealtimeDouble> variables) {
				return variables.get(StateVariable.this);
			}
		};
	}

}
