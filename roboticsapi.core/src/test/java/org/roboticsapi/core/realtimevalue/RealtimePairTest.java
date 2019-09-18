/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class RealtimePairTest {

	private boolean threadFailed = false;
	private boolean threadSucceeded = false;

	@Test
	public void testRealtimePair() throws RoboticsException {
		RealtimePair<Double, Double> pair = new RealtimePair<>(RealtimeDouble.ONE, RealtimeDouble.ZERO);
		Pair<Double, Double> value = pair.getCurrentValue();
		Assert.assertEquals(value.getFirst(), (Double) 1.0);
		Assert.assertEquals(value.getSecond(), (Double) 0.0);
	}

	@Test
	public void testRealtimePairListener() throws RoboticsException {
		RealtimePair<Double, Double> pair = new RealtimePair<>(RealtimeDouble.ONE, RealtimeDouble.ZERO);
		pair.addListener(new RealtimeValueListener<Pair<Double, Double>>() {
			@Override
			public void onValueChanged(Pair<Double, Double> newValue) {
				if (newValue.getFirst() != 1 || newValue.getSecond() != 0) {
					threadFailed = true;
				} else {
					threadSucceeded = true;
				}
			}
		});

		Assert.assertFalse(threadFailed);
		Assert.assertTrue(threadSucceeded);
	}
}
