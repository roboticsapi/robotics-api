/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.WritableRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeinteger.ConditionalRealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.core.realtimevalue.realtimeinteger.WritableRealtimeInteger;

public class ConditionalRealtimeIntegerTest {
	private final int INT_IF_TRUE = 1;
	private final int INT_IF_FALSE = 0;

	private RealtimeInteger mockSensor = null;
	private WritableRealtimeInteger ifTrue;
	private WritableRealtimeInteger ifFalse;
	private WritableRealtimeBoolean condition;

	@Before
	public void setup() {
		ifTrue = RealtimeInteger.createWritable(INT_IF_TRUE);
		ifFalse = RealtimeInteger.createWritable(INT_IF_FALSE);
		condition = RealtimeBoolean.createWritable(false);
	}

	@After
	public void teardown() {
		ifTrue = null;
		ifFalse = null;
		condition = null;
	}

	@Test
	public void testConstructorWithTwoRealtimeIntegers() throws RealtimeValueReadException {
		mockSensor = RealtimeInteger.createConditional(condition, ifTrue, ifFalse);

		Assert.assertTrue(mockSensor instanceof ConditionalRealtimeInteger);

		ConditionalRealtimeInteger mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertSame(condition, mockSensor2.getCondition());
		assertSame(ifTrue, mockSensor2.getIfTrue());
		assertSame(ifFalse, mockSensor2.getIfFalse());
	}

	@Test
	public void testConstructorWithRealtimeIntegerAndInt() {
		mockSensor = new ConditionalRealtimeInteger(condition, INT_IF_TRUE, ifFalse);

		Assert.assertTrue(mockSensor instanceof ConditionalRealtimeInteger);

		ConditionalRealtimeInteger mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertSame(condition, mockSensor2.getCondition());
		assertSame(INT_IF_TRUE, mockSensor2.getIfTrue().getCheapValue());
		assertSame(ifFalse, mockSensor2.getIfFalse());

		mockSensor = new ConditionalRealtimeInteger(condition, ifTrue, INT_IF_FALSE);

		mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertSame(condition, mockSensor2.getCondition());
		assertSame(ifTrue, mockSensor2.getIfTrue());
		assertSame(INT_IF_FALSE, mockSensor2.getIfFalse().getCheapValue());
	}

	@Test
	public void testConstructorWithTwoInt() {
		mockSensor = new ConditionalRealtimeInteger(condition, INT_IF_TRUE, INT_IF_FALSE);

		ConditionalRealtimeInteger mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertSame(condition, mockSensor2.getCondition());
		assertSame(INT_IF_TRUE, mockSensor2.getIfTrue().getCheapValue());
		assertSame(INT_IF_FALSE, mockSensor2.getIfFalse().getCheapValue());
	}

	@Test
	public void testToStringSimpleTest() {
		mockSensor = new ConditionalRealtimeInteger(RealtimeBoolean.createWritable(true), INT_IF_TRUE, INT_IF_FALSE);

		assertNotNull(mockSensor.toString());
	}

	@Test
	public void testCheapValueByUsingGetCurrentValue() throws RoboticsException {
		mockSensor = new ConditionalRealtimeInteger(RealtimeBoolean.createWritable(true), INT_IF_TRUE, INT_IF_FALSE);

		ConditionalRealtimeInteger mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertTrue(mockSensor2.getCondition().getCurrentValue());
		assertSame(INT_IF_TRUE, mockSensor.getCurrentValue());

		mockSensor = new ConditionalRealtimeInteger(RealtimeBoolean.createWritable(false), INT_IF_TRUE, INT_IF_FALSE);

		mockSensor2 = (ConditionalRealtimeInteger) mockSensor;

		assertFalse(mockSensor2.getCondition().getCurrentValue());
		assertSame(INT_IF_FALSE, mockSensor.getCurrentValue());
	}
}
