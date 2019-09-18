/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

public class JTimeConsistentRange extends JPrimitive {
	JInPort<Long> inFirstStart = add("inFirstStart", new JInPort<Long>());
	JInPort<Long> inFirstEnd = add("inFirstEnd", new JInPort<Long>());
	JInPort<Long> inSecondStart = add("inSecondStart", new JInPort<Long>());
	JInPort<Long> inSecondEnd = add("inSecondEnd", new JInPort<Long>());
	JOutPort<Long> outStart = add("outStart", new JOutPort<Long>());
	JOutPort<Long> outEnd = add("outEnd", new JOutPort<Long>());
	JParameter<RPIdouble> propMaxSize = add("MaxSize", new JParameter<RPIdouble>());
	JParameter<RPIdouble> propMaxAge = add("MaxAge", new JParameter<RPIdouble>());

	Long[] history1s, history1e, history2s, history2e;
	int count, current;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFirstStart, inFirstEnd, inSecondStart, inSecondEnd);
		count = (int) (propMaxAge.get().get() / getNet().getCycleTime());
		current = 0;

		if (count < 1)
			throw new IllegalArgumentException("MaxAge");

		history1s = new Long[count];
		history1e = new Long[count];
		history2s = new Long[count];
		history2e = new Long[count];
	}

	@Override
	public void updateData() {
		if (anyNull(inFirstEnd, inFirstStart, inSecondEnd, inSecondStart))
			return;
		history1s[current % count] = inFirstStart.get();
		history1e[current % count] = inFirstEnd.get();
		history2s[current % count] = inSecondStart.get();
		history2e[current % count] = inSecondEnd.get();

		int first = current, second = current;
		while (first > current - count && first >= 0 && second > current - count && second >= 0) {
			Long start, end;
			start = Math.min(history1s[first % count], history2s[second % count]);
			end = Math.max(history1e[first % count], history2e[second % count]);
			if ((end - start) / 1e3 < propMaxSize.get().get()) {
				outStart.set(start);
				outEnd.set(end);
				break;
			}
			if (history1e[first % count] > history2s[second % count]) {
				first--;
			} else {
				second--;
			}
		}
		current++;
	}

}
