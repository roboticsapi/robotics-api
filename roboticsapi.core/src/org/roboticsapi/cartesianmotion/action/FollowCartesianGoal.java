/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.sensor.TransformationSensor;

public class FollowCartesianGoal extends Action implements GoalAction, CartesianPositionAction {

	private final Frame goal;

	public FollowCartesianGoal(Frame reference, TransformationSensor sensor, double startX, double startY,
			double startZ, double startA, double startB, double startC) throws InitializationException {
		super(0);

		goal = new Frame("FollowCartesianGoal goal frame");

		reference.addRelation(new TransformationSensorConnection(sensor, null), goal);
	}

	public FollowCartesianGoal(Frame reference, TransformationSensor sensor, Transformation startTransformation)
			throws InitializationException {
		this(reference, sensor, startTransformation.getTranslation().getX(),
				startTransformation.getTranslation().getY(), startTransformation.getTranslation().getZ(),
				startTransformation.getRotation().getA(), startTransformation.getRotation().getB(),
				startTransformation.getRotation().getC());

	}

	public FollowCartesianGoal(Frame goal) {
		super(0);
		this.goal = goal;
	}

	public Frame getGoal() {
		return goal;
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions() {
		List<ActionRealtimeException> exceptions = super.defineActionExceptions();
		exceptions.add(new ActionCancelledException(this));

		return exceptions;
	}

}
