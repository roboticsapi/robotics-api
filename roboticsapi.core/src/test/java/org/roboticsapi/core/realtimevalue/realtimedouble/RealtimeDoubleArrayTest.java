/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class RealtimeDoubleArrayTest {

	@Test
	public void testIsNullHasExpectedStructure() {
		RealtimeDoubleArray fromValue = RealtimeDoubleArray.createFromConstants(new double[] { 1, 2 });
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeDoubleArrayIsNull);

		Assert.assertEquals(fromValue, ((RealtimeDoubleArrayIsNull) createIsNull).getOther());

	}
}
