package org.roboticsapi.facet.runtime.rpi.core.types;

import org.roboticsapi.facet.runtime.rpi.ArrayType;

/**
 * Array of RPICore::double a.k.a Core::double[]
 */
public class RPIstringArray extends ArrayType<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIstringArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIstringArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIstringArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.facet.runtime.rpi.core.types.RPIstring getInitialValue() {
		return new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring("");
	}
}
