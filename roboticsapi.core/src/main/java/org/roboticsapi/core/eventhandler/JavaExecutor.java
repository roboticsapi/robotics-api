/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.EventEffect;

public class JavaExecutor extends EventEffect {

	public JavaExecutor(Runnable runnable, boolean async) {
		super(runnable, async);
	}

	public JavaExecutor(Runnable thread) {
		super(thread);
	}

}
