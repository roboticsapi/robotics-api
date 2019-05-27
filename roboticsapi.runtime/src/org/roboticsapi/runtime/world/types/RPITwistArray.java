package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ArrayType;

/**
 * Array of RPIWorld::Twist a.k.a World::Twist[]
 */
public class RPITwistArray extends ArrayType<org.roboticsapi.runtime.world.types.RPITwist> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPITwistArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPITwistArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPITwistArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.runtime.world.types.RPITwist getInitialValue() {
		return new org.roboticsapi.runtime.world.types.RPITwist();
	}
}
