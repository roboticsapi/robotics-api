package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Linear interpolation module (linearly interpolates between from and to)
 */
public class Lerp extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::Lerp";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Amount (0 = from, 1 = to, 0.5 = in between) */
	private final InPort inAmount = new InPort("inAmount");

	/** Start value */
	private final InPort inFrom = new InPort("inFrom");

	/** Destination value */
	private final InPort inTo = new InPort("inTo");

	/** Interpolated value */
	private final OutPort outValue = new OutPort("outValue");

	/** Start position */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramFrom = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"From", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Destination position */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramTo = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"To", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public Lerp() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inAmount);
		add(inFrom);
		add(inTo);
		add(outValue);

		// Add all parameters
		add(paramFrom);
		add(paramTo);
	}

	/**
	 * Creates linear interpolation module (linearly interpolates between from and
	 * to)
	 *
	 * @param paramFrom Start position
	 * @param paramTo   Destination position
	 */
	public Lerp(org.roboticsapi.runtime.core.types.RPIdouble paramFrom, org.roboticsapi.runtime.core.types.RPIdouble paramTo) {
		this();

		// Set the parameters
		setFrom(paramFrom);
		setTo(paramTo);
	}

	/**
	 * Creates linear interpolation module (linearly interpolates between from and
	 * to)
	 *
	 * @param paramFrom Start position
	 * @param paramTo   Destination position
	 */
	public Lerp(Double paramFrom, Double paramTo) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramFrom),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramTo));
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
	 * Amount (0 = from, 1 = to, 0.5 = in between)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInAmount() {
		return this.inAmount;
	}

	/**
	 * Start value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrom() {
		return this.inFrom;
	}

	/**
	 * Destination value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTo() {
		return this.inTo;
	}

	/**
	 * Interpolated value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Start position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getFrom() {
		return this.paramFrom;
	}

	/**
	 * Sets a parameter of the block: Start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFrom(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramFrom.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFrom(Double value) {
		this.setFrom(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Destination position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getTo() {
		return this.paramTo;
	}

	/**
	 * Sets a parameter of the block: Destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTo(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramTo.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTo(Double value) {
		this.setTo(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
