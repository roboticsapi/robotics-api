/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

public final class NegatedRealtimeBoolean extends RealtimeBoolean {

	private final RealtimeBoolean other;

	NegatedRealtimeBoolean(RealtimeBoolean other) {
		super(other);
		this.other = other;
	}

	public RealtimeBoolean getOther() {
		return other;
	}

	@Override
	public RealtimeBoolean not() {
		return other;
	}

	@Override
	protected Boolean calculateCheapValue() {
		Boolean cheapValue = other.getCheapValue();
		if (cheapValue == null) {
			return null;
		}
		return !cheapValue;
	}

	@Override
	public boolean isAvailable() {
		return other.isAvailable();
	}

	@Override
	public String toString() {
		return "!(" + other + ")";
	}
}
