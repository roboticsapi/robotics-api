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
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.ReadIntFromNet;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntSetNull;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;

public class RealtimeIntegerFragment extends RealtimeValueFragment<Integer> {

	public RealtimeIntegerFragment(RealtimeValue<Integer> value, OutPort result, OutPort time, Primitive... children) {
		super(value, result, time, children);
	}

	public RealtimeIntegerFragment(RealtimeValue<Integer> value, OutPort result, Primitive... children) {
		super(value, result, children);
	}

	public RealtimeIntegerFragment(RealtimeValue<Integer> value) {
		super(value);
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Integer> observer) throws MappingException {
		ReadIntFromNet netcomm = new ReadIntFromNet("i" + (nr++));
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				observer.onValueChanged(new RPIint(value.getString()).get());
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			IntSetNull setNull = ret.add(new IntSetNull());
			ret.connect(setNull.getOutValue(), netcomm.getInValue());
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", setNull.getInValue()));
			ret.addDependency(condition.not(), ret.addInPort("inCondition", setNull.getInNull()));
		} else {
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", netcomm.getInValue()));
		}
		return ret;
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<Integer> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
