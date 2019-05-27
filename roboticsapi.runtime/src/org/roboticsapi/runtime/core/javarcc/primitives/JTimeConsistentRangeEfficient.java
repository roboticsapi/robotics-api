/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;

public class JTimeConsistentRangeEfficient extends JPrimitive {
	JInPort<Long> inFirstStart = add("inFirstStart", new JInPort<Long>());
	JInPort<Long> inFirstEnd = add("inFirstEnd", new JInPort<Long>());
	JInPort<Long> inSecondStart = add("inSecondStart", new JInPort<Long>());
	JInPort<Long> inSecondEnd = add("inSecondEnd", new JInPort<Long>());
	JOutPort<Long> outStart = add("outStart", new JOutPort<Long>());
	JOutPort<Long> outEnd = add("outEnd", new JOutPort<Long>());
	JParameter<RPIdouble> propMaxSize = add("MaxSize", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propMaxAge = add("MaxAge", new JParameter<RPIdouble>());

	Long[] history1s, history1e, history2s, history2e;
	int lastFirst, lastSecond;
	int count, current;
	int step;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirstStart, inFirstEnd, inSecondStart, inSecondEnd);
		count = (int) (propMaxAge.get().get() / getNet().getCycleTime());
		step = 1;
		for (int i = count; i > 0; step <<= 1)
			i >>= 1;
		current = 0;

		if (count < 1)
			throw new IllegalArgumentException("MaxAge");

		history1s = new Long[count];
		history1e = new Long[count];
		history2s = new Long[count];
		history2e = new Long[count];
		lastFirst = -1;
		lastSecond = -1;
	}

	@Override
	public void updateData() {
		if (anyNull(inFirstEnd, inFirstStart, inSecondEnd, inSecondStart))
			return;
		history1s[current % count] = inFirstStart.get();
		history1e[current % count] = inFirstEnd.get();
		history2s[current % count] = inSecondStart.get();
		history2e[current % count] = inSecondEnd.get();

		int first = -1, second = -1;
		// value 1 modified
		if (current == 0 || history1s[(current - 1) % count] != history1s[current % count]
				|| history1e[(current - 1) % count] == history1e[current % count]) {
			int found = bisect(history1s[current % count], history1e[current % count], history2s, history2e, current,
					current - count); // lastSecond);
			if (found != -1) {
				second = found;
				first = current;
			}
		}

		// value 2 modified
		if (current == 0 || history2s[(current - 1) % count] != history2s[current % count]
				|| history2e[(current - 1) % count] == history2e[current % count]) {
			int found = bisect(history2s[current % count], history2e[current % count], history1s, history1e, current,
					current - count); // lastFirst);
			if (found != -1) {
				second = current;
				first = found;
			}
		}

		if (second == -1 || first == -1) {
			second = lastSecond;
			first = lastFirst;
		}

		if (second != -1 && first != -1) {
			Long start = Math.min(history1s[first % count], history2s[second % count]);
			Long end = Math.max(history1e[first % count], history2e[second % count]);
//			checkData(start, end);
			lastFirst = first;
			lastSecond = second;
			outStart.set(start);
			outEnd.set(end);

		} else {
//			checkData(-1L, -1L);
		}

		current++;
	}

	public void checkData(long proposedStart, long proposedEnd) {
		int first = current, second = current;
		while (first > current - count && first >= 0 && second > current - count && second >= 0) {
			Long start, end;
			start = Math.min(history1s[first % count], history2s[second % count]);
			end = Math.max(history1e[first % count], history2e[second % count]);
			if ((end - start) / 1e3 < propMaxSize.get().get()) {
				if (proposedEnd != end || proposedStart != start) {
					System.err.println("Efficient evaluation found incorrect range");
				}
				return;
			}
			if (history1e[first % count] > history2s[second % count]) {
				first--;
			} else {
				second--;
			}
		}
		if (proposedEnd != -1 || proposedStart != -1) {
			System.err.println("Efficient evaluation found range though none exists");
		}
	}

	private int bisect(Long start, Long end, Long[] starts, Long[] ends, int startIndex, int endIndex) {
		int pos = startIndex;
		int ret = -1;
		int step = this.step;
		while (step > startIndex - endIndex)
			step >>= 1;
		while (true) {

			if (pos > startIndex) {
				pos -= step;
			} else if (pos < 0 || pos < endIndex) {
				pos += step;
			} else {
				if ((Math.max(end, ends[pos % count]) - Math.min(start, starts[pos % count])) / 1e3 < propMaxSize.get()
						.get()) {
					if (ret < pos)
						ret = pos;
				}
				if (starts[pos % count] < start) {
					pos += step;
				} else {
					pos -= step;
				}
			}
			if (step > 0)
				step >>= 1;
			else
				break;
		}
		return ret;
	}

}
