package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Conditional
 */
public class BooleanConditional extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::BooleanConditional";

	/** Condition */
	private final InPort inCondition = new InPort("inCondition");

	/** Frame if condition is false */
	private final InPort inFalse = new InPort("inFalse");

	/** Frame if condition is true */
	private final InPort inTrue = new InPort("inTrue");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Value if true */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramTrue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"True", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("false"));

	/** Value if false */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramFalse = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"False", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("false"));

	public BooleanConditional() {
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
	public BooleanConditional(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramTrue,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramFalse) {
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
	public BooleanConditional(Boolean paramTrue, Boolean paramFalse) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramTrue),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramFalse));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getTrue() {
		return this.paramTrue;
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramTrue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(Boolean value) {
		this.setTrue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

	/**
	 * Value if false
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getFalse() {
		return this.paramFalse;
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramFalse.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(Boolean value) {
		this.setFalse(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

}
