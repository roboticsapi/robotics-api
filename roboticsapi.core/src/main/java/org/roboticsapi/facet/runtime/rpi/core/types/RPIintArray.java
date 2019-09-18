package org.roboticsapi.facet.runtime.rpi.core.types;

import org.roboticsapi.facet.runtime.rpi.ArrayType;

/**
 * Array of RPICore::int a.k.a Core::int[]
 */
public class RPIintArray extends ArrayType<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIintArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIintArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIintArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.facet.runtime.rpi.core.types.RPIint getInitialValue() {
		return new org.roboticsapi.facet.runtime.rpi.core.types.RPIint();
	}
}
