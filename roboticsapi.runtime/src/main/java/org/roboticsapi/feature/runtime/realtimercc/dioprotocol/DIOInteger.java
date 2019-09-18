/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dioprotocol;

public class DIOInteger extends DIOLiteral {
	private final int content;

	public DIOInteger(int value) {
		content = value;
	}

	public DIOInteger(String strvalue) {
		content = Integer.parseInt(strvalue);
	}

	@Override
	public String toString() {
		return Integer.toString(content);
	}
}
