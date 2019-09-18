/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.util.RealtimeBooleanOccurenceTimeComputer;
import org.roboticsapi.core.util.TimeIntervals;

public class StateOccurenceTimeComputerTest {

	private class UnknownTimeRealtimeBoolean extends RealtimeBoolean {

		@Override
		public boolean isAvailable() {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private final class TimeIntervalRealtimeBoolean extends RealtimeBoolean {
		TimeIntervals occurence;

		public TimeIntervalRealtimeBoolean(TimeIntervals intervals) {
			occurence = intervals;
		}

		public TimeIntervals getOccurence() {
			return occurence;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && occurence.equals(((TimeIntervalRealtimeBoolean) obj).occurence);
		}

		@Override
		public boolean isAvailable() {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private class TestTimeComputer extends RealtimeBooleanOccurenceTimeComputer {
		@Override
		protected TimeIntervals getCustomStateIntervals(RealtimeBoolean state) {
			if (state instanceof TimeIntervalRealtimeBoolean) {
				return ((TimeIntervalRealtimeBoolean) state).getOccurence();
			}
			return null;
		}
	}

	@Test
	public void testTrueState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.TRUE);
		Assert.assertEquals(Double.NEGATIVE_INFINITY, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(Double.NEGATIVE_INFINITY), 0.001);
	}

	@Test
	public void testFalseState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.FALSE);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getFirstStart(), 0.001);
	}

	@Test
	public void testUnknownState() {
		TestTimeComputer c = new TestTimeComputer();
		Assert.assertNull(c.getTimeAtFirstOccurence(new UnknownTimeRealtimeBoolean()));
		TimeIntervals intervals = c.getIntervals(new UnknownTimeRealtimeBoolean());
		Assert.assertNull(intervals);
	}

	@Test
	public void testAndState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals second = new TimeIntervals(2, 5);
		TimeIntervals intervals = c
				.getIntervals(new TimeIntervalRealtimeBoolean(first).and(new TimeIntervalRealtimeBoolean(second)));

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
		TimeIntervals intervals = c
				.getIntervals(new TimeIntervalRealtimeBoolean(first).and(new UnknownTimeRealtimeBoolean()));
		Assert.assertNull(intervals);
	}

	@Test
	public void testOrUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals intervals = c
				.getIntervals(new TimeIntervalRealtimeBoolean(first).or(new UnknownTimeRealtimeBoolean()));
		Assert.assertNull(intervals);
	}

	@Test
	public void testNotUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals intervals = c.getIntervals(new UnknownTimeRealtimeBoolean().not());
		Assert.assertNull(intervals);
	}

	@Test
	public void testExplicitUnknown() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new UnknownTimeRealtimeBoolean()));
		Assert.assertNull(intervals);
		intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new UnknownTimeRealtimeBoolean(),
				new TimeIntervalRealtimeBoolean(first)));
		Assert.assertNull(intervals);
	}

	@Test
	public void testOrState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals second = new TimeIntervals(2, 5);
		TimeIntervals intervals = c
				.getIntervals(new TimeIntervalRealtimeBoolean(first).or(new TimeIntervalRealtimeBoolean(second)));

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
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

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
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

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
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

		Assert.assertEquals(3d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateOnBeforeOff() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 2);
		TimeIntervals second = new TimeIntervals(3, 4);
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

		Assert.assertEquals(1d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intervals.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(3), 0.001);
	}

	@Test
	public void testExplicitStateOffBeforeOn() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(3, 4);
		TimeIntervals second = new TimeIntervals(1, 2);
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

		Assert.assertEquals(3d, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}

	@Test
	public void testExplicitStateSameTime() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		TimeIntervals second = new TimeIntervals(1, 3);
		TimeIntervals intervals = c.getIntervals(RealtimeBoolean.createFlipFlop(new TimeIntervalRealtimeBoolean(first),
				new TimeIntervalRealtimeBoolean(second)));

		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getFirstStart(), 0.001);
	}

	@Test
	public void testNotState() {
		TestTimeComputer c = new TestTimeComputer();
		TimeIntervals first = new TimeIntervals(1, 3);
		first.addInterval(4, 6);
		TimeIntervals intervals = c.getIntervals(new TimeIntervalRealtimeBoolean(first).not());

		Assert.assertEquals(Double.NEGATIVE_INFINITY, intervals.getFirstStart(), 0.001);
		Assert.assertEquals(1d, intervals.getEndAfter(Double.NEGATIVE_INFINITY), 0.001);
		Assert.assertEquals(3d, intervals.getStartAfter(1), 0.001);
		Assert.assertEquals(4d, intervals.getEndAfter(3), 0.001);
		Assert.assertEquals(6d, intervals.getStartAfter(4), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getEndAfter(6), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, intervals.getStartAfter(Double.POSITIVE_INFINITY), 0.001);
	}
}
