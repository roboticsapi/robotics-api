/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multicopter;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.actuator.AbstractPhysicalActuator;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class Quadcopter extends AbstractPhysicalActuator<QuadcopterDriver>
		implements CartesianMotionDevice, PhysicalObject {

	private final Dependency<Frame> referenceFrame;
	private final Dependency<Double> horizontalRadius, verticalRadius, weight;

	// private UnknownDynamicConnection relation;

	public Quadcopter() {
		referenceFrame = createDependency("referenceFrame", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Reference");
			}
		});
		horizontalRadius = createDependency("horizontalRadius", 0.);
		verticalRadius = createDependency("verticalRadius", 0.);
		weight = createDependency("weight", 0.);
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		addDefaultParameters(new MotionCenterParameter(getBase()));
		addDefaultParameters(new CartesianParameters(3, 7, Double.POSITIVE_INFINITY, 1.5, 4, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));

		addDefaultParameters(new QuadcopterHoverThrustParameters(RealtimeDouble.createFromConstant(-Math.toRadians(40)),
				RealtimeDouble.createFromConstant(Math.toRadians(40)), RealtimeDouble.createFromConstant(0.15),
				RealtimeDouble.createFromConstant(0.2), RealtimeDouble.createFromConstant(0.2)));
		addDefaultParameters(new QuadcopterPositionControllerParameters(1.5, 1, 1));
	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		addDefaultParameters(new CartesianParameters(3.5, 7.5, Double.POSITIVE_INFINITY, 2, 5, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
	}

	@ConfigurationProperty
	public void setHRadius(double radius) {
		this.horizontalRadius.set(radius);
	}

	@ConfigurationProperty
	public void setVRadius(double radius) {
		this.verticalRadius.set(radius);
	}

	@ConfigurationProperty
	public void setWeight(double weight) {
		this.weight.set(weight);
	}

	@Override
	public Frame getReferenceFrame() {
		return referenceFrame.get();
	}

	@Override
	public Frame getMovingFrame() {
		return getBase();
	}

	public double getVRadius() {
		return verticalRadius.get();
	}

	public double getHRadius() {
		return horizontalRadius.get();
	}

	public double getWeight() {
		return weight.get();
	}

	@Override
	public Pose getDefaultMotionCenter() {
		return getBase().asPose();
	}

}
