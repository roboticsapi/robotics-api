/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

public abstract class TargetedEventHandler extends EventEffect {

	private Command target;

	protected TargetedEventHandler() {
		this(null);
	}

	protected TargetedEventHandler(Command context) {
		this(context, null);
	}

	protected TargetedEventHandler(Command context, Command target) {
		super(context);
		this.setTarget(target);
	}

	public void setTarget(Command target) {
		this.target = target;

		validate();
	}

	public Command getTarget() {
		return target != null ? target : getContext();
	}

	@Override
	protected void validate() {
		super.validate();

		if (getContext() == null) {
			return;
		}

		if (!(getContext() instanceof TransactionCommand) && getContext() != getTarget()) {
			throw new IllegalArgumentException(
					"Command context and target must be the same if CommandCanceller is used in context of a RuntimeCommand. Use a TransactionCommand to cancel Commands other than the CommandCanceller's context.");
		}
	}

}
