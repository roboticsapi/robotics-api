/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import org.roboticsapi.core.Actuator.CompletedState;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.core.actuator.GeneralActuatorException;
import org.roboticsapi.multijoint.IllegalJointValueException;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.impl.AlwaysActiveStatePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;

public class JointDriverMapperResult<JD extends JointDriver> extends BaseActuatorDriverMapperResult {

	private JointPorts ports;

	private JointDriverMapperResult(JD jointDriver, NetFragment fragment) {
		super(jointDriver, fragment, new AlwaysActiveStatePortFactory<CompletedState>());
	}

	public JointDriverMapperResult(JD jointDriver, NetFragment fragment, JointPorts ports) {
		this(jointDriver, fragment);
		this.ports = ports;

		declarePorts();
	}

	public JointDriverMapperResult(JD jointDriver, NetFragment fragment, DataflowInPort position,
			DataflowOutPort concurrentAccess, DataflowOutPort drivesNotEnabled, DataflowOutPort illegalJointValue,
			DataflowOutPort unknownError) {
		this(jointDriver, fragment);
		this.ports = new JointPorts(position, concurrentAccess, drivesNotEnabled, illegalJointValue, unknownError);

		declarePorts();
	}

	private void declarePorts() {
		addExceptionPort(ConcurrentAccessException.class, ports.getConcurrentAccessStatePort());

		addExceptionPort(ActuatorNotOperationalException.class, ports.getDrivesNotEnabledStatePort());

		addExceptionPort(GeneralActuatorException.class, ports.getUnknownErrorStatePort());

		addExceptionPort(IllegalJointValueException.class, ports.getIllegalJointValueStatePort());

	}

	@Override
	public NetFragment getNetFragment() {
		return super.getNetFragment();
	}

	public static class JointPorts {
		private final DataflowInPort position;
		private final DataflowOutPort concurrentAccess;
		private final DataflowOutPort drivesNotEnabled;
		private final DataflowOutPort illegalJointValue;
		private final DataflowOutPort unknownError;

		public JointPorts(DataflowInPort position, DataflowOutPort concurrentAccess, DataflowOutPort drivesNotEnabled,
				DataflowOutPort illegalJointValue, DataflowOutPort unknownError) {
			this.position = position;
			this.concurrentAccess = concurrentAccess;
			this.drivesNotEnabled = drivesNotEnabled;
			this.illegalJointValue = illegalJointValue;
			this.unknownError = unknownError;
		}

		public DataflowOutPort getConcurrentAccessStatePort() {
			return concurrentAccess;
		}

		public DataflowOutPort getDrivesNotEnabledStatePort() {
			return drivesNotEnabled;
		}

		public DataflowOutPort getIllegalJointValueStatePort() {
			return illegalJointValue;
		}

		public DataflowOutPort getUnknownErrorStatePort() {
			return unknownError;
		}

		public DataflowInPort getPosition() {
			return position;
		}
	}

}
