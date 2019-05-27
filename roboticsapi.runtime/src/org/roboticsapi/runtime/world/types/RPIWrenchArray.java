package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ArrayType;

/**
 * Array of RPIWorld::Wrench a.k.a World::Wrench[]
 */
public class RPIWrenchArray extends ArrayType<org.roboticsapi.runtime.world.types.RPIWrench> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIWrenchArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIWrenchArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIWrenchArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.runtime.world.types.RPIWrench getInitialValue() {
		return new org.roboticsapi.runtime.world.types.RPIWrench();
	}
}
