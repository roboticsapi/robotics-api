/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.mock;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeComparator;

public class MockRealtimeComparator<T> extends RealtimeComparator<T> {
	public MockRealtimeComparator(RealtimeValue<T> left, RealtimeValue<T> right) {
		super(left, right);
	}

	@Override
	protected Boolean computeCheapValue(T leftValue, T rightValue) {
		if ((leftValue == null) || (rightValue == null)) {
			throw new IllegalArgumentException("None of the values may be null.");
		}

		return leftValue.equals(rightValue);
	}
}
