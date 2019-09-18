/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc.condition;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.facet.javarcc.JCondition;

public class JOrCondition implements JCondition {
	List<JCondition> conditions = new ArrayList<JCondition>();

	@Override
	public String toString() {
		return "or(" + conditions + ")";
	}

	public JOrCondition(JCondition... conditions) {
		for (JCondition condition : conditions)
			this.conditions.add(condition);
	}

	public JOrCondition(List<JCondition> conditions) {
		for (JCondition condition : conditions)
			this.conditions.add(condition);
	}

	@Override
	public Boolean isTrue() {
		boolean anyTrue = false, anyFalse = false, anyNull = false;
		for (JCondition condition : conditions)
			if (condition.isTrue() == null)
				anyNull = true;
			else if (condition.isTrue() == true)
				anyTrue = true;
			else if (condition.isTrue() == false)
				anyFalse = true;
		if (anyTrue)
			return true;
		else if (anyNull)
			return null;
		else if (anyFalse)
			return false;
		else
			return null;
	}

	@Override
	public boolean isFinal() {
		for (JCondition cond : conditions) {
			if (cond.isTrue() == true && cond.isFinal())
				return true;
		}
		for (JCondition cond : conditions) {
			if (!cond.isFinal())
				return false;
		}
		return true;
	}

}
