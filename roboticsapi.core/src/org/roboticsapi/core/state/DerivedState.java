/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import java.util.List;

import org.roboticsapi.core.State;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event composed of (multiple?) other states
 */
public abstract class DerivedState extends State {

	public DerivedState() {
		super();
	}

	public abstract List<State> getStates();

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), getStates().toArray());
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (getStates().size() != ((DerivedState) obj).getStates().size()) {
			return false;
		}
		for (int i = 0; i < getStates().size(); i++) {
			if (!getStates().get(i).equals(((DerivedState) obj).getStates().get(i))) {
				return false;
			}
		}
		return true;
	}

	protected String childrenToString() {
		List<State> states = getStates();
		if (states.size() == 0) {
			return "";
		}
		StringBuffer ret = new StringBuffer();
		for (State child : states) {
			ret.append(child).append(",");
		}
		return ret.substring(0, ret.length() - 1);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + childrenToString() + ">";
	}
}