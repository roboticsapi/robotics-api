package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A comparison module
 */
public class IntEquals extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::IntEquals";

	/** First value */
	private final InPort inFirst = new InPort("inFirst");

	/** Second value */
	private final InPort inSecond = new InPort("inSecond");

	/** Result (true if First = Second) */
	private final OutPort outValue = new OutPort("outValue");

	/** First value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIint> paramFirst = new Parameter<org.roboticsapi.runtime.core.types.RPIint>(
			"First", new org.roboticsapi.runtime.core.types.RPIint("0"));

	/** Second value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIint> paramSecond = new Parameter<org.roboticsapi.runtime.core.types.RPIint>(
			"Second", new org.roboticsapi.runtime.core.types.RPIint("0"));

	/** Allowed difference */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIint> paramEpsilon = new Parameter<org.roboticsapi.runtime.core.types.RPIint>(
			"Epsilon", new org.roboticsapi.runtime.core.types.RPIint("0"));

	public IntEquals() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inFirst);
		add(inSecond);
		add(outValue);

		// Add all parameters
		add(paramFirst);
		add(paramSecond);
		add(paramEpsilon);
	}

	/**
	 * Creates a comparison module
	 *
	 * @param epsilon Allowed difference
	 */
	public IntEquals(org.roboticsapi.runtime.core.types.RPIint paramEpsilon) {
		this();

		// Set the parameters
		setEpsilon(paramEpsilon);
	}

	/**
	 * Creates a comparison module
	 *
	 * @param epsilon Allowed difference
	 */
	public IntEquals(Integer paramEpsilon) {
		this(new org.roboticsapi.runtime.core.types.RPIint(paramEpsilon));
	}

	/**
	 * Creates a comparison module
	 *
	 * @param paramFirst   First value
	 * @param paramSecond  Second value
	 * @param paramEpsilon Allowed difference
	 */
	public IntEquals(org.roboticsapi.runtime.core.types.RPIint paramFirst,
			org.roboticsapi.runtime.core.types.RPIint paramSecond, org.roboticsapi.runtime.core.types.RPIint paramEpsilon) {
		this();

		// Set the parameters
		setFirst(paramFirst);
		setSecond(paramSecond);
		setEpsilon(paramEpsilon);
	}

	/**
	 * Creates a comparison module
	 *
	 * @param paramFirst   First value
	 * @param paramSecond  Second value
	 * @param paramEpsilon Allowed difference
	 */
	public IntEquals(Integer paramFirst, Integer paramSecond, Integer paramEpsilon) {
		this(new org.roboticsapi.runtime.core.types.RPIint(paramFirst),
				new org.roboticsapi.runtime.core.types.RPIint(paramSecond),
				new org.roboticsapi.runtime.core.types.RPIint(paramEpsilon));
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
	 * Result (true if First = Second)
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
	public final Parameter<org.roboticsapi.runtime.core.types.RPIint> getFirst() {
		return this.paramFirst;
	}

	/**
	 * Sets a parameter of the block: First value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(org.roboticsapi.runtime.core.types.RPIint value) {
		this.paramFirst.setValue(value);
	}

	/**
	 * Sets a parameter of the block: First value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFirst(Integer value) {
		this.setFirst(new org.roboticsapi.runtime.core.types.RPIint(value));
	}

	/**
	 * Second value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIint> getSecond() {
		return this.paramSecond;
	}

	/**
	 * Sets a parameter of the block: Second value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(org.roboticsapi.runtime.core.types.RPIint value) {
		this.paramSecond.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Second value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSecond(Integer value) {
		this.setSecond(new org.roboticsapi.runtime.core.types.RPIint(value));
	}

	/**
	 * Allowed difference
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIint> getEpsilon() {
		return this.paramEpsilon;
	}

	/**
	 * Sets a parameter of the block: Allowed difference
	 * 
	 * @param value new value of the parameter
	 */
	public final void setEpsilon(org.roboticsapi.runtime.core.types.RPIint value) {
		this.paramEpsilon.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Allowed difference
	 * 
	 * @param value new value of the parameter
	 */
	public final void setEpsilon(Integer value) {
		this.setEpsilon(new org.roboticsapi.runtime.core.types.RPIint(value));
	}

}
