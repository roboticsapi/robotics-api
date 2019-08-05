/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.commandfilter;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.commandfilter.SimpleRuntimeCommandFilter;

public class SimpleRuntimeCommandFilterTest {
	private class MockSimpleRuntimeCommandFilter extends SimpleRuntimeCommandFilter {
		@Override
		protected void processRuntimeCommand(RuntimeCommand command) {
		}

		@Override
		public boolean filterRuntimeCommand(RuntimeCommand command) {
			return super.filterRuntimeCommand(command);
		}
	}

	@Test
	public void testFilterRuntimeCommandWithNullCommandExpectingTrue() {
		MockSimpleRuntimeCommandFilter testFilter = new MockSimpleRuntimeCommandFilter();

		assertTrue(testFilter.filterRuntimeCommand(null));
	}
}
