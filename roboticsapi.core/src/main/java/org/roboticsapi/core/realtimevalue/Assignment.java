/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue;

import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class Assignment<T> {

	private final RealtimeValue<T> source;
	private final PersistContext<T> target;
	private final RealtimeBoolean condition;

	public Assignment(RealtimeValue<T> source, PersistContext<T> target) {
		this.source = source;
		this.target = target;
		this.condition = RealtimeBoolean.TRUE;
	}

	public Assignment(RealtimeValue<T> source, PersistContext<T> target, RealtimeBoolean condition) {
		this.source = source;
		this.target = target;
		this.condition = condition;
	}

	public RealtimeValue<T> getSource() {
		return source;
	}

	public PersistContext<T> getTarget() {
		return target;
	}

	public RealtimeBoolean getCondition() {
		return condition;
	}

}
