/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime.CommandHook;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActuatorState;

public abstract class AbstractActuator<AD extends ActuatorDriver> extends AbstractDevice<AD> implements Actuator {

	private final class BusyCheckHook implements CommandHook {
		private boolean running = false;

		public BusyCheckHook() {
			// TODO: handle OperationState changes correctly
			// AbstractActuator.this.getDriver().addOperationStateListener(
			// new OperationStateListener() {
			//
			// @Override
			// public void operationStateChanged(OnlineObject object,
			// OperationState newState) {
			// if (newState == OperationState.
			// || newState == OperationState.SAFEOPERATIONAL) {
			// BusyCheckHook.this.reset();
			// }
			//
			// }
			// });
		}

		@Override
		public void commandSealHook(Command command) {
		}

		@Override
		public void commandLoadHook(Command command) {
		}

		@Override
		public void commandHandleHook(CommandHandle handle) {
			try {
				CommandStatusListener busyCommandListener = new CommandStatusListener() {

					@Override
					public void statusChanged(CommandStatus newStatus) {
						if (newStatus == CommandStatus.RUNNING) {
							if (running == false) {
								running = true;
								notifyBusyListeners();
							}
						}
						if (newStatus == CommandStatus.TERMINATED || newStatus == CommandStatus.ERROR) {
							if (running == true) {
								running = false;
								notifyBusyListeners();
							}
						}

					}
				};
				handle.addStatusListener(busyCommandListener);
			} catch (CommandException e) {
				e.printStackTrace();
			}

		}

		private void notifyBusyListeners() {
			AbstractActuator.this.notifyBusyListeners(running);
		}

		// TODO: the method reset() is only used in the constructor
		// ("out-commented" code):
		// private void reset() {
		// if (running == true) {
		// running = false;
		// notifyBusyListeners();
		// }
		// }
	}

	private final List<ActuatorBusyListener> busyListeners = new ArrayList<ActuatorBusyListener>();
	private CommandHook busyHook;

	/** Default device parameters for this device */
	private DeviceParameterBag maximumParameters = new DeviceParameterBag();
	private DeviceParameterBag defaultParameters = new DeviceParameterBag();

	@Override
	public void addBusyListener(ActuatorBusyListener listener) {
		busyListeners.add(listener);

		if (busyHook != null) {
			return;
		}

		busyHook = new BusyCheckHook();
		getDriver().getRuntime().addCommandHook(busyHook);
	}

	@Override
	public void removeBusyListener(org.roboticsapi.core.Actuator.ActuatorBusyListener listener) {
		busyListeners.remove(listener);

		if (busyListeners.size() == 0) {
			getDriver().getRuntime().removeCommandHook(busyHook);
			busyHook = null;
		}

	}

	private void notifyBusyListeners(boolean busy) {
		for (ActuatorBusyListener l : busyListeners) {
			l.busyStateChanged(busy);
		}
	}

	@Override
	public void checkParameterBounds(DeviceParameterBag parameters) throws InvalidParametersException {
		for (DeviceParameters p : parameters.getAll()) {
			checkParameterBounds(p);
		}
	}

	@Override
	public void checkParameterBounds(DeviceParameters... parameters) throws InvalidParametersException {
		for (DeviceParameters p : parameters) {
			checkParameterBounds(p);
		}
	}

	@Override
	public void checkParameterBounds(DeviceParameters parameters) throws InvalidParametersException {
		DeviceParameters max = maximumParameters.get(parameters.getClass());

		if (max != null && !parameters.respectsBounds(max)) {
			throw new InvalidParametersException("Given DeviceParameters of type "
					+ parameters.getClass().getSimpleName() + " exceed maximum values of this Actuator");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#validateParameters(org.roboticsapi.core
	 * .actuator.DeviceParameters)
	 */
	@Override
	public abstract void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	@Override
	protected final void beforeInitializationOfDerivedDevices() throws RoboticsException {
		defineMaximumParameters();
		defineDefaultParameters();

		beforeInitializationOfDerivedActuators();
	}

	protected void beforeInitializationOfDerivedActuators() throws RoboticsException {
		// empty
	}

	protected abstract void defineMaximumParameters() throws InvalidParametersException;

	protected void defineDefaultParameters() throws InvalidParametersException {
		// empty default implementation
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#addDefaultParameters(org.roboticsapi.
	 * core.actuator.DeviceParameters)
	 */
	@Override
	public void addDefaultParameters(final DeviceParameters newParameters) throws InvalidParametersException {
		checkParameterBounds(newParameters);
		validateParameters(newParameters);
		this.defaultParameters = this.defaultParameters.withParameters(newParameters);
	}

	protected void addMaximumParameters(final DeviceParameters newParameters) {
		immutableWhenInitialized();
		this.maximumParameters = this.maximumParameters.withParameters(newParameters);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#getDefaultParameters()
	 */
	@Override
	public DeviceParameterBag getDefaultParameters() {
		// TODO maybe inefficient, perhaps determine defaultParameters
		// incrementally ?
		return maximumParameters.withParameters(defaultParameters);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.core.Actuator#getCompletedState()
	 */
	@Override
	public final ActuatorState getCompletedState() {
		return completed().setDevice(this);
	}

	public static ActuatorState completed() {
		return new CompletedState();
	}

}
