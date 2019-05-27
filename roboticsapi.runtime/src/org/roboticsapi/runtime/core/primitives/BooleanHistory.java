package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A Boolean history module (counts the amount of 'true' in a certain time
 * frame)
 */
public class BooleanHistory extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::BooleanHistory";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Value to count */
	private final InPort inValue = new InPort("inValue");

	/** All (Values has always been true) */
	private final OutPort outAll = new OutPort("outAll");

	/** Amount of 'true's (0..1) */
	private final OutPort outAmount = new OutPort("outAmount");

	/** None (Value has always been false) */
	private final OutPort outNone = new OutPort("outNone");

	/** Sliding window size (s) */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramDuration = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Duration", new org.roboticsapi.runtime.core.types.RPIdouble("1"));

	public BooleanHistory() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
		add(inValue);
		add(outAll);
		add(outAmount);
		add(outNone);

		// Add all parameters
		add(paramDuration);
	}

	/**
	 * Creates a Boolean history module (counts the amount of 'true' in a certain
	 * time frame)
	 *
	 * @param duration Sliding window size (s)
	 */
	public BooleanHistory(org.roboticsapi.runtime.core.types.RPIdouble paramDuration) {
		this();

		// Set the parameters
		setDuration(paramDuration);
	}

	/**
	 * Creates a Boolean history module (counts the amount of 'true' in a certain
	 * time frame)
	 *
	 * @param duration Sliding window size (s)
	 */
	public BooleanHistory(Double paramDuration) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramDuration));
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
	 * Reset port
	 *
	 * @return the input port of the block
	 */
	public final InPort getInReset() {
		return this.inReset;
	}

	/**
	 * Value to count
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * All (Values has always been true)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAll() {
		return this.outAll;
	}

	/**
	 * Amount of 'true's (0..1)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAmount() {
		return this.outAmount;
	}

	/**
	 * None (Value has always been false)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutNone() {
		return this.outNone;
	}

	/**
	 * Sliding window size (s)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getDuration() {
		return this.paramDuration;
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramDuration.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(Double value) {
		this.setDuration(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
