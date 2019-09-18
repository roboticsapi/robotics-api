/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.RoboticsRuntime;

public class FlipFlopRealtimeBoolean extends RealtimeBoolean {

	private final RealtimeBoolean activating;
	private final RealtimeBoolean deactivating;
	private final boolean oneShot;

	FlipFlopRealtimeBoolean(RealtimeBoolean activating, RealtimeBoolean deactivating) {
		this(activating, deactivating, true);
	}

	FlipFlopRealtimeBoolean(RealtimeBoolean activating, RealtimeBoolean deactivating, RoboticsRuntime runtime) {
		this(activating, deactivating, true, runtime);
	}

	FlipFlopRealtimeBoolean(RealtimeBoolean activating, RealtimeBoolean deactivating, boolean oneShot) {
		super(activating, deactivating);

		// TODO: should we enforce having a RoboticsRuntime? If not, getCurrentValue()
		// will throw an exception
		// if (activating.getRuntime() == null && deactivating.getRuntime() == null) {
		// throw new IllegalArgumentException("Either activating or deactivating must
		// have a RoboticsRuntime");
		// }
		this.activating = activating;
		this.deactivating = deactivating;
		this.oneShot = oneShot;
	}

	FlipFlopRealtimeBoolean(RealtimeBoolean activating, RealtimeBoolean deactivating, boolean oneShot,
			RoboticsRuntime runtime) {
		super(activating, deactivating);
		if (runtime == null) {
			throw new IllegalArgumentException("runtime may not be null");
		}
		setRuntime(runtime);
		this.activating = activating;
		this.deactivating = deactivating;
		this.oneShot = oneShot;
	}

	public boolean isOneShot() {
		return oneShot;
	}

	public RealtimeBoolean getDeactivatingValue() {
		return deactivating;
	}

	public RealtimeBoolean getActivatingValue() {
		return activating;
	}

	@Override
	public boolean isAvailable() {
		return activating.isAvailable() && deactivating.isAvailable();
	}

	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj) && oneShot == ((FlipFlopRealtimeBoolean) obj).oneShot;
	}

}
