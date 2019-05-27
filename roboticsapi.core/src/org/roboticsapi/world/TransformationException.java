/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.exception.RoboticsException;

public class TransformationException extends RoboticsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Frame from;
	private Frame to;

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Exception innerException) {
		super(message, innerException);
	}

	public TransformationException(Exception innerException) {
		super(innerException);
	}

	public TransformationException(String message, Frame from, Frame to) {
		super(message + "<" + from.getName() + "," + to.getName() + ">");
		this.setFrom(from);
		this.setTo(to);
	}

	public TransformationException(String message, Frame from, Frame to, Exception innerException) {
		super(message + "<" + from.getName() + "," + to.getName() + ">", innerException);
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
