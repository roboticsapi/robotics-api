package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Casts double to int
 */
public class IntFromDouble extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::IntFromDouble";

	/** Value */
	private final InPort inValue = new InPort("inValue");

	/** Result: int(Value) */
	private final OutPort outValue = new OutPort("outValue");

	/** Value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Value", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("1"));

	public IntFromDouble() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates casts double to int
	 *
	 * @param paramValue Value
	 */
	public IntFromDouble(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates casts double to int
	 *
	 * @param paramValue Value
	 */
	public IntFromDouble(Double paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramValue));
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
	 * Result: int(Value)
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Double value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
