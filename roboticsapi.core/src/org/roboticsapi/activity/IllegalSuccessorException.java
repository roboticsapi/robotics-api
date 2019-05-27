/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

public class IllegalSuccessorException extends ActivitySchedulingException {
	/**
	 *
	 */
	private static final long serialVersionUID = -4225202857116569993L;
	private final Activity current;
	private final Activity successor;

	public IllegalSuccessorException(String message, Activity current, Activity successor) {
		super(message);
		this.current = current;
		this.successor = successor;

	}

	public IllegalSuccessorException(Activity current, Activity successor) {
		this("Activity " + current.getName() + " can not be taken over by " + successor.getName(), current, successor);

	}

	public Activity getCurrent() {
		return current;
	}

	public Activity getSuccessor() {
		return successor;
	}

}
