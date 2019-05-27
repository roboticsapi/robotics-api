/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import org.roboticsapi.core.State;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.ExplicitState;
import org.roboticsapi.core.state.FalseState;
import org.roboticsapi.core.state.LongState;
import org.roboticsapi.core.state.NotState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.state.TrueState;

public abstract class StateOccurenceTimeComputer {

	public TimeIntervals getIntervals(State state) {
		if (state instanceof TrueState) {
			return getTrueInterval();
		} else if (state instanceof FalseState) {
			return getFalseInterval();
		} else if (state instanceof AndState) {
			return getAndInterval((AndState) state);
		} else if (state instanceof OrState) {
			return getOrInterval((OrState) state);
		} else if (state instanceof LongState) {
			return getLongInterval((LongState) state);
		} else if (state instanceof ExplicitState) {
			return getExplicitInterval((ExplicitState) state);
		} else if (state instanceof NotState) {
			return getNotInterval((NotState) state);
		}
		return getCustomStateIntervals(state);
	}

	private TimeIntervals getNotInterval(NotState state) {
		TimeIntervals time = new TimeIntervals();
		TimeIntervals other = getIntervals(state.getState());
		if (other == null) {
			return null;
		}
		double start = Double.NEGATIVE_INFINITY;
		while (start < Double.POSITIVE_INFINITY) {
			double end = other.getStartAfter(start);
			if (end != Double.NEGATIVE_INFINITY) {
				time.addInterval(start, end);
			}
			start = other.getEndAfter(end);
		}
		return time;
	}

	private TimeIntervals getExplicitInterval(ExplicitState state) throws AssertionError {
		TimeIntervals time = new TimeIntervals();
		TimeIntervals on = getIntervals(state.getActivatingState());
		TimeIntervals off = getIntervals(state.getDeactivatingState());
		if (on == null || off == null) {
			return null;
		}
		double s1 = on.getFirstStart(), e1 = on.getEndAfter(s1);
		double s2 = off.getFirstStart(), e2 = off.getEndAfter(s2);
		while (s1 < Double.POSITIVE_INFINITY) {
			if (s1 < s2) {
				time.addInterval(s1, s2);
				if (e1 < e2) {
					s1 = on.getStartAfter(e1);
					e1 = on.getEndAfter(s1);
				} else {
					s1 = e2;
				}
			} else {
				if (e1 < e2) {
					s1 = on.getStartAfter(e1);
					e1 = on.getEndAfter(s1);
				} else {
					if (s1 < e2) {
						s1 = e2;
					}
					s2 = off.getStartAfter(e2);
					e2 = off.getEndAfter(s2);
					if (s1 == e1) {
						s1 = on.getStartAfter(e1);
						e1 = on.getEndAfter(s1);
					}
				}
			}
		}
		return time;
	}

	private TimeIntervals getLongInterval(LongState state) {
		TimeIntervals time = new TimeIntervals();
		State longState = state.getOther();
		double duration = state.getSeconds();
		TimeIntervals other = getIntervals(longState);
		if (other == null) {
			return null;
		}
		double start = other.getFirstStart();
		while (start < Double.POSITIVE_INFINITY) {
			double end = other.getEndAfter(start);
			if (end >= start + duration) {
				time.addInterval(start + duration, end);
			}
			start = other.getStartAfter(end);
		}
		return time;
	}

	private TimeIntervals getOrInterval(OrState state) {
		TimeIntervals time = null;
		for (State inner : state.getStates()) {
			TimeIntervals intervals = getIntervals(inner);
			if (intervals == null) {
				return null;
			}
			if (time == null) {
				time = intervals;
			} else {
				time = time.union(intervals);
			}
		}
		return time;
	}

	private TimeIntervals getAndInterval(AndState state) {
		TimeIntervals time = null;
		for (State inner : state.getStates()) {
			TimeIntervals intervals = getIntervals(inner);
			if (intervals == null) {
				return null;
			}
			if (time == null) {
				time = intervals;
			} else {
				time = time.intersect(intervals);
			}
		}
		return time;
	}

	private TimeIntervals getFalseInterval() {
		TimeIntervals time = new TimeIntervals();
		return time;
	}

	private TimeIntervals getTrueInterval() {
		TimeIntervals time = new TimeIntervals();
		time.addInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return time;
	}

	protected abstract TimeIntervals getCustomStateIntervals(State state);

	public Double getTimeAtFirstOccurence(State state) {
		TimeIntervals intervals = getIntervals(state);
		if (intervals == null) {
			return null;
		}
		return intervals.getFirstStart();
	}
}
