package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A double square root
 */
public class DoubleSquareRoot extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleSquareRoot";

	/** Value */
	private final InPort inValue = new InPort("inValue");

	/** Result (sqrt(Value)) */
	private final OutPort outValue = new OutPort("outValue");

	/** Value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramValue = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Value", new org.roboticsapi.runtime.core.types.RPIdouble("1"));

	public DoubleSquareRoot() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates a double square root
	 *
	 * @param paramValue Value
	 */
	public DoubleSquareRoot(org.roboticsapi.runtime.core.types.RPIdouble paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates a double square root
	 *
	 * @param paramValue Value
	 */
	public DoubleSquareRoot(Double paramValue) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramValue));
	}

	/**
	 * Value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Result (sqrt(Value))
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Double value) {
		this.setValue(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
