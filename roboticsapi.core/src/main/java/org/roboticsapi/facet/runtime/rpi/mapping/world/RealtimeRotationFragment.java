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
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.world.netcomm.ReadRotationFromNet;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationSetNull;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;

public class RealtimeRotationFragment extends RealtimeValueFragment<Rotation> {

	public RealtimeRotationFragment(RealtimeValue<Rotation> value, OutPort result, OutPort time,
			Primitive... children) {
		super(value, result, time, children);
	}

	public RealtimeRotationFragment(RealtimeValue<Rotation> value, OutPort result, Primitive... children) {
		super(value, result, children);
	}

	public RealtimeRotationFragment(RealtimeValue<Rotation> value) {
		super(value);
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Rotation> observer) throws MappingException {
		ReadRotationFromNet netcomm = new ReadRotationFromNet("r" + (nr++));
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPIRotation rpi = new RPIRotation(value.getString());
				observer.onValueChanged(new Rotation(rpi.getA().get(), rpi.getB().get(), rpi.getC().get()));
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			RotationSetNull setNull = ret.add(new RotationSetNull());
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
	public RealtimeValue<Rotation> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
