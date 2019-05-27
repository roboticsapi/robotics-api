/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import java.util.concurrent.Semaphore;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityStatusListenerAdapter;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.JavaTransformationSensorConnection;
import org.roboticsapi.world.Transformation;

public class FollowCartesianGoalFromJavaActivityImpl extends FollowCartesianGoalActivityImpl
		implements FollowCartesianGoalFromJavaActivity, FollowCartesianGoalHandle {

	private boolean valid = false;
	protected RoboticsException exception;
	final Frame reference;

	private final Semaphore startSem = new Semaphore(1);
	private StackTraceElement[] lastUpdateStackTrace = null;
	private JavaTransformationSensorConnection transformation;

	public FollowCartesianGoalFromJavaActivityImpl(CartesianMotionDevice robot, final Frame reference,
			DeviceParameterBag parameters) throws RoboticsException {
		super(robot, null, parameters);
		this.reference = reference;

		// FIXME: Here should be nothing with contexts
		if (!reference.isInitialized()) {
			((AbstractRoboticsObject) robot).getContext().initialize(reference);
		}

		addStatusListener(new ActivityStatusListenerAdapter() {

			@Override
			public void onActivityFailed(RoboticsException exc) {
				exception = exc;
				valid = false;
				startSem.release();
				try {
					reference.removeRelation(transformation);
				} catch (InitializationException e) {
				}
			}

			@Override
			public void onActivityStarted() {
				valid = true;
				startSem.release();
			}

			@Override
			public void onActivityCompleted() {
				valid = false;
				startSem.release();
				try {
					reference.removeRelation(transformation);
				} catch (InitializationException e) {
				}
			}
		});

	}

	public Frame getReference() {
		return reference;
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		transformation = new JavaTransformationSensorConnection(getRuntime());
		Frame goal = new Frame("FollowCartesianGoalFromJavaActivityImpl '" + getName() + "' goal Frame");

		// FIXME: Here should be nothing with contexts
		((AbstractRoboticsObject) getDevice()).getContext().initialize(goal);

		getReference().addRelation(transformation, goal);
		setGoal(goal);

		return super.prepare(prevActivity);
	}

	@Override
	public void updateGoal(double x, double y, double z, double a, double b, double c) throws RoboticsException {

		if (!valid) {
			if (exception != null) {
				throw new RoboticsException("FollowCartesianGoal was interrupted, please restart.", exception);
			}
			throw new RoboticsException("FollowCartesianGoal was interrupted for unknown reason, please restart.");
		}

		lastUpdateStackTrace = Thread.currentThread().getStackTrace();

		transformation.setX(x);
		transformation.setY(y);
		transformation.setZ(z);
		transformation.setA(a);
		transformation.setB(b);
		transformation.setC(c);

	}

	@Override
	public void updateGoal(Transformation trans) throws RoboticsException {
		updateGoal(trans.getTranslation().getX(), trans.getTranslation().getY(), trans.getTranslation().getZ(),
				trans.getRotation().getA(), trans.getRotation().getB(), trans.getRotation().getC());

	}

	@Override
	public RtActivity beginExecute() throws RoboticsException {
		RtActivity ret;
		try {
			startSem.acquire();
			ret = super.beginExecute();
			startSem.acquire();
		} catch (InterruptedException e) {
			throw new RoboticsException("Error starting FollowGoalFromJavaActivity", e);
		}

		return ret;
	}

	@Override
	public RoboticsException getException() {
		RoboticsException occurredException = super.getException();

		if (occurredException == null) {
			return null;
		}

		if (lastUpdateStackTrace != null) {
			occurredException.setStackTrace(lastUpdateStackTrace);
		}
		return occurredException;
	}

	@Override
	protected void cancelExecuteInternal() throws RoboticsException {
		super.cancelExecuteInternal();

		try {
			endExecute();
		} catch (ActionCancelledException e) {
			// this is okay here
		}

		startSem.release();
	}

	@Override
	public FollowCartesianGoalHandle startFollowing() throws RoboticsException {
		beginExecute();

		return getHandle();
	}

	@Override
	public FollowCartesianGoalHandle getHandle() {
		return this;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

}
