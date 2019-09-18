/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dioprotocol;

public class DIOFloat extends DIOLiteral {
	private final double content;

	public DIOFloat(double value) {
		content = value;
	}

	public DIOFloat(String strvalue) {
		content = Double.parseDouble(strvalue);
	}

	@Override
	public String toString() {
		return Double.toString(content);
	}
}
