/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Interface for RoboticsObjects that serve as proxies for RoboticsRuntime
 * objects and can determine which operational state they currently have.
 */
public interface OnlineObject extends RoboticsObject {

	/**
	 * The operational states that an OnlineObject can have.
	 */
	public enum OperationState {

		/** The OnlineObject is not initialized. */
		NEW {
			@Override
			public boolean isPresent() {
				return false;
			}
		},
		/** The operational state is unknown (could not be checked). */
		UNKNOWN {
			@Override
			public boolean isPresent() {
				return false;
			}
		},
		/**
		 * The operational state was checked, but the RoboticsRuntime object that this
		 * OnlineObject serves as proxy for could not be found.
		 */
		ABSENT {
			@Override
			public boolean isPresent() {
				return false;
			}
		},
		/** The RoboticsRuntime object is present, but currently offline. */
		OFFLINE {
			@Override
			public boolean isPresent() {
				return true;
			}
		},
		/** The RoboticsRuntime object is in safeoperational state . */
		SAFEOPERATIONAL {
			@Override
			public boolean isPresent() {
				return true;
			}
		},
		/** The RoboticsRuntime object is operational. */
		OPERATIONAL {
			@Override
			public boolean isPresent() {
				return true;
			}
		};

		public abstract boolean isPresent();
	}

	/**
	 * The listener interface for receiving changes in operational state. The class
	 * that is interested in processing a operational state change event implements
	 * this interface, and the object created with that class is registered with a
	 * component using the component's <code>addOperationStateListener<code> method.
	 * When the operational state change event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OperationState
	 */
	public interface OperationStateListener {

		/**
		 * Called when the operational state of the respective OnlineObject changes.
		 *
		 * @param object   the object whose operational state has changed
		 * @param newState the new state
		 */
		public void operationStateChanged(OnlineObject object, OperationState newState);
	}

	/**
	 * Abstract implementation of an OperationStateListener that notifies just about
	 * the change in present state.
	 *
	 */
	public abstract class PresentListener implements OperationStateListener {

		private OperationState prevState = null;

		@Override
		public void operationStateChanged(OnlineObject object, OperationState newState) {

			if (newState.isPresent() && (prevState == null || !prevState.isPresent())) {
				isPresent(object);
			} else if (!newState.isPresent() && (prevState == null || prevState.isPresent())) {
				isNotPresent(object);
			}

			prevState = newState;
		}

		/**
		 * Called when the OnlineObject is present.
		 *
		 * @param ro the OnlineObject that is present.
		 */
		public abstract void isPresent(OnlineObject ro);

		/**
		 * Called when the OnlineObject is not present.
		 *
		 * @param ro the OnlineObject that is not present.
		 */
		public abstract void isNotPresent(OnlineObject ro);
	}

	/**
	 * Adds a listener for changes in operational state of this OnlineObject.
	 *
	 * @param listener the listener
	 */
	public void addOperationStateListener(OperationStateListener listener);

	/**
	 * Removes a previously added listener for changes in operational state.
	 *
	 * @param listener the listener
	 */
	public void removeOperationStateListener(OperationStateListener listener);

	/**
	 * Gets the operational state of this OnlineObject.
	 *
	 * @return the state
	 */
	public abstract OperationState getState();

	/**
	 * Checks if this OnlineObject is present, i.e. the RoboticsRuntime object was
	 * found.
	 *
	 * @return true, if is present
	 */
	public boolean isPresent();

}
