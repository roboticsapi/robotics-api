/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.runtime.rpi.driver;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.CommandedPosition;
import org.roboticsapi.core.world.relation.MeasuredPosition;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.device.IllegalGoalException;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity.CartesianMotionInterfaces;
import org.roboticsapi.framework.cartesianmotion.sensor.CartesianCommandedRealtimeTransformation;
import org.roboticsapi.framework.cartesianmotion.sensor.CartesianCommandedRealtimeTwist;
import org.roboticsapi.framework.cartesianmotion.sensor.CartesianMeasuredRealtimeTransformation;
import org.roboticsapi.framework.cartesianmotion.sensor.CartesianMeasuredRealtimeTwist;
import org.roboticsapi.framework.platform.DifferentialWheeledPlatform;
import org.roboticsapi.framework.platform.PlatformDriver;
import org.roboticsapi.framework.platform.runtime.rpi.PlatformWheelPositionRealtimeDouble;
import org.roboticsapi.framework.platform.runtime.rpi.PlatformWheelVelocityRealtimeDouble;

public class DifferentialWheeledPlatformGenericDriver extends AbstractActuatorDriver<DifferentialWheeledPlatform>
		implements PlatformDriver, CartesianActuatorDriver {

	private static final String DEVICE_INTERFACE = "cartesianposition";
	private Relation measuredRelation, commandedRelation;
	private final Map<Integer, Relation> wheelRelations = new HashMap<Integer, Relation>();

	@Override
	public Relation createCommandedOdometryRelation(Frame from, Frame to) {
		RealtimeTwist localVel = new CartesianCommandedRealtimeTwist(this, getRpiDeviceName());
		RealtimeTransformation pos = new CartesianCommandedRealtimeTransformation(this, getRpiDeviceName());
		return new CommandedPosition(from, to, pos,
				RealtimeTwist.createFromLinearAngular(localVel.getTranslationVelocity().transform(pos.getRotation()),
						localVel.getRotationVelocity().transform(pos.getRotation())));
	}

	@Override
	public Relation createMeasuredOdometryRelation(Frame from, Frame to) {
		RealtimeTwist localVel = new CartesianMeasuredRealtimeTwist(this, getRpiDeviceName());
		RealtimeTransformation pos = new CartesianMeasuredRealtimeTransformation(this, getRpiDeviceName());
		return new MeasuredPosition(from, to, pos,
				RealtimeTwist.createFromLinearAngular(localVel.getTranslationVelocity().transform(pos.getRotation()),
						localVel.getRotationVelocity().transform(pos.getRotation())));
	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		CartesianMotionInterfaces.collectDeviceInterfaceFactories(this, collector);
	}

	@Override
	protected void onPresent() {
		super.onPresent();

		commandedRelation = createCommandedOdometryRelation(getDevice().getOdometryOrigin(),
				getDevice().getOdometryFrame());
		commandedRelation.establish();

		measuredRelation = createMeasuredOdometryRelation(getDevice().getOdometryOrigin(),
				getDevice().getOdometryFrame());
		measuredRelation.establish();

		for (int i = 0; i < getDevice().getWheels().length; i++) {
			wheelRelations.put(i,
					createWheelConnection(getDevice().getMountFrame(i), getDevice().getWheel(i).getBase(), i));
			wheelRelations.get(i).establish();
		}

	}

	@Override
	protected void onAbsent() {
		super.onAbsent();

		if (measuredRelation != null) {
			measuredRelation.remove();
		}
		measuredRelation = null;

		if (commandedRelation != null) {
			commandedRelation.remove();
		}
		commandedRelation = null;
		for (int i = 0; i < getDevice().getWheels().length; i++) {
			Relation wheelRelation = wheelRelations.remove(i);
			if (wheelRelation != null) {
				wheelRelation.remove();
			}
		}
	}

	/**
	 * Creates a {@link Connection} from a mount frame to the base frame of the
	 * specified wheel.
	 *
	 * @param wheelNumber the wheel number
	 * @return the created connection.
	 */
	protected Relation createWheelConnection(Frame from, Frame to, int wheelNumber) {
		RealtimeDouble pSensor = getWheelPositionSensor(wheelNumber);
		RealtimeDouble vSensor = getWheelVelocitySensor(wheelNumber);

		RealtimeTransformation sensor = RealtimeTransformation.createFromXYZABC(RealtimeDouble.ZERO,
				RealtimeDouble.ZERO, RealtimeDouble.ZERO, pSensor, RealtimeDouble.ZERO, RealtimeDouble.ZERO);

		RealtimeTwist velSensor = RealtimeTwist.createFromLinearAngular(RealtimeDouble.ZERO, RealtimeDouble.ZERO,
				RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO, vSensor);

		return new MeasuredPosition(from, to, sensor, velSensor);
	}

	@Override
	protected RpiParameters getRpiDeviceParameters() {
		CartesianParameters cp = getDevice().getDefaultParameters().get(CartesianParameters.class);
		return super.getRpiDeviceParameters().with("maxPosVel", new RPIdouble(cp.getMaximumPositionVelocity()))
				.with("maxRotVel", new RPIdouble(cp.getMaximumRotationVelocity()))
				.with("maxPosAcc", new RPIdouble(cp.getMaximumPositionAcceleration()))
				.with("maxRotAcc", new RPIdouble(cp.getMaximumRotationAcceleration()));
	}

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return interfaces.containsKey(DEVICE_INTERFACE);
	}

	@Override
	public Relation createRelation(Frame from, Frame to) {
		return null;
	}

	@Override
	public RealtimeDouble getWheelPositionSensor(int i) {
		return new PlatformWheelPositionRealtimeDouble(this, i);
	}

	@Override
	public RealtimeDouble getWheelVelocitySensor(int i) {
		return new PlatformWheelVelocityRealtimeDouble(this, i);
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		List<ActuatorDriverRealtimeException> ret = super.defineActuatorDriverExceptions();
		ret.add(new IllegalGoalException(this));
		return ret;
	}

}
