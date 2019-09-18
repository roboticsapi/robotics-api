/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import java.util.logging.Level;

import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.util.RAPILogger;

/**
 * An event handler throwing an exception
 */
public class JavaExceptionThrower extends EventEffect {
	public JavaExceptionThrower(final CommandRuntimeException exception) {
		super(new Runnable() {
			@Override
			public void run() {
				try {
					exception.getCommand().getUnhandledExceptionHandler().handleException(exception);
				} catch (Exception exc) {
					RAPILogger.getLogger(this).log(Level.SEVERE,
							"Unhandled Exception when handling Exceptions in Command "
									+ exception.getCommand().getName(),
							exc);
				}
			}
		});
	}
}
