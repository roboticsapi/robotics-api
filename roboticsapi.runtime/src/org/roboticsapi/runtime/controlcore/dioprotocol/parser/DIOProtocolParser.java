/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol.parser;

import java.io.ByteArrayInputStream;

import org.roboticsapi.runtime.controlcore.dioprotocol.DIOStatement;

public class DIOProtocolParser {
	public static DIOStatement parse(String text) {
		Parser parser = new Parser(new Scanner(new ByteArrayInputStream(text.getBytes())));

		parser.Parse();

		return parser.diostatement;
	}
}
