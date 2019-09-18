/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.junit.Test;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.mockclass.MockOnlineObjectImpl;

public class ConsoleWriterTest {
	private ConsoleWriter<Object> mockWriter = null;
	private Runnable mockRunnable = null;

	@Test
	public void testMethodRunOfRunnableAttributeWhenConstructorGotNoArgumentSimpleTest() {
		mockWriter = new ConsoleWriter<Object>();
		mockRunnable = mockWriter.getRunnable();

		mockRunnable.run();
	}

	@Test
	public void testMethodRunOfRunnableAttributeWhenConstructorGotAMessageStringArgumentSimpleTest() {
		mockWriter = new ConsoleWriter<Object>("This is a test message.");
		mockRunnable = mockWriter.getRunnable();

		mockRunnable.run();
	}

	@Test
	public void testOperationStateChangedWithMockArgumentsWhenConstructorGotNoArgumentSimpleTest() {
		mockWriter = new ConsoleWriter<Object>();

		OnlineObject testObject = new MockOnlineObjectImpl();

		mockWriter.operationStateChanged(testObject, OperationState.NEW);
	}

	@Test
	public void testOperationStateChangedWithMockArgumentsWhenConstructorGotAMessageStringSimpleTest() {
		mockWriter = new ConsoleWriter<Object>("This is a test message");

		OnlineObject testObject = new MockOnlineObjectImpl();

		mockWriter.operationStateChanged(testObject, OperationState.NEW);
	}
}
