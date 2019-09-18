package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * value injection module
 */
public class DoubleValue extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleValue";

	/** Value */
	private final OutPort outValue = new OutPort("outValue");

	/** Value to output */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Value", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public DoubleValue() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public DoubleValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public DoubleValue(Double paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramValue));
	}

	/**
	 * Value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Value to output
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Double value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
