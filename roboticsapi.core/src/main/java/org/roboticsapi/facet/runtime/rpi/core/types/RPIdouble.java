/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.core.types;

import java.util.regex.Pattern;

import org.roboticsapi.facet.runtime.rpi.PrimitiveType;

public class RPIdouble extends PrimitiveType {
	static Pattern doublePattern = Pattern.compile("\\s*(nan|\\+inf|\\-inf|[\\-\\+0-9\\.eE]+)\\s*(.*)");

	private double value;

	public RPIdouble(double value) {
		super(doublePattern);
		this.value = value;
	}

	public RPIdouble() {
		this(0);
	}

	public RPIdouble(String value) {
		this();
		consumeString(value);
	}

	public double get() {
		return value;
	}

	public void set(double value) {
		this.value = value;
	}

	@Override
	public void appendString(StringBuilder ret) {
		if (Double.isNaN(value)) {
			ret.append("nan");
		} else if (Double.isInfinite(value) && value > 0) {
			ret.append("+inf");
		} else if (Double.isInfinite(value) && value < 0) {
			ret.append("-inf");
		} else {
			ret.append(value);
		}
	}

	@Override
	protected void consumeValue(String string) {
		if ("nan".equals(string)) {
			this.value = Double.NaN;
		} else if ("+inf".equals(string)) {
			this.value = Double.POSITIVE_INFINITY;
		} else if ("-inf".equals(string)) {
			this.value = Double.NEGATIVE_INFINITY;
		} else {
			this.value = java.lang.Double.valueOf(string);
		}
	}

}
