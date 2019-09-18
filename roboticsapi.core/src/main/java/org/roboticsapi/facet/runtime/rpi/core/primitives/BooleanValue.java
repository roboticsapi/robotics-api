package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * value injection module
 */
public class BooleanValue extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::BooleanValue";

	/** Value */
	private final OutPort outValue = new OutPort("outValue");

	/** Value to output */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"Value", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("false"));

	public BooleanValue() {
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
	public BooleanValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public BooleanValue(Boolean paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramValue));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Boolean value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

}
