/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.RealtimeTuple;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.ReadBoolFromNet;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanValue;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;

public class RealtimeTupleFragment extends RealtimeValueFragment<Object[]> {
	private RealtimeValueFragment<?>[] fragments;
	private RealtimeTuple value;

	public RealtimeTupleFragment(RealtimeTuple value, RealtimeValueFragment<?>... fragments) {
		super(value);
		this.value = value;
		this.fragments = fragments;
	}

	public OutPort getValuePort(int index) {
		return fragments[index].getValuePort();
	}

	public OutPort getTimePort(int index) {
		return fragments[index].getTimePort();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Object[]> observer) throws MappingException {
		RealtimeValueConsumerFragment ret = new ObserverFragment();
		final Object[] values = new Object[fragments.length + 1];
		for (int i = 0; i < fragments.length; i++) {
			final int index = i;
			Observer obs = value.getValues()[i].createObserver(new RealtimeValueListener() {
				@Override
				public void onValueChanged(Object newValue) {
					values[0] = true;
					values[index + 1] = newValue;
				}
			}, condition, false);
			ret.addWithDependencies(fragments[index].createObserverFragment(obs.getCondition(), obs.getListener()));
		}
		ReadBoolFromNet tuple = ret.add(new ReadBoolFromNet("tuple"));
		BooleanValue constantBoolean = ret.add(new BooleanValue(false));
		ret.connect(constantBoolean.getOutValue(), tuple.getInValue());
		tuple.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
			}

			@Override
			public void updatePerformed() {
				if (values[0] != null) {
					Object[] notify = new Object[fragments.length];
					for (int i = 0; i < fragments.length; i++) {
						notify[i] = values[i + 1];
					}
					observer.onValueChanged(notify);
					values[0] = null;
				}
			}
		});

		return ret;
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<Object[]> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
