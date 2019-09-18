/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimerotation;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Rotation;

/**
 * Extracts a certain component from a RealtimeRotation.
 */
public final class GetFromRotationRealtimeDouble extends RealtimeDouble {

	/**
	 * Transformation Components.
	 */
	public enum RotationComponent {
		A, B, C, QuaternionX, QuaternionY, QuaternionZ, QuaternionW
	}

	private final RotationComponent component;
	private final RealtimeRotation rotationo;

	/**
	 * Instantiates a new TransformationComponentSensor.
	 *
	 * @param rotation  the TransformationSensor to extract a component from
	 * @param component the component to extract
	 */
	GetFromRotationRealtimeDouble(RealtimeRotation rotation, RotationComponent component) {
		super(rotation);
		this.rotationo = rotation;

		if (component == null) {
			throw new IllegalArgumentException("Argument component must not be null.");
		}

		this.component = component;
	}

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public RotationComponent getComponent() {
		return component;
	}

	/**
	 * Gets the original RealtimeRotation.
	 *
	 * @return the sensor
	 */
	public RealtimeRotation getRotation() {
		return rotationo;
	}

	@Override
	protected Double calculateCheapValue() {
		Rotation curr = getRotation().getCheapValue();
		if (curr == null) {
			return null;
		}

		switch (component) {
		case A:
			return curr.getA();
		case B:
			return curr.getB();
		case C:
			return curr.getC();
		case QuaternionX:
			return curr.getQuaternion().getX();
		case QuaternionY:
			return curr.getQuaternion().getY();
		case QuaternionZ:
			return curr.getQuaternion().getZ();
		case QuaternionW:
			return curr.getQuaternion().getW();
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && component == ((GetFromRotationRealtimeDouble) obj).component
				&& rotationo.equals(((GetFromRotationRealtimeDouble) obj).rotationo);
	}

	@Override
	public int hashCode() {
		return classHash(rotationo, component);
	}

	@Override
	public boolean isAvailable() {
		return rotationo.isAvailable();
	}

	@Override
	public String toString() {
		return rotationo + "." + component;
	}
}
