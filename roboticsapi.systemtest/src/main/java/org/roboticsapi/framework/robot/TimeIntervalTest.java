/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot;

import org.junit.Assert;
import org.junit.Test;

import org.roboticsapi.core.util.TimeIntervals;

public class TimeIntervalTest {

	@Test()
	public void testSimpleTimeInterval() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(2), 0.001);
	}

	@Test
	public void testAddSecondTimeInterval() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		interval.addInterval(3, 4);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(3d, interval.getStartAfter(2), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddTouchingIntervals() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		interval.addInterval(2, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(3d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(3), 0.001);
	}

	@Test
	public void testAddOverlappingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(2, 4);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddLeftOverlappingInterval() {
		TimeIntervals interval = new TimeIntervals(2, 4);
		interval.addInterval(1, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddContainedInterval() {
		TimeIntervals interval = new TimeIntervals(1, 4);
		interval.addInterval(2, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddLeftTouchingInterval() {
		TimeIntervals interval = new TimeIntervals(2, 3);
		interval.addInterval(1, 2);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(3d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(3), 0.001);
	}

	@Test
	public void testAddContainingInterval() {
		TimeIntervals interval = new TimeIntervals(2, 3);
		interval.addInterval(1, 4);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddBothTouchingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		interval.addInterval(3, 4);
		interval.addInterval(2, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testAddBothOverlappingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(4, 6);
		interval.addInterval(2, 5);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(6d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(6), 0.001);
	}

	@Test
	public void testRemoveNonexistantTimeInterval() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		interval.removeInterval(3, 4);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(2), 0.001);
	}

	@Test
	public void testRemoveRightIntersectingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.removeInterval(2, 4);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(2), 0.001);
	}

	@Test
	public void testRemoveLeftIntersectingInterval() {
		TimeIntervals interval = new TimeIntervals(2, 4);
		interval.removeInterval(1, 3);
		Assert.assertEquals(3d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testRemoveLeftTouchingInterval() {
		TimeIntervals interval = new TimeIntervals(2, 4);
		interval.removeInterval(1, 2);
		Assert.assertEquals(2d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testRemoveTouchingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 2);
		interval.removeInterval(2, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(2), 0.001);
	}

	@Test
	public void testRemoveEntireTimeInterval() {
		TimeIntervals interval = new TimeIntervals(2, 3);
		interval.removeInterval(1, 4);
		Assert.assertEquals(Double.POSITIVE_INFINITY, interval.getFirstStart(),
				0.001);
	}

	@Test
	public void testRemoveSubInterval() {
		TimeIntervals interval = new TimeIntervals(1, 4);
		interval.removeInterval(2, 3);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(3d, interval.getStartAfter(2), 0.001);
		Assert.assertEquals(4d, interval.getEndAfter(3), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(4), 0.001);
	}

	@Test
	public void testRemoveIntersectingInterval() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(4, 5);
		interval.addInterval(6, 7);
		interval.addInterval(8, 10);
		interval.removeInterval(2, 9);
		Assert.assertEquals(1d, interval.getFirstStart(), 0.001);
		Assert.assertEquals(2d, interval.getEndAfter(1), 0.001);
		Assert.assertEquals(9d, interval.getStartAfter(2), 0.001);
		Assert.assertEquals(10d, interval.getEndAfter(9), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				interval.getStartAfter(10), 0.001);
	}

	@Test
	public void testUnionIntervals() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(8, 10);
		TimeIntervals interval2 = new TimeIntervals(2, 4);
		interval.addInterval(11, 12);
		TimeIntervals union = interval.union(interval2);

		Assert.assertEquals(1d, union.getFirstStart(), 0.001);
		Assert.assertEquals(4d, union.getEndAfter(1), 0.001);
		Assert.assertEquals(8d, union.getStartAfter(4), 0.001);
		Assert.assertEquals(10d, union.getEndAfter(8), 0.001);
		Assert.assertEquals(11d, union.getStartAfter(10), 0.001);
		Assert.assertEquals(12d, union.getEndAfter(11), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY, union.getStartAfter(12),
				0.001);
	}

	@Test
	public void testIntersectIntervals() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(8, 10);
		TimeIntervals interval2 = new TimeIntervals(2, 4);
		interval.addInterval(11, 12);
		TimeIntervals intersect = interval.intersect(interval2);

		Assert.assertEquals(2d, intersect.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intersect.getEndAfter(2), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				intersect.getStartAfter(3), 0.001);
	}

	@Test
	public void testIntersectIntervals2() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(8, 10);
		TimeIntervals interval2 = new TimeIntervals(2, 4);
		interval.addInterval(11, 12);
		TimeIntervals intersect = interval2.intersect(interval);

		Assert.assertEquals(2d, intersect.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intersect.getEndAfter(2), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				intersect.getStartAfter(3), 0.001);
	}

	@Test
	public void testIntersectIntervals3() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(4, 5);
		interval.addInterval(6, 8);
		TimeIntervals interval2 = new TimeIntervals(2, 7);
		TimeIntervals intersect = interval.intersect(interval2);

		Assert.assertEquals(2d, intersect.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intersect.getEndAfter(2), 0.001);
		Assert.assertEquals(4d, intersect.getStartAfter(3), 0.001);
		Assert.assertEquals(5d, intersect.getEndAfter(4), 0.001);
		Assert.assertEquals(6d, intersect.getStartAfter(5), 0.001);
		Assert.assertEquals(7d, intersect.getEndAfter(6), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				intersect.getStartAfter(7), 0.001);
	}

	@Test
	public void testIntersectIntervals4() {
		TimeIntervals interval = new TimeIntervals(1, 3);
		interval.addInterval(4, 5);
		interval.addInterval(6, 8);
		TimeIntervals interval2 = new TimeIntervals(2, 7);
		TimeIntervals intersect = interval2.intersect(interval);

		Assert.assertEquals(2d, intersect.getFirstStart(), 0.001);
		Assert.assertEquals(3d, intersect.getEndAfter(2), 0.001);
		Assert.assertEquals(4d, intersect.getStartAfter(3), 0.001);
		Assert.assertEquals(5d, intersect.getEndAfter(4), 0.001);
		Assert.assertEquals(6d, intersect.getStartAfter(5), 0.001);
		Assert.assertEquals(7d, intersect.getEndAfter(6), 0.001);
		Assert.assertEquals(Double.POSITIVE_INFINITY,
				intersect.getStartAfter(7), 0.001);
	}

}
