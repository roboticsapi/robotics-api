/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;

/**
 * Abstract class for a motion with defined Cartesian path
 */
public abstract class PathMotion<P extends CartesianMotionPlan> extends PlannedAction<P>
		implements CartesianPositionAction {

	public PathMotion(double watchdogTimeout) {
		super(watchdogTimeout, true, true);
	}

	protected Pose from, to;

	public Pose getFrom() {
		return from;
	}

	public Pose getTo() {
		return to;
	}

	public ActionRealtimeBoolean getMotionTimeProgress(Command command, Frame f, CartesianMotionPlan plan) {
		Transformation transformationTo;
		try {
			transformationTo = plan.getBaseFrame().getTransformationTo(f);
		} catch (TransformationException e) {
			RAPILogger.getLogger(this).warning("Action " + getClass().getSimpleName()
					+ " can not determine State for reaching Frame " + f.getName());
			return null;
		}

		Double timeForTransformation = plan.getTimeForTransformation(transformationTo);

		if (timeForTransformation == null) {
			return null;
		}

		return getMotionTimeProgress(command, (float) (timeForTransformation / plan.getTotalTime()));
	}

}
