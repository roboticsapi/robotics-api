/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.Action;
import org.roboticsapi.world.Frame;

public class HybridMoveAction extends Action {

	private final Frame base;
	private final Action xAction;
	private final Action yAction;
	private final Action zAction;
	private final Action aAction;
	private final Action bAction;
	private final Action cAction;

	public HybridMoveAction(Action xAction, Action yAction, Action zAction, Action aAction, Action bAction,
			Action cAction, Frame base) {
		super(getMinWatchdogTimeout(xAction, yAction, zAction, aAction, bAction, cAction));
		checkNotNull(xAction, "xAction");
		checkNotNull(yAction, "yAction");
		checkNotNull(zAction, "zAction");
		checkNotNull(aAction, "aAction");
		checkNotNull(bAction, "bAction");
		checkNotNull(cAction, "cAction");
		checkNotNull(base, "base");
		this.xAction = xAction;
		this.yAction = yAction;
		this.zAction = zAction;
		this.aAction = aAction;
		this.bAction = bAction;
		this.cAction = cAction;
		this.base = base;
	}

	private void checkNotNull(Object arg, String name) {

		if (arg == null) {
			throw new IllegalArgumentException("Argument " + name + " must not be null");
		}

	}

	private static double getMinWatchdogTimeout(Action... actions) {
		double min = -1;
		for (Action a : actions) {
			if (a == null) {
				continue;
			}
			if (min == -1) {
				min = a.getWatchdogTimeout();
			} else {
				min = Math.min(min, a.getWatchdogTimeout());
			}
		}
		return Math.max(min, 0);
	}

	public Action getxAction() {
		return xAction;
	}

	public Action getyAction() {
		return yAction;
	}

	public Action getzAction() {
		return zAction;
	}

	public Action getaAction() {
		return aAction;
	}

	public Action getbAction() {
		return bAction;
	}

	public Action getcAction() {
		return cAction;
	}

	public Frame getBase() {
		return base;
	}

}
