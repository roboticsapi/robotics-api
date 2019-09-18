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
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformationArray;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeArrayFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.world.netcomm.ReadFrameArrayFromNet;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArray;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArraySet;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameSetNull;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrameArray;

public class RealtimeTransformationArrayFragment extends RealtimeArrayFragment<Transformation[]> {

	private final RealtimeTransformationArray realtimeValue;

	public RealtimeTransformationArrayFragment(RealtimeTransformationArray value, OutPort result, OutPort time,
			Primitive... children) {
		super(value, result, time, children);
		realtimeValue = value;
	}

	public RealtimeTransformationArrayFragment(RealtimeTransformationArray value, OutPort result,
			Primitive... children) {
		super(value, result, children);
		realtimeValue = value;
	}

	public RealtimeTransformationArrayFragment(RealtimeTransformationArray value) {
		super(value);
		realtimeValue = value;
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Transformation[]> observer) throws MappingException {
		ReadFrameArrayFromNet netcomm = new ReadFrameArrayFromNet("r" + (nr++));
		RealtimeValueConsumerFragment ret = new ObserverFragment(netcomm);
		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPIFrameArray rpi = new RPIFrameArray(value.getString());
				Transformation[] ret = new Transformation[realtimeValue.getSize()];
				for (int i = 0; i < ret.length; i++) {
					RPIFrame f = rpi.get(i);
					ret[i] = new Transformation(f.getPos().getX().get(), f.getPos().getY().get(),
							f.getPos().getZ().get(), f.getRot().getA().get(), f.getRot().getB().get(),
							f.getRot().getC().get());
				}
				observer.onValueChanged(ret);
			}

			@Override
			public void updatePerformed() {
			}
		});

		if (condition != null) {
			FrameSetNull setNull = ret.add(new FrameSetNull());
			ret.connect(setNull.getOutValue(), netcomm.getInValue());
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", setNull.getInValue()));
			ret.addDependency(condition.not(), ret.addInPort("inCondition", setNull.getInNull()));
		} else {
			ret.addDependency(getRealtimeValue(), ret.addInPort("inValue", netcomm.getInValue()));
		}
		return ret;
	}

	@Override
	protected RealtimeArrayFragment<Transformation[]>.ArrayCreatorPrimitive getArrayCreatorPrimitive(int size) {
		FrameArray frameArray = new FrameArray();
		return new ArrayCreatorPrimitive(frameArray, () -> frameArray.getOutArray());
	}

	@Override
	protected RealtimeArrayFragment<Transformation[]>.ArraySetterPrimitive getArraySetterPrimitive(int size,
			int index) {
		FrameArraySet setter = new FrameArraySet(size, index);
		return new ArraySetterPrimitive(setter, () -> setter.getInArray(), () -> setter.getInValue(),
				() -> setter.getOutArray());
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<Transformation[]> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
