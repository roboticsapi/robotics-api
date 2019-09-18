/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.OnlineObject.OperationStateListener;
import org.roboticsapi.core.RealtimeValueListener;

public class ConsoleWriter<T> extends EventEffect implements RealtimeValueListener<T>, OperationStateListener {

	private final String message;

	// Constructor 1
	public ConsoleWriter() {
		super(new Runnable() {

			@Override
			public void run() {
				System.out.println("ConsoleWriter");
			}
		});
		this.message = "";
	}

	// Constructor 2
	public ConsoleWriter(final String message) {
		super(new Runnable() {

			@Override
			public void run() {
				System.out.println(message);
			}
		});
		this.message = message;
	}

	@Override
	public void onValueChanged(T newValue) {
		System.out.println(("".equals(message) ? "" : (message + ": ")) + newValue);

	}

	@Override
	public void operationStateChanged(OnlineObject object, OperationState newState) {

		System.out.println(
				("".equals(message) ? "" : (message + ": ")) + object.getName() + " now in OperationState " + newState);

	}

}
