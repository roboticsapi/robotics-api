package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ArrayType;

/**
 * Array of RPIWorld::Frame a.k.a World::Frame[]
 */
public class RPIFrameArray extends ArrayType<org.roboticsapi.runtime.world.types.RPIFrame> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIFrameArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIFrameArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIFrameArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.runtime.world.types.RPIFrame getInitialValue() {
		return new org.roboticsapi.runtime.world.types.RPIFrame();
	}
}
