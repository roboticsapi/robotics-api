/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

public class ActuatorFragment extends RealtimeValueConsumerFragment {

	private ActuatorDriver actuator;

	public ActuatorFragment(ActuatorDriver actuator, RealtimeBoolean completion) {
		this.actuator = actuator;
		addCompletion(completion);
	}

	public ActuatorFragment(ActuatorDriver actuator, OutPort completion) {
		super(completion.getPrimitive());
		this.actuator = actuator;
		addCompletion(completion);
	}

	public ActuatorFragment(ActuatorDriver actuator, RealtimeBoolean completion, Primitive... children) {
		super(children);
		this.actuator = actuator;
		addCompletion(completion);
	}

	public ActuatorFragment(ActuatorDriver actuator, OutPort completion, Primitive... children) {
		super(children);
		this.actuator = actuator;
		addCompletion(completion);
	}

	private void addCompletion(OutPort completion) {
		addRealtimeValueFragmentFactory(new ActuatorStateFragmentFactory<Actuator.CompletedRealtimeBoolean>(
				Actuator.CompletedRealtimeBoolean.class, actuator.getDevice(), completion));
	}

	private void addCompletion(RealtimeBoolean completion) {
		addRealtimeValueAliasFactory(new ActuatorStateAliasFactory<Actuator.CompletedRealtimeBoolean>(
				Actuator.CompletedRealtimeBoolean.class, actuator.getDevice(), completion));
	}

	public void addException(Class<? extends ActuatorDriverRealtimeException> type, RealtimeBoolean condition) {
		addRealtimeValueAliasFactory(new TypedActuatorExceptionMapper<>(type, actuator, condition));
	}

	public void addException(Class<? extends ActuatorDriverRealtimeException> type, OutPort condition, String name) {
		addRealtimeValueFragmentFactory(
				new TypedActuatorExceptionFragmentFactory<>(type, actuator, provideOutPort(condition, name)));
	}

}
