/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.ArrayList;

public abstract class ArrayType<T extends Type> extends Type {
	private final ArrayList<T> data;

	public ArrayType(int capacity) {
		data = new ArrayList<T>(capacity);
		for (int i = 0; i < capacity; i++) {
			data.add(getInitialValue());
		}
	}

	public int getSize() {
		return data.size();
	}

	public ArrayType(String value) {
		data = new ArrayList<T>();
		if (!value.startsWith("[")) {
			throw new IllegalArgumentException("value");
		}
		value = value.substring(1);
		while (true) {
			if (value.startsWith("]")) {
				break;
			}
			T element = getInitialValue();
			data.add(element);
			value = element.consumeString(value);
			if (value.startsWith(",")) {
				value = value.substring(1);
			} else if (!value.startsWith("]")) {
				throw new IllegalArgumentException("value");
			}
		}
		if (!value.startsWith("]")) {
			throw new IllegalArgumentException("value");
		}
	}

	protected abstract T getInitialValue();

	public T get(int index) {
		return data.get(index);
	}

	public void set(int index, T value) {
		data.set(index, value);
	}

	@Override
	public void appendString(StringBuilder ret) {
		ret.append("[");
		for (T value : data) {
			ret.append(value).append(",");
		}
		if (ret.length() > 1) {
			ret.deleteCharAt(ret.length() - 1);
		}
		ret.append("]");
	}

	@Override
	public String consumeString(String value) {
		if (!value.startsWith("[")) {
			throw new IllegalArgumentException("value");
		}
		value = value.substring(1);
		for (int i = 0; i < data.size(); i++) {
			value = data.get(i).consumeString(value);
			if (value.startsWith("]")) {
				break;
			} else if (value.startsWith(",")) {
				value = value.substring(1);
			} else {
				throw new IllegalArgumentException("value");
			}
		}
		if (!value.startsWith("]")) {
			throw new IllegalArgumentException("value");
		}
		return value.substring(1);
	}
}
