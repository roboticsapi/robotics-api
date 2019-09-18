
package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

/**
 * Calculates the sine
 */
public class DoubleSin extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleSin";

	/** value */
	private final InPort inValue = new InPort("inValue");

	/** Result (sin(value)) */
	private final OutPort outValue = new OutPort("outValue");

	/** Value */
	private final Parameter<RPIdouble> paramValue = new Parameter<RPIdouble>("Value", new RPIdouble("1"));

	public DoubleSin() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates calculates the sine
	 *
	 * @param paramValue Value
	 */
	public DoubleSin(RPIdouble paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates calculates the sine
	 *
	 * @param paramValue Value
	 */
	public DoubleSin(Double paramValue) {
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
	 * Result (sin(value))
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
