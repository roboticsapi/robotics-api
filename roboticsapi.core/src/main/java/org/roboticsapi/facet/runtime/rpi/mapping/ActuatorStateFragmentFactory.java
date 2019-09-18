/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class ActuatorStateFragmentFactory<V extends ActuatorRealtimeBoolean>
		extends TypedRealtimeValueFragmentFactory<Boolean, V> {
	private Actuator actuator;
	private OutPort port;

	protected ActuatorStateFragmentFactory(Class<V> type, Actuator actuator, OutPort port) {
		super(type);
		this.actuator = actuator;
		this.port = port;
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(V value) throws MappingException, RpiException {
		if (value.getActuator() == actuator) {
			RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value);
			FragmentInPort inValue = ret.addInPort("inValue");
			ret.addDependency(inValue, port);
			ret.defineResult(inValue.getInternalOutPort());
			return ret;
		}
		return null;
	}

}
