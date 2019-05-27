package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Calculates the arc sine
 */
public class DoubleAsin extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleAsin";

	/** value */
	private final InPort inValue = new InPort("inValue");

	/** Result (asin(value)) */
	private final OutPort outValue = new OutPort("outValue");

	/** Value */
	private final Parameter<RPIdouble> paramValue = new Parameter<RPIdouble>("Value", new RPIdouble("1"));

	public DoubleAsin() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates calculates the arc sine
	 *
	 * @param paramValue Value
	 */
	public DoubleAsin(RPIdouble paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates calculates the arc sine
	 *
	 * @param paramValue Value
	 */
	public DoubleAsin(Double paramValue) {
		this(new RPIdouble(paramValue));
	}

	/**
	 * value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Result (asin(value))
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
	public final Parameter<RPIdouble> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(RPIdouble value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value
	 *
	 * @param value new value of the parameter
	 */
	public final void setValue(Double value) {
		this.setValue(new RPIdouble(value));
	}

}