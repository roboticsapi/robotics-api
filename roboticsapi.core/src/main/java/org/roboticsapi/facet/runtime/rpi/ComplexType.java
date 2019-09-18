/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

public abstract class ComplexType extends Type {

	protected final void appendComponent(StringBuilder buf, String name, Type value) {
		buf.append(name + ":" + value);
	}

	protected abstract void appendComponents(StringBuilder buf);

	protected abstract String consumeComponent(String key, String value);

	@Override
	public void appendString(StringBuilder ret) {
		ret.append("{");
		appendComponents(ret);
		ret.append("}");
	}

	@Override
	public String consumeString(String value) {
		if (!value.startsWith("{")) {
			throw new IllegalArgumentException("Illegal value " + value);
		}
		value = value.substring(1);
		while (!value.isEmpty()) {
			int pos = value.indexOf(":");
			String key = value.substring(0, pos);
			value = value.substring(pos + 1);
			value = consumeComponent(key, value);
			if (value.startsWith(",")) {
				value = value.substring(1);
			} else if (value.startsWith("}")) {
				return value.substring(1);
			} else {
				throw new IllegalArgumentException("Illegal value " + value);
			}
		}
		throw new IllegalArgumentException("Illegal value " + value);
	}

}
