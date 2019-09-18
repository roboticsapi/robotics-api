/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public final class EqualRealtimeInteger extends RealtimeBoolean {

	private final RealtimeInteger first;
	private final RealtimeInteger second;

	EqualRealtimeInteger(RealtimeInteger first, RealtimeInteger second) {
		super(first, second);
		this.first = first;
		this.second = second;
	}

	public RealtimeInteger getFirst() {
		return first;
	}

	public RealtimeInteger getSecond() {
		return second;
	}

	@Override
	protected Boolean calculateCheapValue() {
		Integer firstValue = first.getCheapValue();
		Integer secondValue = second.getCheapValue();
		if (firstValue == null || secondValue == null) {
			return null;
		}
		return firstValue == secondValue;
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(getFirst(), getSecond());
	}

}
