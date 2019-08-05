/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.State;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.ExplicitState;
import org.roboticsapi.core.state.FalseState;
import org.roboticsapi.core.state.LongState;
import org.roboticsapi.core.state.NotState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.state.TrueState;
import org.roboticsapi.core.util.StateOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;

public class StateOccurenceTimeComputerTest {

	private class UnknownState extends State {
	}

	private class TimeIntervalState extends State {
		TimeIntervals occurence;

		public TimeIntervalState(TimeIntervals intervals) {
			occurence = intervals;
		}

		public TimeIntervals getOccurence() {
			return occurence;
		}
	}

	private class TestTimeComputer extends StateOccurenceTimeComputer {
		@Override
		protected TimeIntervals getCustomStateIntervals(State state) {
			if (state instanceof TimeIntervalState) {
				return ((TimeIntervalState) state).getOccurence();
			}
			return null;
		}
	}

	@Test
	public void testTrueState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(new TrueState());
		Assert.assertEquals(Double.NEGATIVE_INFINITY, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(Double.NEGATIVE_INFINITY), 0.001);
	}

	@Test
	public void testFalseState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(new FalseState());
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getFirstStart(), 0.001);
	}

	@Test
	public void testUnknownState() {
		TestTimeComputer c = new TestTimeComputer();
		Assert.assertNull(c.getTimeAtFirstOccurence(new UnknownState()));
		TimeIntervals intervals = c.getIntervals(new UnknownState());
		Assert.assertNull(intervals);
	}

	@Test
	public void testAndState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals second = new TimeIntervals(2, 5);
		TimeIntervals intervals = c
				.getIntervals(new AndState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(2d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intervals.getEndAfter(2), 0.001);
		Assert.assertEquals(4d, intervals.getStartAfter(3), 0.001);
		Assert.assertEquals(5d, intervals.getEndAfter(4), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(5), 0.001);
	}

	@Test
	public void testAndUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals intervals = c.getIntervals(new AndState(new TimeIntervalState(first), new UnknownState()));
		Assert.assertNull(intervals);
	}

	@Test
	public void testOrUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals intervals = c.getIntervals(new OrState(new TimeIntervalState(first), new UnknownState()));
		Assert.assertNull(intervals);
	}

	@Test
	public void testNotUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(new NotState(new UnknownState()));
		Assert.assertNull(intervals);
	}

	@Test
	public void testLongUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(new LongState(new UnknownState(), 1));
		Assert.assertNull(intervals);
	}

	@Test
	public void testExplicitUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals intervals = c.getIntervals(new ExplicitState(new TimeIntervalState(first), new UnknownState()));
		Assert.assertNull(intervals);
		intervals = c.getIntervals(new ExplicitState(new UnknownState(), new TimeIntervalState(first)));
		Assert.assertNull(intervals);
	}

	@Test
	public void testOrState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals second = new TimeIntervals(2, 5);
		TimeIntervals intervals = c
				.getIntervals(new OrState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(1d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(6d, intervals.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(6), 0.001);
	}

	@Test
	public void testExplicitState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals second = new TimeIntervals(2, 5);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(1d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(2d, intervals.getEndAfter(1), 0.001);
		Assert.assertEquals(5d, intervals.getStartAfter(2), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(5), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateIntermediateOff() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 4);
		TimeIntervals second = new TimeIntervals(2, 3);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(1d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(2d, intervals.getEndAfter(1), 0.001);
		Assert.assertEquals(3d, intervals.getStartAfter(2), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateOffAtStart() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(2, 4);
		TimeIntervals second = new TimeIntervals(1, 3);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(3d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateOnBeforeOff() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 2);
		TimeIntervals second = new TimeIntervals(3, 4);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(1d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intervals.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(3), 0.001);
	}

	@Test
	public void testExplicitStateOffBeforeOn() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(3, 4);
		TimeIntervals second = new TimeIntervals(1, 2);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(3d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateSameTime() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals second = new TimeIntervals(1, 3);
		TimeIntervals intervals = c
				.getIntervals(new ExplicitState(new TimeIntervalState(first), new TimeIntervalState(second)));

		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getFirstStart(), 0.001);
	}

	@Test
	public void testNotState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals intervals = c.getIntervals(new NotState(new TimeIntervalState(first)));

		Assert.assertEquals(Double.NEGATIVE_INFINITY, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(1d, intervals.getEndAfter(Double.NEGATIVE_INFINITY), 0.001);
		Assert.assertEquals(3d, intervals.getStartAfter(1), 0.001);
		Assert.assertEquals(4d, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(6d, intervals.getStartAfter(4), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(6), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testLongState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 2);
		first.addInterval(4, 6);
		TimeIntervals intervals = c.getIntervals(new LongState(new TimeIntervalState(first), 1));

		Assert.assertEquals(2d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(2d, intervals.getEndAfter(2), 0.001);
		Assert.assertEquals(5d, intervals.getStartAfter(2), 0.001);
		Assert.assertEquals(6d, intervals.getEndAfter(5), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(5), 0.001);
	}
}
