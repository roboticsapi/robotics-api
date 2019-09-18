/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.junit.Assert;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Transformation;

public class RealtimeTransformationArrayTest {

	// @Test disabled, as RealtimeTransformationArray.createFromConstant(...) is
	// not yet implemented
	public void testIsNullHasExpectedStructure() {
		RealtimeTransformationArray fromValue = RealtimeTransformationArray
				.createFromConstant(new Transformation[] { new Transformation(0, 0, 0, 0, 0, 0) });
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeTransformationArrayIsNull);

		Assert.assertEquals(fromValue, ((RealtimeTransformationArrayIsNull) createIsNull).getOther());
	}
}
