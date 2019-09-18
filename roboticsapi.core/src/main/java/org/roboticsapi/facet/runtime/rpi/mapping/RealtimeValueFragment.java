/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.FragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

public abstract class RealtimeValueFragment<T> extends DependentFragment {
	private FragmentOutPort valuePort;
	private FragmentOutPort timePort;
	private RealtimeValue<T> value;
	private List<RealtimeValueFragmentFactory> realtimeValueProducers = new ArrayList<RealtimeValueFragmentFactory>();

	public RealtimeValueFragment(RealtimeValue<T> value) {
		this.value = value;
	}

	public RealtimeValueFragment(RealtimeValue<T> value, OutPort result, Primitive... children) {
		this(value);
		add(result.getPrimitive());
		for (Primitive prim : children)
			add(prim);
		defineResult(result);
	}

	public RealtimeValueFragment(RealtimeValue<T> value, OutPort result, OutPort time, Primitive... children) {
		this(value, result, children);
		add(time.getPrimitive());
		defineTime(time);
	}

	public void defineResult(OutPort port) {
		valuePort = provideOutPort(port, "outValue");
	}

	public void defineTime(OutPort port) {
		timePort = provideOutPort(port, "outTime");
	}

	public OutPort getValuePort() {
		return valuePort;
	}

	public OutPort getTimePort() {
		return timePort;
	}

	public void addRealtimeValueProducer(RealtimeValueFragmentFactory producer) {
		realtimeValueProducers.add(producer);
	}

	public List<RealtimeValueFragmentFactory> getRealtimeValueProducers() {
		return realtimeValueProducers;
	}

	public RealtimeValue<T> getRealtimeValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getRealtimeValue() + "]";
	}

	public abstract RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			RealtimeValueListener<T> observer) throws MappingException;

	public abstract InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition);

	public abstract RealtimeValue<T> createInterNetcommValue(Command command, String key);
}
