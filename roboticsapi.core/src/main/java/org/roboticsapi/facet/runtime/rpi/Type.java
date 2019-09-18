/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

public abstract class Type implements Cloneable {
	public abstract void appendString(StringBuilder ret);

	public abstract String consumeString(String value);

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		appendString(ret);
		return ret.toString();
	}

	@Override
	public Type clone() {
		try {
			return (Type) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
