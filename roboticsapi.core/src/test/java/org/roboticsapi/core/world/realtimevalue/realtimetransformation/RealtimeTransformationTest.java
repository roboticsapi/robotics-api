/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class RealtimeTransformationTest {

	@Test
	public void testIsNullHasExpectedStructure() {
		RealtimeTransformation fromValue = RealtimeTransformation.createFromConstantXYZ(1, 1, 1);
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeTransformationIsNull);

		Assert.assertEquals(fromValue, ((RealtimeTransformationIsNull) createIsNull).getOther());
	}
}
