/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.ReadDoubleArrayFromNet;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArraySet;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArraySetNull;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeArrayFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;

public class RealtimeDoubleArrayFragment extends RealtimeArrayFragment<Double[]> {

	private RealtimeDoubleArray realtimeValue;

	public RealtimeDoubleArrayFragment(RealtimeDoubleArray value, OutPort result, OutPort time, Primitive... children) {
		super(value, result, time, children);
		realtimeValue = value;
	}

	public RealtimeDoubleArrayFragment(RealtimeDoubleArray value, OutPort result, Primitive... children) {
		super(value, result, children);
		realtimeValue = value;
	}

	public RealtimeDoubleArrayFragment(RealtimeDoubleArray value) {
		super(value);
		realtimeValue = value;
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Double[]> observer) throws MappingException {
		ReadDoubleArrayFromNet netcomm = new ReadDoubleArrayFromNet("dd" + (nr++), realtimeValue.getSize());
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPIdoubleArray array = new RPIdoubleArray(value.getString());
				Double[] ret = new Double[realtimeValue.getSize()];
				for (int i = 0; i < ret.length; i++)
					ret[i] = array.get(i).get();
				observer.onValueChanged(ret);
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			DoubleArraySetNull setNull = ret.add(new DoubleArraySetNull());
			ret.connect(setNull.getOutValue(), netcomm.getInValue());
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", setNull.getInValue()));
			ret.addDependency(condition.not(), ret.addInPort("inCondition", setNull.getInNull()));
		} else {
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", netcomm.getInValue()));
		}
		return ret;
	}

	@Override
	protected RealtimeArrayFragment<Double[]>.ArrayCreatorPrimitive getArrayCreatorPrimitive(int size) {
		DoubleArray doubleArray = new DoubleArray();
		return new ArrayCreatorPrimitive(doubleArray, () -> doubleArray.getOutArray());
	}

	@Override
	protected RealtimeArrayFragment<Double[]>.ArraySetterPrimitive getArraySetterPrimitive(int size, int index) {
		DoubleArraySet setter = new DoubleArraySet(size, index);
		return new ArraySetterPrimitive(setter, () -> setter.getInArray(), () -> setter.getInValue(),
				() -> setter.getOutArray());
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<Double[]> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}
}
