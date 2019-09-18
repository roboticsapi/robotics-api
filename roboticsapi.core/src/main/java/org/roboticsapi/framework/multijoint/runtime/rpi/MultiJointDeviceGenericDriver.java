/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi;

import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.MultiDependency.Builder;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.JointDriver;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.framework.multijoint.activity.JointGoalMotionInterface;
import org.roboticsapi.framework.multijoint.activity.JointHoldMotionInterface;
import org.roboticsapi.framework.multijoint.activity.JointPtpInterface;
import org.roboticsapi.framework.multijoint.activity.JointStopInterface;
import org.roboticsapi.framework.multijoint.activity.JointVelocityMotionInterface;
import org.roboticsapi.framework.multijoint.activity.SimulatedJointMotionInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.JointGoalMotionInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.JointHoldMotionInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.JointPtpInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.JointStopInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.JointVelocityMotionInterfaceImpl;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.SimulatedJointMotionInterfaceImpl;

/**
 * Default implementation of a {@link MultiJointDeviceDriver} for RPI runtimes.
 */
public class MultiJointDeviceGenericDriver<D extends MultiJointDevice> extends AbstractActuatorDriver<D>
		implements MultiJointDeviceDriver {

	protected static final String DEVICE_INTERFACE = "robotarm";
	private final MultiDependency<JointDriver> jointDrivers;

	public MultiJointDeviceGenericDriver() {
		jointDrivers = createMultiDependency("jointDriver", new Dependency.Builder<Integer>() {
			@Override
			public Integer create() {
				return getJointCount();
			}
		}, new Builder<JointDriver>() {
			@Override
			public JointDriver create(int index) {
				return createJointDriver(index, getDevice().getJoint(index));
			}
		});
	}

	public MultiJointDeviceGenericDriver(D device, RpiRuntime runtime) {
		this();
		setDevice(device);
		setRuntime(runtime);
	}

	@Override
	public int getJointCount() {
		return getDevice().getJointCount();
	}

	@ConfigurationProperty
	public void setJointDriver(int jointNumber, JointDriver driver) {
		jointDrivers.set(jointNumber, driver);
	}

	@Override
	public JointDriver getJointDriver(int jointNumber) {
		return jointDrivers.get(jointNumber);

	}

	protected JointDriver createJointDriver(int jointNumber, Joint joint) {
		return new JointGenericDriver(getRuntime(), getRpiDeviceName(), getRpiDeviceType(), jointNumber, joint);
	}

	@Override
	protected RpiParameters getRpiDeviceParameters() {
		List<? extends Joint> joints = getDevice().getJoints();
		RPIdoubleArray minJoint = new RPIdoubleArray(joints.size());
		RPIdoubleArray maxJoint = new RPIdoubleArray(joints.size());
		RPIdoubleArray maxVel = new RPIdoubleArray(joints.size());
		RPIdoubleArray maxAcc = new RPIdoubleArray(joints.size());

		for (int i = 0; i < joints.size(); i++) {
			minJoint.set(i, new RPIdouble(joints.get(i).getMinimumPosition()));
			maxJoint.set(i, new RPIdouble(joints.get(i).getMaximumPosition()));
			maxVel.set(i, new RPIdouble(joints.get(i).getMaximumVelocity()));
			maxAcc.set(i, new RPIdouble(joints.get(i).getMaximumAcceleration()));
		}

		return super.getRpiDeviceParameters().with("min_joint", minJoint).with("max_joint", maxJoint)
				.with("max_vel", maxVel).with("max_acc", maxAcc);
	}

	@Override
	protected final boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		if (!interfaces.containsKey(DEVICE_INTERFACE)) {
			return false;
		} else if (interfaces.get(DEVICE_INTERFACE).get(RPIint.class, "jointcount") == null) {
			RAPILogger.getLogger(this).warning(
					"Could not retrieve jointcount parameter from runtime for device: " + this.getRpiDeviceName());
			return false;
		} else {
			return interfaces.get(DEVICE_INTERFACE).get(RPIint.class, "jointcount").get() == getJointCount()
					&& checkMultiJointDeviceInterfaces(interfaces);
		}
	}

	protected boolean checkMultiJointDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		return true;
	}

	@Override
	public double[] getJointAngles() throws CommunicationException {
		try {
			RealtimeDouble[] sensors = new RealtimeDouble[getJointCount()];
			for (int i = 0; i < sensors.length; i++) {
				sensors[i] = getJointDriver(i).getCommandedPositionSensor();
			}
			Double[] result = RealtimeDoubleArray.createFromComponents(sensors).getCurrentValue();
			double[] ret = new double[sensors.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = result[i];
			}
			return ret;
		} catch (RealtimeValueReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public double[] getMeasuredJointAngles() throws CommunicationException {
		try {
			RealtimeDouble[] sensors = new RealtimeDouble[getJointCount()];
			for (int i = 0; i < sensors.length; i++) {
				sensors[i] = getJointDriver(i).getMeasuredPositionSensor();
			}
			Double[] result = RealtimeDoubleArray.createFromComponents(sensors).getCurrentValue();
			double[] ret = new double[sensors.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = result[i];
			}
			return ret;
		} catch (RealtimeValueReadException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		List<ActuatorDriverRealtimeException> exceptions = super.defineActuatorDriverExceptions();
		for (Joint j : getDevice().getJoints()) {
			exceptions.addAll(j.getDriver().defineActuatorDriverExceptions());
		}
		return exceptions;
	}

	@Override
	protected void collectDeviceInterfaceFactories(DeviceInterfaceFactoryCollector collector) {
		super.collectDeviceInterfaceFactories(collector);

		collector.add(JointGoalMotionInterface.class, () -> new JointGoalMotionInterfaceImpl(this));
		collector.add(JointHoldMotionInterface.class, () -> new JointHoldMotionInterfaceImpl(this));
		collector.add(JointPtpInterface.class, () -> new JointPtpInterfaceImpl(this));
		collector.add(JointStopInterface.class, () -> new JointStopInterfaceImpl(this));
		collector.add(JointVelocityMotionInterface.class, () -> new JointVelocityMotionInterfaceImpl(this));
		collector.add(SimulatedJointMotionInterface.class, () -> new SimulatedJointMotionInterfaceImpl(this));
	}

}
