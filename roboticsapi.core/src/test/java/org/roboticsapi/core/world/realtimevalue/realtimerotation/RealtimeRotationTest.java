/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Rotation;

public class RealtimeRotationTest {

	@Test
	public void testIsNullHasExpectedStructure() {
		RealtimeRotation fromValue = RealtimeRotation.createFromConstant(new Rotation(1, 1, 1));
		RealtimeBoolean createIsNull = fromValue.isNull();

		Assert.assertTrue(createIsNull instanceof RealtimeRotationIsNull);

		Assert.assertEquals(fromValue, ((RealtimeRotationIsNull) createIsNull).getOther());
	}
}
