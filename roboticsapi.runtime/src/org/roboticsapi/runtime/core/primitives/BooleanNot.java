package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A Boolean not module
 */
public class BooleanNot extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::BooleanNot";

	/** Input value */
	private final InPort inValue = new InPort("inValue");

	/** Result (!Input) */
	private final OutPort outValue = new OutPort("outValue");

	/** Input value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIbool> paramValue = new Parameter<org.roboticsapi.runtime.core.types.RPIbool>(
			"Value", new org.roboticsapi.runtime.core.types.RPIbool("false"));

	public BooleanNot() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates a Boolean not module
	 *
	 * @param paramValue Input value
	 */
	public BooleanNot(org.roboticsapi.runtime.core.types.RPIbool paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates a Boolean not module
	 *
	 * @param paramValue Input value
	 */
	public BooleanNot(Boolean paramValue) {
		this(new org.roboticsapi.runtime.core.types.RPIbool(paramValue));
	}

	/**
	 * Input value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Result (!Input)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Input value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIbool> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Input value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.runtime.core.types.RPIbool value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Input value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Boolean value) {
		this.setValue(new org.roboticsapi.runtime.core.types.RPIbool(value));
	}

}
