/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ExceptionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class ExceptionStateMapper extends TypedRealtimeValueAliasFactory<Boolean, ExceptionRealtimeBoolean> {

	public ExceptionStateMapper() {
		super(ExceptionRealtimeBoolean.class);
	}

	@Override
	protected RealtimeValue<Boolean> createAlias(ExceptionRealtimeBoolean value) {
		RealtimeBoolean condition = value.getScope().getExceptionCondition(value.getException());
		if (condition != null)
			// exceptions with conditions are caused by the condition
			return condition;
		else if (value.getScope().getInnerExceptions().contains(value.getException()))
			// inner exceptions are handled separate
			return null;
		else
			// Exceptions that have no condition don't occur
			return RealtimeBoolean.FALSE;
	}

}
