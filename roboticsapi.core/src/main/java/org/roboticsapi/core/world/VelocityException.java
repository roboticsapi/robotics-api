/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.exception.RoboticsException;

public class VelocityException extends RoboticsException {
	private static final long serialVersionUID = 1L;
	private Frame from;
	private Frame to;

	public VelocityException(String message) {
		super(message);
	}

	public VelocityException(String message, Exception innerException) {
		super(message, innerException);
	}

	public VelocityException(Exception innerException) {
		super(innerException);
	}

	public VelocityException(String message, Frame from, Frame to) {
		super(message + "<" + from.getName() + "," + to.getName() + ">");
		this.setFrom(from);
		this.setTo(to);
	}

	public void setFrom(Frame from) {
		this.from = from;
	}

	public Frame getFrom() {
		return from;
	}

	public void setTo(Frame to) {
		this.to = to;
	}

	public Frame getTo() {
		return to;
	}
}
