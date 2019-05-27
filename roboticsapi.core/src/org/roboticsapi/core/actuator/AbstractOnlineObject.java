/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.NotPresentException;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.exception.OperationStateUnknownException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.roboticsobject.InternalOperationState;
import org.roboticsapi.core.roboticsobject.InternalOperationState.StateListener;
import org.roboticsapi.core.roboticsobject.OperationStateMachine;

/**
 * Abstract implementation of {@link OnlineObject}.
 */
public abstract class AbstractOnlineObject extends AbstractRoboticsObject implements OnlineObject {

	private OperationState intendedState = OperationState.OFFLINE;

	private final PresentListener dependentObjectListener = new PresentListener() {
		@Override
		public void isNotPresent(OnlineObject ro) {
			statemachine.dependentObjectPresentChanged(ro, false);
		}

		@Override
		public void isPresent(OnlineObject ro) {
			statemachine.dependentObjectPresentChanged(ro, true);
		}
	};

	private final InternalOperationState.StateListener onlineListenerNotifier = new StateListener() {

		@Override
		public void stateLeft(InternalOperationState state, InternalOperationState nextState) {
		}

		@Override
		public void stateEntered(InternalOperationState state) {
			for (OperationStateListener l : new ArrayList<OperationStateListener>(onlineListeners)) {
				l.operationStateChanged(AbstractOnlineObject.this, state.getOperationState());
			}
		}
	};

	private OnlineObject mirrorObject;

	protected void mirrorOperationState(OnlineObject other) {
		if (other == null) {
			throw new IllegalArgumentException("The parameter other must not be null.");
		}

		if (this.equals(other)) {
			throw new IllegalArgumentException(
					"The parameter other must not be the same object like the calling object.");
		}

		endMirrorOperationState();
		this.mirrorObject = other;
		mirrorListener = new OperationStateListener() {
			@Override
			public void operationStateChanged(OnlineObject object, OperationState newState) {
				if (!isInitialized()) {
					return;
				}
				if (isPresent() != object.isPresent()) {
					setPresent(object.isPresent());
				}
				switch (newState) {
				case OFFLINE:
					goOffline();
					break;
				case SAFEOPERATIONAL:
					goSafeoperational();
					break;
				case OPERATIONAL:
					goOperational();
					break;
				default:
					break;
				}
			}
		};
		other.addOperationStateListener(mirrorListener);
		mirrorListener.operationStateChanged(other, other.getState());
	}

	protected void endMirrorOperationState() {
		if (mirrorObject != null) {

			mirrorObject.removeOperationStateListener(mirrorListener);
			mirrorObject = null;
		}
	}

	private final List<OperationStateListener> onlineListeners = new ArrayList<OperationStateListener>();
	private final List<OnlineObject> dependentObjects = new ArrayList<OnlineObject>();

	private final OperationStateMachine statemachine = new OperationStateMachine(this);

	private OperationStateListener mirrorListener;

	protected AbstractOnlineObject() {
		statemachine.getUnknownOperationState().addListener(new InternalOperationState.StateEnteredListener() {

			@Override
			public void stateEntered(InternalOperationState state) {
				checkConnection();
			}

		});

		statemachine.getNewState().addListener(onlineListenerNotifier);
		statemachine.getUnknownOperationState().addListener(onlineListenerNotifier);
		statemachine.getAbsentOperationState().addListener(onlineListenerNotifier);
		statemachine.getOfflineOperationState().addListener(onlineListenerNotifier);
		statemachine.getSafeOperationalOperationState().addListener(onlineListenerNotifier);
		statemachine.getOperationalOperationState().addListener(onlineListenerNotifier);

	}

	@Override
	public OperationState getState() {
		return statemachine.getOperationState();
	}

	@Override
	public final boolean isPresent() {
		return statemachine.isPresent();
	}

	private void setPresent(boolean present) {
		statemachine.presentChecked(present);

		if (present) {
			switch (intendedState) {
			case OFFLINE:
				goOffline();
				break;
			case SAFEOPERATIONAL:
				goSafeoperational();
				break;
			case OPERATIONAL:
				goOperational();
				break;
			default:
				break;
			}
			onPresent();
		} else {
			onAbsent();
		}

		// moved to statemachine listener
		// if (this.isPresent() != present) {
		// for (PresentListener listener : onlineListeners) {
		// if (present) {
		// listener.isPresent(this);
		// } else {
		// listener.isNotPresent(this);
		// }
		// }
		// }
	}

	protected void onPresent() {
		// empty default implementation
	}

	protected void onAbsent() {
		// empty default implementation
	}

	protected void goOffline() {
		intendedState = OperationState.OFFLINE;
		if (isPresent()) {
			statemachine.goOffline();
		}
	}

	protected void goSafeoperational() {
		intendedState = OperationState.SAFEOPERATIONAL;
		if (isPresent()) {
			statemachine.goSafeoperational();
		}
	}

	protected void goOperational() {
		intendedState = OperationState.OPERATIONAL;
		if (isPresent()) {
			statemachine.goOperational();
		}
	}

	protected final void addDependentObject(OnlineObject object) {
		if (object != null) {
			dependentObjects.add(object);
			object.addOperationStateListener(dependentObjectListener);
		}
	}

	protected final void removeDependentObject(OnlineObject object) {
		if (object != null) {
			dependentObjects.remove(object);
			object.removeOperationStateListener(dependentObjectListener);
		}
	}

	public List<OnlineObject> getDependentObjects() {
		return dependentObjects;
	}

	public final boolean checkDependentObjects() {
		for (OnlineObject object : dependentObjects) {
			if (!object.isPresent()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks whether the RoboticsRuntime object that this OnlineObject servers as
	 * proxy for does exist.
	 *
	 * @throws NotPresentException thrown if
	 */
	public final void checkConnection() {

		// check dependentObjects, remember exception
		boolean dependentPresent = true;
		dependentPresent &= checkDependentObjects();

		// if dependent objects not present, we can't say anything
		if (!dependentPresent) {
			return;
		}

		// check if this object is present
		try {
			setPresent(checkPresent());
		} catch (OperationStateUnknownException exc) {
			// if operation state unknown, do nothing

		}
	}

	protected boolean checkPresent() throws OperationStateUnknownException {
		return true;
	}

	@Override
	public final void addOperationStateListener(OperationStateListener listener) {
		if (!onlineListeners.contains(listener)) {
			onlineListeners.add(listener);
		}
		listener.operationStateChanged(this, getState());
	}

	@Override
	public final void removeOperationStateListener(OperationStateListener listener) {
		onlineListeners.remove(listener);
	}

	@Override
	protected void afterInitialization() throws RoboticsException {
		statemachine.initialized();
	}

	@Override
	protected void beforeUninitialization() throws RoboticsException {
		for (OnlineObject obj : new ArrayList<OnlineObject>(dependentObjects)) {
			removeDependentObject(obj);
		}
		statemachine.uninitialized();
	}

}
