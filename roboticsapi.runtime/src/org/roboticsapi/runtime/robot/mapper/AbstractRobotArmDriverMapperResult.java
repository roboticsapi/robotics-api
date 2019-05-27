/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.actuator.GeneralActuatorException;
import org.roboticsapi.multijoint.IllegalJointValueException;
import org.roboticsapi.robot.RobotArmDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.AbstractExceptionPortFactory;
import org.roboticsapi.runtime.mapping.result.impl.AggregatingExceptionPortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.StatePortFactory;

public abstract class AbstractRobotArmDriverMapperResult<R extends RobotArmDriver>
		extends BaseActuatorDriverMapperResult {

	private final ActuatorDriverMapperResult[] jointResult;

	protected final class RobotExceptionPortFactory<T extends IllegalJointValueException>
			extends AbstractExceptionPortFactory<T> {
		private final ActuatorDriverMapperResult[] jointResult;

		public RobotExceptionPortFactory(ActuatorDriverMapperResult[] jointResult) {
			this.jointResult = jointResult;
		}

		@Override
		public List<DataflowOutPort> createTypedExceptionPort(T exception) throws MappingException {
			if (jointResult == null) {
				return null;
			}

			List<DataflowOutPort> ret = new Vector<DataflowOutPort>();
			try {
				for (int i = 0; i < jointResult.length; i++) {
					if (jointResult[i].getActuatorDriver() != exception.getActuatorDriver()) {
						continue;
					}
					ret.addAll(jointResult[i].getExceptionPort(
							jointResult[i].getActuatorDriver().defineActuatorDriverException(exception.getClass())));
				}
			} catch (CommandException ex) {
				throw new MappingException(ex);
			}

			return ret;
		}
	}

	public AbstractRobotArmDriverMapperResult(final R robot, NetFragment fragment, final DeviceParameterBag parameters,
			SoftRobotRuntime runtime, ActuatorDriverMapperResult[] jointResult, StatePortFactory completedFactory)
			throws MappingException {
		super(robot, fragment, completedFactory);
		this.jointResult = copyArray(jointResult);

		definePorts();
	}

	public AbstractRobotArmDriverMapperResult(final R robot, NetFragment fragment, final DeviceParameterBag parameters,
			SoftRobotRuntime runtime, ActuatorDriverMapperResult[] jointResult, DataflowOutPort completed)
			throws MappingException {
		super(robot, fragment, completed);
		this.jointResult = copyArray(jointResult);

		definePorts();
	}

	private void definePorts() throws MappingException {

		// TODO: refactor exception aggregation, make factory more generic
		List<DataflowOutPort> concurrentAccessPorts = new ArrayList<DataflowOutPort>();
		List<DataflowOutPort> notOperationalPorts = new ArrayList<DataflowOutPort>();
		List<DataflowOutPort> generalPorts = new ArrayList<DataflowOutPort>();

		try {
			for (int i = 0; i < getJointResult().length; i++) {
				concurrentAccessPorts.addAll(getJointResult()[i].getExceptionPort(getJointResult()[i]
						.getActuatorDriver().defineActuatorDriverException(ConcurrentAccessException.class)));

				notOperationalPorts.addAll(getJointResult()[i].getExceptionPort(getJointResult()[i].getActuatorDriver()
						.defineActuatorDriverException(ActuatorNotOperationalException.class)));

				generalPorts.addAll(getJointResult()[i].getExceptionPort(getJointResult()[i].getActuatorDriver()
						.defineActuatorDriverException(GeneralActuatorException.class)));
			}
		} catch (CommandException ex) {
			throw new MappingException(ex);
		}

		addExceptionPortFactory(ConcurrentAccessException.class,
				new AggregatingExceptionPortFactory<ConcurrentAccessException>(concurrentAccessPorts));

		addExceptionPortFactory(ActuatorNotOperationalException.class,
				new AggregatingExceptionPortFactory<ActuatorNotOperationalException>(notOperationalPorts));

		addExceptionPortFactory(GeneralActuatorException.class,
				new AggregatingExceptionPortFactory<GeneralActuatorException>(generalPorts));

		addExceptionPortFactory(IllegalJointValueException.class,
				new RobotExceptionPortFactory<IllegalJointValueException>(this.getJointResult()));

	}

	private ActuatorDriverMapperResult[] copyArray(ActuatorDriverMapperResult[] source) {
		if (source == null) {
			return null;
		}

		ActuatorDriverMapperResult[] target = new ActuatorDriverMapperResult[source.length];

		for (int i = 0; i < source.length; i++) {
			target[i] = source[i];
		}

		return target;
	}

	public ActuatorDriverMapperResult[] getJointResult() {
		return jointResult;
	}

}