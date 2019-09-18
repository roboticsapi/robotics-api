/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.StateVariable;
import org.roboticsapi.core.world.relation.CommandedPosition;
import org.roboticsapi.core.world.relation.DynamicConnection;
import org.roboticsapi.core.world.relation.MeasuredPosition;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.IndexedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;
import org.roboticsapi.framework.multijoint.IllegalJointValueException;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.JointDriver;

public class JointGenericDriver extends AbstractActuatorDriver<Joint>
		implements JointDriver, IndexedActuatorDriver<Joint> {

	private final Dependency<Integer> jointNumber;
	private CommandedPosition commandedPosition;
	private MeasuredPosition measuredPosition;

	public JointGenericDriver() {
		jointNumber = createDependency("jointNumber");
	}

	public JointGenericDriver(RpiRuntime runtime, String deviceName, String deviceType, int jointNumber, Joint joint) {
		this();
		setRuntime(runtime);
		setRpiDeviceName(deviceName);

		setJointNumber(jointNumber);
		setDevice(joint);
	}

	@Override
	public int getIndex() {
		return jointNumber.get();
	}

	public int getJointNumber() {
		return jointNumber.get();
	}

	/**
	 * Sets the number of the joint.
	 * 
	 * @param jointNumber The joint's new number.
	 */
	@ConfigurationProperty(Optional = false)
	public void setJointNumber(int jointNumber) {
		this.jointNumber.set(jointNumber);
	}

	@Override
	public RealtimeDouble getCommandedPositionSensor() {
		return new JointRealtimeDouble(this, "outCommandedPosition");
	}

	@Override
	public RealtimeDouble getCommandedVelocitySensor() {
		return new JointRealtimeDouble(this, "outCommandedVelocity");
	}

	@Override
	public RealtimeDouble getMeasuredPositionSensor() {
		return new JointRealtimeDouble(this, "outMeasuredPosition");
	}

	@Override
	public RealtimeDouble getMeasuredVelocitySensor() {
		return new JointRealtimeDouble(this, "outMeasuredVelocity");
	}

	@Override
	protected boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces) {
		// TODO: Check joint number
		return interfaces.containsKey(MultiJointDeviceGenericDriver.DEVICE_INTERFACE);

	}

	@Override
	protected void onPresent() {
		super.onPresent();
		Map<StateVariable, RealtimeDouble> state = new HashMap<>();
		DynamicConnection dc = getDevice().getLogicalRelation();
		state.put(dc.getPositionStateVariables().get(0), getMeasuredPositionSensor());
		state.put(dc.getVelocityStateVariables().get(0), getMeasuredVelocitySensor());

		measuredPosition = new MeasuredPosition(getDevice().getFixedFrame(), getDevice().getMovingFrame(),
				dc.getTransformationForState(state), dc.getTwistForState(state));
		measuredPosition.establish();

		state.put(dc.getPositionStateVariables().get(0), getCommandedPositionSensor());
		state.put(dc.getVelocityStateVariables().get(0), getCommandedVelocitySensor());
		commandedPosition = new CommandedPosition(getDevice().getFixedFrame(), getDevice().getMovingFrame(),
				dc.getTransformationForState(state), dc.getTwistForState(state));
		commandedPosition.establish();
	}

	@Override
	protected void onAbsent() {
		if (measuredPosition != null) {
			measuredPosition.remove();
			measuredPosition = null;
		}
		if (commandedPosition != null) {
			commandedPosition.remove();
			commandedPosition = null;
		}
		super.onAbsent();
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		List<ActuatorDriverRealtimeException> exceptions = super.defineActuatorDriverExceptions();

		exceptions.add(new IllegalJointValueException(this));
		exceptions.add(new ConcurrentAccessException(this));
		exceptions.add(new ActuatorNotOperationalException(this));

		return exceptions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getRpiDeviceName() + " Joint " + jointNumber.get() + "'";
	}

}
