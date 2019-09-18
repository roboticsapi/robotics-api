/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PrimitiveType extends Type {

	public PrimitiveType(Pattern pattern) {
		this.pattern = pattern;
	}

	private final Pattern pattern;

	@Override
	public String consumeString(String value) {
		Matcher match = pattern.matcher(value);
		if (match.matches()) {
			consumeValue(match.group(1));
			return match.group(2);
		}
		throw new IllegalArgumentException("value");
	}

	protected abstract void consumeValue(String string);

}
