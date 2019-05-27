package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Calculates the division rest
 */
public class DoubleModulo extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleModulo";

	/** First */
	private final InPort inFirst = new InPort("inFirst");

	/** Second */
	private final InPort inSecond = new InPort("inSecond");

	/** Result (First % Second) */
	private final OutPort outValue = new OutPort("outValue");

	/** First value */
	private final Parameter<RPIdouble> paramFirst = new Parameter<RPIdouble>("First", new RPIdouble("1"));

	/** Second value */
	private final Parameter<RPIdouble> paramSecond = new Parameter<RPIdouble>("Second", new RPIdouble("1"));

	public DoubleModulo() {
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
	 * Creates calculates the division rest
	 *
	 * @param paramFirst  First value
	 * @param paramSecond Second value
	 */
	public DoubleModulo(RPIdouble paramFirst, RPIdouble paramSecond) {
		this();

		// Set the parameters
		setFirst(paramFirst);
		setSecond(paramSecond);
	}

	/**
	 * Creates calculates the division rest
	 *
	 * @param paramFirst  First value
	 * @param paramSecond Second value
	 */
	public DoubleModulo(Double paramFirst, Double paramSecond) {
		this(new RPIdouble(paramFirst), new RPIdouble(paramSecond));
	}

	/**
	 * First
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * Second
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * Result (First % Second)
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
	public final Parameter<RPIdouble> getFirst() {
		return this.paramFirst;
	}

	/**
	 * Sets a parameter of the block: First value
	 *
	 * @param value new value of the parameter
	 */
	public final void setFirst(RPIdouble value) {
		this.paramFirst.setValue(value);
	}

	/**
	 * Sets a parameter of the block: First value
	 *
	 * @param value new value of the parameter
	 */
	public final void setFirst(Double value) {
		this.setFirst(new RPIdouble(value));
	}

	/**
	 * Second value
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getSecond() {
		return this.paramSecond;
	}

	/**
	 * Sets a parameter of the block: Second value
	 *
	 * @param value new value of the parameter
	 */
	public final void setSecond(RPIdouble value) {
		this.paramSecond.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Second value
	 *
	 * @param value new value of the parameter
	 */
	public final void setSecond(Double value) {
		this.setSecond(new RPIdouble(value));
	}

}
