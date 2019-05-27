/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.multijoint.IllegalJointValueException;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;

/**
 * Default implementation of a {@link MultiJointDeviceDriver} for the RCC
 * reference implementation.
 */
public class SoftRobotMultiJointDeviceDriver extends AbstractSoftRobotActuatorDriver implements MultiJointDeviceDriver {

	protected static final String DEVICE_INTERFACE = "robotarm";
	List<? extends Joint> joints;

	@Override
	public void setup(List<? extends Joint> joints) {
		this.joints = joints;
	}

	@Override
	public JointDriver createJointDriver(int jointNumber) {
		return new SoftRobotJointDriver(getRuntime(), getDeviceName(), getDeviceType(), jointNumber);
	}

	@Override
	public List<? extends Joint> getJoints() {
		return new ArrayList<Joint>(joints);
	}

	@Override
	public Joint getJoint(int jointNumber) {
		return joints.get(jointNumber);
	}

	@Override
	public final int getJointCount() {
		if (joints == null) {
			return 0;
		}
		return joints.size();
	}

	@Override
	public final double[] getJointAngles() throws CommunicationException {
		return getJointAngles("commanded");
	}

	@Override
	public final double[] getMeasuredJointAngles() throws CommunicationException {
		return getJointAngles("measured");
	}

	private final double[] getJointAngles(String type) throws CommunicationException {
		checkDeviceAndRuntimeOperational();

		DoubleSensor[] sensors = new DoubleSensor[getJointCount()];
		for (int i = 0; i < sensors.length; i++) {
			if (type.equals("measured"))
				sensors[i] = getJoint(i).getMeasuredPositionSensor();
			else
				sensors[i] = getJoint(i).getCommandedPositionSensor();
		}

		DoubleArraySensor pos = DoubleArraySensor.fromSensors(sensors);
		Double[] ret;
		try {
			ret = pos.getCurrentValue();
		} catch (SensorReadException e) {
			throw new CommunicationException("Error reading current joint position", e);
		}
		double[] rr = new double[ret.length];
		for (int i = 0; i < rr.length; i++)
			rr[i] = ret[i];
		return rr;
	}

	protected void collectMultiJointDeviceParameters(MultiJointDevice device, Map<String, String> parameters) {
		StringBuilder minJoint = new StringBuilder("{");
		StringBuilder maxJoint = new StringBuilder("{");

		List<? extends Joint> list = device.getJoints();

		for (int i = 0; i < list.size(); i++) {
			Joint joint = list.get(i);

			minJoint.append(joint.getMinimumPosition());
			maxJoint.append(joint.getMaximumPosition());

			if (i != joints.size() - 1) {
				minJoint.append(",");
				maxJoint.append(",");
			} else {
				minJoint.append("}");
				maxJoint.append("}");
			}
		}

		parameters.put("min_joint", minJoint.toString());
		parameters.put("max_joint", maxJoint.toString());
	}

	@Override
	protected final boolean checkDeviceInterfaces(List<String> interfaces) {
		return interfaces.contains(DEVICE_INTERFACE) && checkMultiJointDeviceInterfaces(interfaces);
	}

	protected boolean checkMultiJointDeviceInterfaces(List<String> interfaces) {
		return true;
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		final List<ActuatorDriverRealtimeException> errors = super.defineActuatorDriverExceptions();
		for (Joint j : getJoints()) {
			try {
				errors.add(j.getDriver().defineActuatorDriverException(IllegalJointValueException.class));
			} catch (CommandException e) {
			}
		}
		return errors;
	}

}
