/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.facet.runtime.rpi.ActiveFragment;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.world.netcomm.ReadTwistFromNet;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistNetcommOut;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistSetNull;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

public class RealtimeTwistFragment extends RealtimeValueFragment<Twist> {

	public RealtimeTwistFragment(RealtimeValue<Twist> value, OutPort result, OutPort time, Primitive... children) {
		super(value, result, time, children);
	}

	public RealtimeTwistFragment(RealtimeValue<Twist> value, OutPort result, Primitive... children) {
		super(value, result, children);
	}

	public RealtimeTwistFragment(RealtimeValue<Twist> value) {
		super(value);
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Twist> observer) throws MappingException {
		ReadTwistFromNet netcomm = new ReadTwistFromNet("t" + (nr++));
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPITwist rpi = new RPITwist(value.getString());
				observer.onValueChanged(
						new Twist(rpi.getVel().getX().get(), rpi.getVel().getY().get(), rpi.getVel().getZ().get(),
								rpi.getRot().getX().get(), rpi.getRot().getY().get(), rpi.getRot().getZ().get()));
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			TwistSetNull setNull = ret.add(new TwistSetNull());
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
		ActiveFragment active = new ActiveFragment();
		String key = "itw" + (nr++);
		TwistNetcommOut netcomm = active.add(new TwistNetcommOut(key, false));
		FragmentInPort inPort = active.addInPort("inValue", netcomm.getInValue());
		InterNetcommFragment ret = new InterNetcommFragment(key);
		ret.add(active);
		ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", inPort));
		ret.addDependency(condition, ret.addInPort("inValue", active.getInActive()));
		return ret;
	}

	@Override
	public RealtimeValue<Twist> createInterNetcommValue(Command command, String key) {
		return new PersistedRealtimeTwist(command, key);
	}

}
