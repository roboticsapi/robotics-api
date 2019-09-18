package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Casts int to double
 */
public class DoubleFromInt extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleFromInt";

	/** Value */
	private final InPort inValue = new InPort("inValue");

	/** Result: double(Value) */
	private final OutPort outValue = new OutPort("outValue");

	/** Value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Value", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("1"));

	public DoubleFromInt() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates casts int to double
	 *
	 * @param paramValue Value
	 */
	public DoubleFromInt(org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates casts int to double
	 *
	 * @param paramValue Value
	 */
	public DoubleFromInt(Integer paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramValue));
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
	 * Result: double(Value)
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Integer value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

}
