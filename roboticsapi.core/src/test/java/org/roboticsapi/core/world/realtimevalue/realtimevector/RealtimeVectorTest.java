/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Vector;

public class RealtimeVectorTest {

	@Test
	public void testIsNullHasExpectedStructure() {
		RealtimeVector fromValue = RealtimeVector.createFromConstant(new Vector());
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeVectorIsNull);

		Assert.assertEquals(fromValue, ((RealtimeVectorIsNull) createIsNull).getOther());
	}
}
