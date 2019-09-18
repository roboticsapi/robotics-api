/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dioprotocol.parser;

import java.io.ByteArrayInputStream;

import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOStatement;

public class DIOProtocolParser {
	public static DIOStatement parse(String text) {
		Parser parser = new Parser(new Scanner(new ByteArrayInputStream(text.getBytes())));

		parser.Parse();

		return parser.diostatement;
	}
}
