package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A Boolean history module (counts the number of 'true' during a fixed number
 * of cycles)
 */
public class BooleanLastN extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::BooleanLastN";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Value to count */
	private final InPort inValue = new InPort("inValue");

	/** All (Value has always been true) */
	private final OutPort outAll = new OutPort("outAll");

	/** Number of 'true's */
	private final OutPort outCount = new OutPort("outCount");

	/** None (Value has always been false) */
	private final OutPort outNone = new OutPort("outNone");

	/** Sliding window size (ticks) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramCount = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Count", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("1"));

	public BooleanLastN() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
		add(inValue);
		add(outAll);
		add(outCount);
		add(outNone);

		// Add all parameters
		add(paramCount);
	}

	/**
	 * Creates a Boolean history module (counts the number of 'true' during a fixed
	 * number of cycles)
	 *
	 * @param count Sliding window size (ticks)
	 */
	public BooleanLastN(org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramCount) {
		this();

		// Set the parameters
		setCount(paramCount);
	}

	/**
	 * Creates a Boolean history module (counts the number of 'true' during a fixed
	 * number of cycles)
	 *
	 * @param count Sliding window size (ticks)
	 */
	public BooleanLastN(Integer paramCount) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramCount));
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
	 * All (Value has always been true)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAll() {
		return this.outAll;
	}

	/**
	 * Number of 'true's
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutCount() {
		return this.outCount;
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
	 * Sliding window size (ticks)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getCount() {
		return this.paramCount;
	}

	/**
	 * Sets a parameter of the block: Sliding window size (ticks)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCount(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramCount.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Sliding window size (ticks)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCount(Integer value) {
		this.setCount(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

}
