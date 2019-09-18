package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A Double average module (calculates the sliding average of a double value in
 * a certain time frame)
 */
public class DoubleAverage extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleAverage";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Value to use */
	private final InPort inValue = new InPort("inValue");

	/** Sliding average of the value */
	private final OutPort outValue = new OutPort("outValue");

	/** Sliding window size (s) */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramDuration = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Duration", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("1"));

	public DoubleAverage() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramDuration);
	}

	/**
	 * Creates a Double average module (calculates the sliding average of a double
	 * value in a certain time frame)
	 *
	 * @param duration Sliding window size (s)
	 */
	public DoubleAverage(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramDuration) {
		this();

		// Set the parameters
		setDuration(paramDuration);
	}

	/**
	 * Creates a Double average module (calculates the sliding average of a double
	 * value in a certain time frame)
	 *
	 * @param duration Sliding window size (s)
	 */
	public DoubleAverage(Double paramDuration) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramDuration));
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
	 * Value to use
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Sliding average of the value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Sliding window size (s)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getDuration() {
		return this.paramDuration;
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramDuration.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(Double value) {
		this.setDuration(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
