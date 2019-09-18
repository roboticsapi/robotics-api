package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A multiplication module
 */
public class DoubleMultiply extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleMultiply";

	/** First value */
	private final InPort inFirst = new InPort("inFirst");

	/** Second value */
	private final InPort inSecond = new InPort("inSecond");

	/** Result (First * Second) */
	private final OutPort outValue = new OutPort("outValue");

	/** First value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramFirst = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"First", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Second value */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramSecond = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Second", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public DoubleMultiply() {
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
	 * Creates a multiplication module
	 *
	 * @param paramFirst  First value
	 * @param paramSecond Second value
	 */
	public DoubleMultiply(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramFirst,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramSecond) {
		this();

		// Set the parameters
		setFirst(paramFirst);
		setSecond(paramSecond);
	}

	/**
	 * Creates a multiplication module
	 *
	 * @param paramFirst  First value
	 * @param paramSecond Second value
	 */
	public DoubleMultiply(Double paramFirst, Double paramSecond) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramFirst),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramSecond));
	}

	/**
	 * First value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * Second value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * Result (First * Second)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * First value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getFirst() {
		return this.paramFirst;
	}

	/**
	 * Sets a parameter of the block: First value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramFirst.setValue(value);
	}

	/**
	 * Sets a parameter of the block: First value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(Double value) {
		this.setFirst(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Second value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getSecond() {
		return this.paramSecond;
	}

	/**
	 * Sets a parameter of the block: Second value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramSecond.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Second value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(Double value) {
		this.setSecond(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
