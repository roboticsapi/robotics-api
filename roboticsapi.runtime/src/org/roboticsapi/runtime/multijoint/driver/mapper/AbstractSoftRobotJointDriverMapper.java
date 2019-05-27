/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.CycleTime;
import org.roboticsapi.runtime.mapping.LinkBuilder;
import org.roboticsapi.runtime.mapping.LinkBuilderResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.DoubleDataflowSensor;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.JointVelDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotJointDriver;
import org.roboticsapi.runtime.multijoint.mapper.JointDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointDriverMapperResult.JointPorts;
import org.roboticsapi.runtime.multijoint.primitives.JointMonitor;
import org.roboticsapi.runtime.multijoint.primitives.JointPosition;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class AbstractSoftRobotJointDriverMapper<AR extends ActionResult>
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotJointDriver, AR> {

	protected final DataflowType getJointDataflowType(final SoftRobotJointDriver jointDriver) {
		return new JointDataflow(jointDriver);
	}

	protected final DataflowOutPort addJointMonitorFragment(NetFragment parent, SoftRobotJointDriver jointDriver,
			DeviceParameterBag parameters) {
		final NetFragment ret = new NetFragment("SoftRobotRobotArm JointMonitor" + jointDriver.getJointNumber());
		final JointMonitor jmonitor = new JointMonitor(jointDriver.getDeviceName(), jointDriver.getJointNumber());

		ret.add(jmonitor);

		parent.add(ret);
		return ret.addOutPort(new JointDataflow(jointDriver), true, jmonitor.getOutCommandedPosition());
	}

	protected final DataflowOutPort addJointVelocityMonitorFragment(NetFragment parent,
			SoftRobotJointDriver jointDriver, DeviceParameterBag parameters) {
		final NetFragment ret = new NetFragment(
				"SoftRobotRobotArm JointVelocityMonitor" + jointDriver.getJointNumber());
		final JointMonitor jmonitor = new JointMonitor(jointDriver.getDeviceName(), jointDriver.getJointNumber());

		ret.add(jmonitor);

		parent.add(ret);
		return ret.addOutPort(new JointVelDataflow(jointDriver), true, jmonitor.getOutCommandedVelocity());
	}

	protected final JointDriverMapperResult<? extends JointDriver> getMapperResult(NetFragment fragment,
			SoftRobotRuntime runtime, SoftRobotJointDriver jointDriver, AR actionResult, DeviceParameterBag parameters,
			DataflowOutPort cancel, DataflowOutPort override) throws MappingException {
		NetFragment result = new NetFragment("SoftRobotRobotArm");

		JointPorts ports;
		try {
			ports = getPositionInput(result, jointDriver, parameters);
		} catch (RPIException e) {
			throw new MappingException(e);
		}
		result.connect(actionResult.getOutPort(), ports.getPosition());

		return new JointDriverMapperResult<SoftRobotJointDriver>(jointDriver, result, ports);
	}

	protected final JointPorts getPositionInput(NetFragment ret, final SoftRobotJointDriver jointDriver,
			final DeviceParameterBag parameters) throws MappingException, RPIException {
		SoftRobotRuntime runtime = jointDriver.getRuntime();

		JointDataflow jointDataflow = new JointDataflow(jointDriver);
		DataflowThroughOutPort jointIn = ret.addThroughPort(false, jointDataflow);
		DataflowOutPort joint = jointIn;

		List<DataflowOutPort> invalidPorts = new ArrayList<DataflowOutPort>();
		JointParameters jointParameters = parameters.get(JointParameters.class);
		if (jointParameters != null) {

			// prepare required data
			CycleTime cycleTime = ret.add(new CycleTime());
			DataflowOutPort cycleTimePort = ret.addOutPort(new DoubleDataflow(), false, cycleTime.getOutValue());
			DoubleSensor cur = jointDriver.getCommandedPositionSensor();
			DoubleSensor cmd = new DoubleDataflowSensor(joint, runtime);
			DoubleSensor dt = new DoubleDataflowSensor(cycleTimePort, runtime);
			DoubleSensor nan = DoubleSensor.fromValue(0).divide(0);

			// populate sensor mapping context
			SensorMappingContext context = new SensorMappingContext();
			context.addSensorResult(cmd, new DoubleSensorMapperResult(new NetFragment(""), joint));
			context.addSensorResult(dt, new DoubleSensorMapperResult(new NetFragment(""), cycleTimePort));

			// joint limits
			DoubleSensor upperLimit = DoubleSensor.fromValue(jointParameters.getMaximumPosition());
			DoubleSensor lowerLimit = DoubleSensor.fromValue(jointParameters.getMinimumPosition());
			DoubleSensor velLimit = DoubleSensor.fromValue(jointParameters.getMaximumVelocity() * 2.0);
			DoubleSensor accLimit = DoubleSensor.fromValue(jointParameters.getMaximumAcceleration() * 0.95);

			// distance and maximum velocity to joint limit
			DoubleSensor upperDistance = upperLimit.add(cur.negate());
			DoubleSensor lowerDistance = cur.add(lowerLimit.negate());
			DoubleSensor maxVel = upperDistance.multiply(accLimit).multiply(2).sqrt();
			DoubleSensor minVel = lowerDistance.multiply(accLimit).multiply(2).sqrt().negate();

			// beyond joint limit -> vel = 0
			maxVel = DoubleSensor.conditional(upperDistance.less(0), DoubleSensor.fromValue(0), maxVel);
			minVel = DoubleSensor.conditional(lowerDistance.less(0), DoubleSensor.fromValue(0), minVel);

			// limit to configured maximum velocity
			maxVel = DoubleSensor.conditional(maxVel.greater(velLimit), velLimit, maxVel);
			minVel = DoubleSensor.conditional(minVel.less(velLimit.negate()), velLimit.negate(), minVel);

			// maximum and minimum allowed position in the next cycle
			DoubleSensor maxPos = cur.add(maxVel.multiply(dt));
			DoubleSensor minPos = cur.add(minVel.multiply(dt));

			// check & limit joint position
			BooleanSensor tooBig = cmd.greater(maxPos);
			BooleanSensor tooSmall = cmd.less(minPos);
			// BooleanSensor invalidSensor = tooBig.or(tooSmall);
			cmd = DoubleSensor.conditional(tooBig, /* maxPos */nan, cmd);
			cmd = DoubleSensor.conditional(tooSmall, /* minPos */nan, cmd);

			// map all the stuff
			SensorMapperResult<Double> mapped = runtime.getMapperRegistry().mapSensor(runtime, cmd, ret, context);
			joint = ret.reinterpret(mapped.getSensorPort(), new JointDataflow(jointDriver));

			// SensorMapperResult<Boolean> invalid = runtime.getMapperRegistry()
			// .mapSensor(runtime, invalidSensor, ret, context);
			// invalidPorts.add(invalid.getSensorPort());
		}

		final JointPosition jnt = ret.add(new JointPosition(jointDriver.getDeviceName(), jointDriver.getJointNumber()));
		jnt.getInPosition().setDebug(2);
		ret.connect(joint, jnt.getInPosition(), jointDataflow);

		DataflowOutPort concurrentAccessPort = ret.addOutPort(new StateDataflow(), true,
				jnt.getOutErrorConcurrentAccess());
		DataflowOutPort drivesNotEnabledPort = ret.addOutPort(new StateDataflow(), true, jnt.getOutErrorJointFailed());
		DataflowOutPort illegalJointValuePort = ret.addOutPort(new StateDataflow(), true,
				jnt.getOutErrorIllegalPosition());
		invalidPorts.add(illegalJointValuePort);
		illegalJointValuePort = ret.add(new OrFragment(new StateDataflow(), invalidPorts)).getOrOut();

		return new JointDriverMapperResult.JointPorts(jointIn.getInPort(), concurrentAccessPort, drivesNotEnabledPort,
				illegalJointValuePort, null);
	}

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, final SoftRobotJointDriver actuatorRuntimeAdapter,
			AR actionResult, final DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment fragment = new NetFragment(actuatorRuntimeAdapter.getName());

		// add inverse kinematics as a LinkBuilder
		fragment.addLinkBuilder(new LinkBuilder() {
			@Override
			public LinkBuilderResult buildLink(final DataflowType from, final DataflowType to) throws MappingException {
				if (from == null && to != null && to.providesValueFor(getJointDataflowType(actuatorRuntimeAdapter))) {
					NetFragment ret = new NetFragment("Joint Monitor");
					DataflowOutPort result = addJointMonitorFragment(ret, actuatorRuntimeAdapter, parameters);
					return new LinkBuilderResult(ret, null, result);
				}
				return null;
			}
		});

		return getMapperResult(fragment, runtime, actuatorRuntimeAdapter, actionResult, parameters, ports.cancel,
				ports.override);
	}

}
