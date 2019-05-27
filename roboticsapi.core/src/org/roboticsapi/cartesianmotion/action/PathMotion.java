/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;

/**
 * Abstract class for a motion with defined Cartesian path
 */
public abstract class PathMotion<P extends CartesianMotionPlan> extends PlannedAction<P>
		implements CartesianPositionAction {

	public PathMotion(double watchdogTimeout) {
		super(watchdogTimeout);
	}

	protected Frame from, to;

	public Frame getFrom() {
		return from;
	}

	public Frame getTo() {
		return to;
	}

	public ActionState getMotionTimeProgress(Frame f, P plan) {
		Transformation transformationTo;
		try {
			transformationTo = plan.getBaseFrame().getTransformationTo(f);
		} catch (TransformationException e) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL, "Action " + getClass().getSimpleName()
					+ " can not determine State for reaching Frame " + f.getName());
			return null;
		}

		Double timeForTransformation = plan.getTimeForTransformation(transformationTo);

		if (timeForTransformation == null) {
			return null;
		}

		return getMotionTimeProgress((float) (timeForTransformation / plan.getTotalTime()));
	}

}
