package org.roboticsapi.facet.runtime.rpi.core.types;

import org.roboticsapi.facet.runtime.rpi.ArrayType;

/**
 * Array of RPICore::double a.k.a Core::double[]
 */
public class RPIdoubleArray extends ArrayType<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> {

	/**
	 * Creates an array of the given size and initializes it with the given contents
	 *
	 * @param capacity size of the array
	 * @param value    initial value (in RPI syntax)
	 */
	public RPIdoubleArray(int capacity, String value) {
		this(capacity);
		consumeString(value);
	}

	/**
	 * Creates an array and initializes it with the given contents
	 *
	 * @param value initial value (in RPI syntax)
	 */
	public RPIdoubleArray(String value) {
		super(value);
	}

	/**
	 * Creates an array of the given size
	 *
	 * @param capacity size of the array
	 */
	public RPIdoubleArray(int capacity) {
		super(capacity);
	}

	@Override
	protected org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble getInitialValue() {
		return new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble();
	}
}
