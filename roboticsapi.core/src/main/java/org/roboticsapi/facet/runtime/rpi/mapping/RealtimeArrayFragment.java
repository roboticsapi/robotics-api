/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.List;
import java.util.function.Supplier;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

public abstract class RealtimeArrayFragment<T> extends RealtimeValueFragment<T> {

	protected class ArrayCreatorPrimitive {
		private Primitive p;
		private Supplier<OutPort> outArraySupplier;

		public ArrayCreatorPrimitive(Primitive p, Supplier<OutPort> outArraySupplier) {
			this.p = p;
			this.outArraySupplier = outArraySupplier;

		}

		public Primitive getPrimitive() {
			return p;
		}

		public OutPort getOutArray() {
			return outArraySupplier.get();
		}
	}

	protected class ArraySetterPrimitive {
		private Primitive p;
		private Supplier<InPort> inArraySupplier;
		private Supplier<InPort> inValueSupplier;
		private Supplier<OutPort> outArraySupplier;

		public ArraySetterPrimitive(Primitive p, Supplier<InPort> inArraySupplier, Supplier<InPort> inValueSupplier,
				Supplier<OutPort> outArraySupplier) {
			this.p = p;
			this.inArraySupplier = inArraySupplier;
			this.inValueSupplier = inValueSupplier;
			this.outArraySupplier = outArraySupplier;
		}

		public Primitive getPrimitive() {
			return p;
		}

		public InPort getInArray() {
			return inArraySupplier.get();
		}

		public InPort getInValue() {
			return inValueSupplier.get();
		}

		public OutPort getOutArray() {
			return outArraySupplier.get();
		}
	}

	public RealtimeArrayFragment(RealtimeValue<T> value) {
		super(value);
	}

	public RealtimeArrayFragment(RealtimeValue<T> value, OutPort result, Primitive... children) {
		super(value, result, children);
	}

	public RealtimeArrayFragment(RealtimeValue<T> value, OutPort result, OutPort time, Primitive... children) {
		super(value, result, time, children);
	}

	public void defineResult(List<OutPort> outPorts) throws MappingException {
		// we need to merge the separate Outports into an RPI array
		RealtimeArrayFragment<T>.ArrayCreatorPrimitive creator = getArrayCreatorPrimitive(outPorts.size());
		add(creator.getPrimitive());

		OutPort current = creator.getOutArray();
		// set each value in the array separately
		for (int i = 0; i < outPorts.size(); ++i) {
			RealtimeArrayFragment<T>.ArraySetterPrimitive setter = getArraySetterPrimitive(outPorts.size(), i);

			// supply the array to the setter
			connect(current, setter.getInArray());

			// supply the value to set at the requested index to the setter
			connect(outPorts.get(i), setter.getInValue());

			// the modified array is the input for the next setter
			current = setter.getOutArray();
		}
	}

	protected abstract ArrayCreatorPrimitive getArrayCreatorPrimitive(int size);

	protected abstract ArraySetterPrimitive getArraySetterPrimitive(int size, int index);

}
