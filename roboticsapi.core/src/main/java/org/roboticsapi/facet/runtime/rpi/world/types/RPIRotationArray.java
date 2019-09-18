package org.roboticsapi.facet.runtime.rpi.world.types;

import org.roboticsapi.facet.runtime.rpi.ArrayType;

/**
 * Array of RPIWorld::Rotation a.k.a World::Rotation[]
 */
public class RPIRotationArray extends ArrayType<org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIRotationArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIRotationArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIRotationArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation getInitialValue() {
		return new org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation();
	}
}
