/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public abstract class AbstractComposedDeviceDriver extends AbstractRoboticsObject implements ComposedDeviceDriver {

	private RoboticsRuntime runtime = null;
	private final Map<OnlineObject, OperationState> childStates = new HashMap<OnlineObject, OnlineObject.OperationState>();
	private final OperationStateListener operationStateListener = new OperationStateListener() {
		@Override
		public void operationStateChanged(OnlineObject object, OperationState newState) {
			childStates.put(object, newState);
			notifyListeners();
		}
	};
	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();
	private OperationState lastListenerOperationState = getState();

	private final void notifyListeners() {
		OperationState newState = getState();
		if (lastListenerOperationState == newState) {
			return;
		}
		lastListenerOperationState = newState;

		for (OperationStateListener l : operationStateListeners) {
			l.operationStateChanged(this, newState);
		}
	}

	protected final void addChildObject(OnlineObject child) {
		if (child != null) {
			childStates.put(child, child.getState());
			child.addOperationStateListener(operationStateListener);
		}
	}

	protected final void removeChildObject(OnlineObject child) {
		if (child != null) {
			child.removeOperationStateListener(operationStateListener);
			childStates.remove(child);
		}
	}

	@ConfigurationProperty(Optional = false)
	public void setRuntime(RoboticsRuntime runtime) {
		immutableWhenInitialized();
		this.runtime = runtime;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	@Override
	public void addOperationStateListener(OperationStateListener listener) {
		operationStateListeners.add(listener);
		listener.operationStateChanged(this, lastListenerOperationState);
	}

	@Override
	public void removeOperationStateListener(OperationStateListener listener) {
		operationStateListeners.remove(listener);
	}

	@Override
	public OperationState getState() {
		if (!isInitialized()) {
			return OperationState.NEW;
		} else {
			return calculateState(childStates);
		}
	}

	protected OperationState calculateState(Map<OnlineObject, OperationState> childStates) {
		return getMinimumStateFrom(childStates.values().toArray(new OperationState[childStates.size()]));
	}

	protected static OperationState getAllEqualStateOrUnknownFrom(OperationState... operationStates) {
		OperationState allEqual = null;
		for (OperationState opState : operationStates) {
			if (allEqual == null) {
				allEqual = opState;
			}
			if (opState != allEqual) {
				return OperationState.UNKNOWN;
			}
		}
		return allEqual == null ? OperationState.OPERATIONAL : allEqual;
	}

	protected static OperationState getMinimumStateFrom(OperationState... operationStates) {
		OperationState minOpSstate = OperationState.OPERATIONAL;
		for (OperationState opState : operationStates) {
			if (getPriority(opState) < getPriority(minOpSstate)) {
				minOpSstate = opState;
			}
		}
		return minOpSstate;
	}

	private static int getPriority(OperationState operationState) {
		// switch operation can't handle null references (switch can only handle
		// integers)
		if (operationState == null) {
			throw new IllegalArgumentException("Argument operationState must not be null.");
		}

		switch (operationState) {
		case ABSENT:
			return 1;
		case NEW:
			return 0;
		case OFFLINE:
			return 3;
		case OPERATIONAL:
			return 5;
		case SAFEOPERATIONAL:
			return 4;
		case UNKNOWN:
			return 2;
		// TODO: Maybe throw new NotImplementedException instead of return
		// -1?
		default:
			return -1;
		}
	}

	@Override
	public boolean isPresent() {
		return getState().isPresent();
	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		return new ArrayList<ActuatorDriverRealtimeException>();
	}

	@Override
	public final List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActuatorDriverRealtimeException>> types = new ArrayList<Class<? extends ActuatorDriverRealtimeException>>();
		for (ActuatorDriverRealtimeException e : defineActuatorDriverExceptions()) {
			types.add(e.getClass());
		}
		return types;
	}

	@Override
	public final <T extends ActuatorDriverRealtimeException> T defineActuatorDriverException(Class<T> type)
			throws CommandException {
		if (type == null) {
			throw new IllegalArgumentException("Exception type must not be null.");
		}

		for (ActuatorDriverRealtimeException ex : defineActuatorDriverExceptions()) {
			if (type.isAssignableFrom(ex.getClass())) {
				return type.cast(ex);
			}
		}
		throw new CommandException("Found no actuator exception of the given type: " + type.getName());
	}

}
