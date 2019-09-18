/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.device;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class CartesianActuator extends AbstractActuator<CartesianActuatorDriver> implements CartesianMotionDevice {

	private final Dependency<Frame> referenceFrame;
	private final Dependency<Frame> movingFrame;

	public CartesianActuator() {
		referenceFrame = createDependency("referenceFrame", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " ReferenceFrame");
			}
		});
		movingFrame = createDependency("movingFrame", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " MovingFrame");
			}
		});
	}

	@Override
	public Frame getReferenceFrame() {
		return referenceFrame.get();
	}

	@ConfigurationProperty(Optional = false)
	public void setReferenceFrame(Frame referenceFrame) {
		this.referenceFrame.set(referenceFrame);
	}

	@Override
	public Pose getDefaultMotionCenter() {
		return movingFrame.get().asPose();
	}

	@Override
	public Frame getMovingFrame() {
		return movingFrame.get();
	}

	@ConfigurationProperty(Optional = false)
	public void setMovingFrame(Frame movingFrame) {
		this.movingFrame.set(movingFrame);
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		addDefaultParameters(new MotionCenterParameter(movingFrame.get()));
	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
	}

}
