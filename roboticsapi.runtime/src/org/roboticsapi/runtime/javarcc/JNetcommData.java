/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.javarcc;

import org.roboticsapi.runtime.rpi.types.Type;

public class JNetcommData<T extends Type> {
	private final T prototype;
	private T data = null;
	private Long lastUpdated = -1L;
	private boolean killed = false;

	public JNetcommData(T prototype) {
		this.prototype = prototype;
	}

	public void update(T data, Long lastUpdated) {
		this.data = data;
		this.lastUpdated = lastUpdated;
	}

	@SuppressWarnings("unchecked")
	public void update(String data, Long lastUpdated) {
		T next = (T) this.prototype.clone();
		next.consumeString(data);
		this.data = next;
		this.lastUpdated = lastUpdated;
	}

	public T getData() {
		return data;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void markKilled() {
		killed = true;
	}

	public boolean isKilled() {
		return killed;
	}
}
