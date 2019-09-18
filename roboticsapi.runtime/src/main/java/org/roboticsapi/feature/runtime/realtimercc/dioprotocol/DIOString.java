/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dioprotocol;

public class DIOString extends DIOLiteral {
	private final String content;

	public DIOString(String str) {
		this(str, false);
	}

	public DIOString(String str, boolean unescape) {
		if (unescape) {
			content = unescape(str);
		} else {
			content = str;
		}
	}

	public String getString() {
		return content;
	}

	public static String unescape(String str) {
		if (str.length() <= 2) {
			return str;
		}

		String res = str.substring(1, str.length() - 1);

		return res.replace("\\\"", "\"").replace("\\\\", "\\");
	}

	@Override
	public String toString() {
		return "\"" + content.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}
}
