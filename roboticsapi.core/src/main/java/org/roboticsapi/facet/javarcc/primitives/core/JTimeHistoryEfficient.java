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

public class JTimeHistoryEfficient extends JPrimitive {
	JInPort<Long> inValue = add("inValue", new JInPort<Long>());
	JInPort<Long> inTime = add("inTime", new JInPort<Long>());
	JOutPort<RPIdouble> outAge = add("outAge", new JOutPort<RPIdouble>());
	JParameter<RPIdouble> propMaxAge = add("MaxAge", new JParameter<RPIdouble>());
	Long[] history;
	int count, current;
	int step;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue, inTime);
		count = (int) (propMaxAge.get().get() / getNet().getCycleTime());
		current = 0;
		if (count < 1)
			throw new IllegalArgumentException("MaxAge");
		history = new Long[count];

		step = 1;
		for (int i = count; i > 0; step <<= 1)
			i >>= 1;
	}

	@Override
	public void updateData() {
		if (anyNull(inTime))
			return;
		history[current % count] = inValue.get();

		int pos = current;
		int ret = -1;
		int step = this.step;
		while (true) {
			if (pos > current) {
				pos -= step;
			} else if (pos < 0 || pos < current - count) {
				pos += step;
			} else {
				if (history[pos % count] != null && history[pos % count] <= inTime.get()) {
					if (ret < pos)
						ret = pos;
				}
				if (history[pos % count] <= inTime.get()) {
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
		outAge.set(new RPIdouble((current - ret) * getNet().getCycleTime()));

//		for (int i = current; i > current - count && i >= 0; i--) {
//			if (history[i % count] != null && history[i % count] <= inTime.get()) {
//				if(i != ret) 
//					System.err.println("XXX");
////				outAge.set(new RPIdouble((current - i) * getNet().getCycleTime()));
//				break;
//			}
//		}
		current++;
	}

}
