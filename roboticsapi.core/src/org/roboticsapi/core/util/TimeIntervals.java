/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.ArrayList;
import java.util.List;

public class TimeIntervals {
	private class Interval {
		double start;
		double end;

		Interval(double start, double end) {
			this.start = start;
			this.end = end;
		}

		boolean contains(double start, double end) {
			return this.start <= start && this.end >= end;
		}

		boolean contains(Interval other) {
			return contains(other.start, other.end);
		}

		boolean intersects(double start, double end) {
			return this.start <= end && this.end >= start;
		}

		boolean intersects(Interval other) {
			return intersects(other.start, other.end);
		}

		@Override
		public String toString() {
			return "[" + start + ", " + end + "] ";
		}
	}

	private final List<Interval> intervals = new ArrayList<Interval>();

	public TimeIntervals() {
	}

	public TimeIntervals(double start, double end) {
		intervals.add(new Interval(start, end));
	}

	private List<Interval> getIntersectingIntervals(Interval other) {
		List<Interval> ret = new ArrayList<TimeIntervals.Interval>();
		for (Interval interval : intervals) {
			if (interval.intersects(other)) {
				ret.add(interval);
			}
		}
		return ret;
	}

	private List<Interval> getContainedIntervals(Interval other) {
		List<Interval> ret = new ArrayList<TimeIntervals.Interval>();
		for (Interval interval : intervals) {
			if (other.contains(interval)) {
				ret.add(interval);
			}
		}
		return ret;
	}

	private List<Interval> getContainingIntervals(Interval other) {
		List<Interval> ret = new ArrayList<TimeIntervals.Interval>();
		for (Interval interval : intervals) {
			if (interval.contains(other)) {
				ret.add(interval);
			}
		}
		return ret;
	}

	public double getFirstStart() {
		if (intervals.size() == 0) {
			return Double.POSITIVE_INFINITY;
		}
		return intervals.get(0).start;
	}

	public double getStartAfter(double time) {
		for (Interval interval : intervals) {
			if (interval.start > time) {
				return interval.start;
			}
		}
		return Double.POSITIVE_INFINITY;
	}

	public double getEndAfter(double time) {
		for (Interval interval : intervals) {
			if (interval.end >= time) {
				return interval.end;
			}
		}
		return Double.POSITIVE_INFINITY;
	}

	public void removeInterval(double start, double end) {
		Interval newInterval = new Interval(start, end);

		// remove all contained intervals
		List<Interval> containedIntervals = getContainedIntervals(newInterval);
		for (Interval i : containedIntervals) {
			intervals.remove(i);
		}

		// split containing intervals
		List<Interval> containingIntervals = getContainingIntervals(newInterval);
		for (Interval i : containingIntervals) {
			double newEnd = i.end;
			i.end = start;
			addInterval(end, newEnd);
		}

		// correct all intersecting intervals
		List<Interval> intersectingIntervals = getIntersectingIntervals(newInterval);
		for (Interval i : intersectingIntervals) {
			if (i.start < start && i.end > start) {
				i.end = start;
			}
			if (i.start > start && i.start < end) {
				i.start = end;
			}
		}
	}

	public void addInterval(double start, double end) {
		Interval newInterval = new Interval(start, end);

		// remove all contained intervals
		List<Interval> containedIntervals = getContainedIntervals(newInterval);
		for (Interval i : containedIntervals) {
			intervals.remove(i);
		}

		// already have a containing interval -> done
		List<Interval> containingIntervals = getContainingIntervals(newInterval);
		if (containingIntervals.size() > 0) {
			return;
		}

		// correct intersecting intervals
		List<Interval> intersectingIntervals = getIntersectingIntervals(newInterval);
		if (intersectingIntervals.size() == 2) {
			intersectingIntervals.get(0).end = intersectingIntervals.get(1).end;
			intervals.remove(intersectingIntervals.get(1));
		} else if (intersectingIntervals.size() == 1) {
			if (intersectingIntervals.get(0).start > start) {
				intersectingIntervals.get(0).start = start;
			}
			if (intersectingIntervals.get(0).end < end) {
				intersectingIntervals.get(0).end = end;
			}
		} else {
			for (int i = 0; i < intervals.size(); i++) {
				if (intervals.get(i).start > start) {
					intervals.add(i, newInterval);
					return;
				}
			}
			intervals.add(newInterval);
		}
	}

	public TimeIntervals intersect(TimeIntervals other) {
		TimeIntervals ret = new TimeIntervals();

		double s1 = getFirstStart(), s2 = other.getFirstStart();
		double e1 = getEndAfter(s1), e2 = other.getEndAfter(s2);
		while (s1 < Double.POSITIVE_INFINITY && s2 < Double.POSITIVE_INFINITY) {
			if (e1 < s2) { // first is totally left of second, continue with
							// next first
				s1 = getStartAfter(e1);
				e1 = getEndAfter(s1);
			} else if (e2 < s1) { // second is totally left of first, continue
									// with next second
				s2 = other.getStartAfter(e2);
				e2 = other.getEndAfter(s2);
			} else {
				double start = s1 < s2 ? s2 : s1;
				double end = e1 < e2 ? e1 : e2;
				ret.addInterval(start, end);
				if (end >= e2) {
					s2 = other.getStartAfter(e2);
					e2 = other.getEndAfter(s2);
				}
				if (end >= e1) {
					s1 = getStartAfter(e1);
					e1 = getEndAfter(s1);
				}
			}
		}

		return ret;
	}

	public TimeIntervals union(TimeIntervals other) {
		TimeIntervals ret = new TimeIntervals();
		for (Interval i : intervals) {
			ret.addInterval(i.start, i.end);
		}
		for (Interval i : other.intervals) {
			ret.addInterval(i.start, i.end);
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		for (Interval i : intervals) {
			ret.append(i.toString());
		}
		return ret.toString();
	}
}
