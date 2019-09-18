/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.ExponentiallySmootedRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.CycleTime;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAdd;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleConditional;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleDivide;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleIsNull;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleMultiply;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoublePower;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoublePre;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class ExponentiallySmootedRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double, ExponentiallySmootedRealtimeDouble> {

	public ExponentiallySmootedRealtimeDoubleMapper() {
		super(ExponentiallySmootedRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(ExponentiallySmootedRealtimeDouble value)
			throws MappingException, RpiException {

		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value);

		FragmentInPort input = ret.addInPort("inValue");
		ret.addDependency(value.getOther(), input);

		// calculate alpha
		CycleTime cycleTime = ret.add(new CycleTime());
		DoubleDivide cycleTimeDivHalfLife = ret.add(new DoubleDivide(0d, -value.getHalfLife()));
		ret.connect(cycleTime.getOutValue(), cycleTimeDivHalfLife.getInFirst());

		DoublePower power = ret.add(new DoublePower(2d, 0d));
		ret.connect(cycleTimeDivHalfLife.getOutValue(), power.getInSecond());

		DoubleMultiply negatePower = ret.add(new DoubleMultiply(-1d, 0d));
		ret.connect(power.getOutValue(), negatePower.getInSecond());

		DoubleAdd add = ret.add(new DoubleAdd(1d, 0d));
		ret.connect(negatePower.getOutValue(), add.getInSecond());

		OutPort alpha = add.getOutValue();
		OutPort oneMinusAlpha = power.getOutValue();

		// calculate exponential smoothing
		DoublePre pre = ret.add(new DoublePre());

		DoubleIsNull nullChecker = ret.add(new DoubleIsNull());
		ret.connect(pre.getOutValue(), nullChecker.getInValue());

		DoubleConditional ifNull = ret.add(new DoubleConditional());
		ret.connect(nullChecker.getOutValue(), ifNull.getInCondition());
		ret.connect(input.getInternalOutPort(), ifNull.getInTrue());
		ret.connect(pre.getOutValue(), ifNull.getInFalse());

		OutPort lastValue = ifNull.getOutValue();

		// smoothing
		DoubleMultiply firstTerm = ret.add(new DoubleMultiply());
		ret.connect(alpha, firstTerm.getInFirst());
		ret.connect(input.getInternalOutPort(), firstTerm.getInSecond());

		DoubleMultiply secondTerm = ret.add(new DoubleMultiply());
		ret.connect(oneMinusAlpha, secondTerm.getInFirst());
		ret.connect(lastValue, secondTerm.getInSecond());

		DoubleAdd addTerms = ret.add(new DoubleAdd());
		ret.connect(firstTerm.getOutValue(), addTerms.getInFirst());
		ret.connect(secondTerm.getOutValue(), addTerms.getInSecond());

		ret.connect(addTerms.getOutValue(), pre.getInValue());

		ret.defineResult(addTerms.getOutValue());
		return ret;
	}
}
