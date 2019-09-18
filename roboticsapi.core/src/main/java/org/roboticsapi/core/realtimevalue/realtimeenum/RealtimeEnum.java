/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeenum;

import org.roboticsapi.core.realtimevalue.RealtimeDerivedValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;

public abstract class RealtimeEnum<T extends Enum<?>> extends RealtimeDerivedValue<T, Integer> {

	public RealtimeEnum(RealtimeInteger realtimeInteger, Class<T> enumClass) {
		super(realtimeInteger, i -> enumClass.getEnumConstants()[i]);
	}

	public RealtimeBoolean isEqual(T other) {
		return ((RealtimeInteger) getRealtimeInteger()).isEqual(RealtimeInteger.createFromConstant(other.ordinal()));
	}

	public RealtimeBoolean isEqual(RealtimeEnum<T> other) {
		return ((RealtimeInteger) getRealtimeInteger()).isEqual((RealtimeInteger) other.getRealtimeInteger());
	}
}
