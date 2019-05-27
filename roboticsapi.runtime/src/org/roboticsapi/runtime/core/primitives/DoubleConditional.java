package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Conditional
 */
public class DoubleConditional extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleConditional";

	/** Condition */
	private final InPort inCondition = new InPort("inCondition");

	/** Frame if condition is false */
	private final InPort inFalse = new InPort("inFalse");

	/** Frame if condition is true */
	private final InPort inTrue = new InPort("inTrue");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Value if true */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramTrue = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"True", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Value if false */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramFalse = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"False", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public DoubleConditional() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inCondition);
		add(inFalse);
		add(inTrue);
		add(outValue);

		// Add all parameters
		add(paramTrue);
		add(paramFalse);
	}

	/**
	 * Creates conditional
	 *
	 * @param paramTrue  Value if true
	 * @param paramFalse Value if false
	 */
	public DoubleConditional(org.roboticsapi.runtime.core.types.RPIdouble paramTrue,
			org.roboticsapi.runtime.core.types.RPIdouble paramFalse) {
		this();

		// Set the parameters
		setTrue(paramTrue);
		setFalse(paramFalse);
	}

	/**
	 * Creates conditional
	 *
	 * @param paramTrue  Value if true
	 * @param paramFalse Value if false
	 */
	public DoubleConditional(Double paramTrue, Double paramFalse) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramTrue),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramFalse));
	}

	/**
	 * Condition
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCondition() {
		return this.inCondition;
	}

	/**
	 * Frame if condition is false
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFalse() {
		return this.inFalse;
	}

	/**
	 * Frame if condition is true
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTrue() {
		return this.inTrue;
	}

	/**
	 * Result
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Value if true
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getTrue() {
		return this.paramTrue;
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramTrue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(Double value) {
		this.setTrue(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Value if false
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getFalse() {
		return this.paramFalse;
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramFalse.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(Double value) {
		this.setFalse(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
