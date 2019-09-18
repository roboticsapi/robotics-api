package org.roboticsapi.facet.runtime.rpi.core.types;

import org.roboticsapi.facet.runtime.rpi.ArrayType;

/**
 * Array of RPICore::bool a.k.a Core::bool[]
 */
public class RPIboolArray extends ArrayType<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIboolArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIboolArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIboolArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.facet.runtime.rpi.core.types.RPIbool getInitialValue() {
		return new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool();
	}
}
