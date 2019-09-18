package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A trapezoidal modifier module (applies a trapezoidal profile to an input
 * value ranging from 0 to 1)
 */
public class Rampify extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::Rampify";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Input value (0 <= inValue <= 1) */
	private final InPort inValue = new InPort("inValue");

	/** Value with trapezoidal profile (0 <= outValue <= 1) */
	private final OutPort outValue = new OutPort("outValue");

	/** Amount of time to apply constant motion (0 <= Constant <= 1) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramConstant = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Constant", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0.5"));

	public Rampify() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramConstant);
	}

	/**
	 * Creates a trapezoidal modifier module (applies a trapezoidal profile to an
	 * input value ranging from 0 to 1)
	 *
	 * @param constant Amount of time to apply constant motion (0 <= Constant <= 1)
	 */
	public Rampify(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramConstant) {
		this();

		// Set the parameters
		setConstant(paramConstant);
	}

	/**
	 * Creates a trapezoidal modifier module (applies a trapezoidal profile to an
	 * input value ranging from 0 to 1)
	 *
	 * @param constant Amount of time to apply constant motion (0 <= Constant <= 1)
	 */
	public Rampify(Double paramConstant) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramConstant));
	}

	/**
	 * Activation port
	 *
	 * @return the input port of the block
	 */
	public final InPort getInActive() {
		return this.inActive;
	}

	/**
	 * Input value (0 <= inValue <= 1)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Value with trapezoidal profile (0 <= outValue <= 1)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Amount of time to apply constant motion (0 <= Constant <= 1)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getConstant() {
		return this.paramConstant;
	}

	/**
	 * Sets a parameter of the block: Amount of time to apply constant motion (0 <=
	 * Constant <= 1)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setConstant(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramConstant.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Amount of time to apply constant motion (0 <=
	 * Constant <= 1)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setConstant(Double value) {
		this.setConstant(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
