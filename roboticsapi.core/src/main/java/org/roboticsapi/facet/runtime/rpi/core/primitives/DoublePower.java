package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A Double power module (calculates the specified number raised to the
 * specified power)
 */
public class DoublePower extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoublePower";

	/** First value (base) */
	private final InPort inFirst = new InPort("inFirst");

	/** Second value (exponent) */
	private final InPort inSecond = new InPort("inSecond");

	/** Result value (base raised to the power exponent) */
	private final OutPort outValue = new OutPort("outValue");

	/** First value (base) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramFirst = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"First", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Second value (exponent) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramSecond = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Second", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public DoublePower() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inFirst);
		add(inSecond);
		add(outValue);

		// Add all parameters
		add(paramFirst);
		add(paramSecond);
	}

	/**
	 * Creates a Double power module (calculates the specified number raised to the
	 * specified power)
	 *
	 * @param paramFirst  First value (base)
	 * @param paramSecond Second value (exponent)
	 */
	public DoublePower(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramFirst,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramSecond) {
		this();

		// Set the parameters
		setFirst(paramFirst);
		setSecond(paramSecond);
	}

	/**
	 * Creates a Double power module (calculates the specified number raised to the
	 * specified power)
	 *
	 * @param paramFirst  First value (base)
	 * @param paramSecond Second value (exponent)
	 */
	public DoublePower(Double paramFirst, Double paramSecond) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramFirst),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramSecond));
	}

	/**
	 * First value (base)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * Second value (exponent)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * Result value (base raised to the power exponent)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * First value (base)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getFirst() {
		return this.paramFirst;
	}

	/**
	 * Sets a parameter of the block: First value (base)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramFirst.setValue(value);
	}

	/**
	 * Sets a parameter of the block: First value (base)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(Double value) {
		this.setFirst(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Second value (exponent)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getSecond() {
		return this.paramSecond;
	}

	/**
	 * Sets a parameter of the block: Second value (exponent)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramSecond.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Second value (exponent)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(Double value) {
		this.setSecond(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
