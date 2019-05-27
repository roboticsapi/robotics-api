/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RAPILogger {
	private static Logger logger;

	public static final String LOGGERNAME = "org.roboticsapi.RAPILogger";
	public static final Level FINEDEBUGLEVEL = Level.FINEST;
	public static final Level DEBUGLEVEL = Level.FINE;
	public static final Level WARNINGLEVEL = Level.WARNING;
	public static final Level ERRORLEVEL = Level.SEVERE;
	public static final Level INFOLEVEL = Level.INFO;

	static {
		logger = Logger.getLogger(LOGGERNAME);
		logger.setLevel(DEBUGLEVEL);
	}

	public static Logger getLogger() {
		return logger;
	}

}
