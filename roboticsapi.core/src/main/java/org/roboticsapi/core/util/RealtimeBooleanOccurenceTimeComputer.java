/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import org.roboticsapi.core.realtimevalue.realtimeboolean.AndRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ConstantRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.FlipFlopRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.NegatedRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.OrRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public abstract class RealtimeBooleanOccurenceTimeComputer {

	public TimeIntervals getIntervals(RealtimeBoolean state) {
		if (state instanceof ConstantRealtimeBoolean) {
			if (state.getCheapValue()) {
				return getTrueInterval();
			} else {
				return getFalseInterval();
			}
		} else if (state instanceof AndRealtimeBoolean) {
			return getAndInterval((AndRealtimeBoolean) state);
		} else if (state instanceof OrRealtimeBoolean) {
			return getOrInterval((OrRealtimeBoolean) state);
		} else if (state instanceof FlipFlopRealtimeBoolean) {
			return getExplicitInterval((FlipFlopRealtimeBoolean) state);
		} else if (state instanceof NegatedRealtimeBoolean) {
			return getNotInterval((NegatedRealtimeBoolean) state);
		}
		return getCustomStateIntervals(state);
	}

	private TimeIntervals getAndInterval(AndRealtimeBoolean state) {
		TimeIntervals time = null;
		for (RealtimeBoolean inner : state.getInnerValues()) {
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

	private TimeIntervals getNotInterval(NegatedRealtimeBoolean state) {
		TimeIntervals time = new TimeIntervals();
		TimeIntervals other = getIntervals(state.getOther());
		if (other == null) {
			return null;
		}
		double start = Double.NEGATIVE_INFINITY;
		if (other.getFirstStart() == start) {
			start = other.getEndAfter(other.getFirstStart());
		}
		while (start < Double.POSITIVE_INFINITY) {
			double end = other.getStartAfter(start);
			if (end != Double.NEGATIVE_INFINITY) {
				time.addInterval(start, end);
			}
			start = other.getEndAfter(end);
		}
		return time;
	}

	private TimeIntervals getExplicitInterval(FlipFlopRealtimeBoolean state) throws AssertionError {
		TimeIntervals time = new TimeIntervals();
		TimeIntervals on = getIntervals(state.getActivatingValue());
		TimeIntervals off = getIntervals(state.getDeactivatingValue());
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

	private TimeIntervals getOrInterval(OrRealtimeBoolean state) {
		TimeIntervals time = null;
		for (RealtimeBoolean inner : state.getInnerValues()) {
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

	private TimeIntervals getFalseInterval() {
		TimeIntervals time = new TimeIntervals();
		return time;
	}

	private TimeIntervals getTrueInterval() {
		TimeIntervals time = new TimeIntervals();
		time.addInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return time;
	}

	protected abstract TimeIntervals getCustomStateIntervals(RealtimeBoolean state);

	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		TimeIntervals intervals = getIntervals(state);
		if (intervals == null) {
			return null;
		}
		return intervals.getFirstStart();
	}
}
