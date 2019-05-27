package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A Rotation average module (calculating the sliding average of a rotation in a
 * certain time frame)
 */
public class RotationAverage extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationAverage";

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Value to use */
	private final InPort inValue = new InPort("inValue");

	/** Sliding average of the value */
	private final OutPort outValue = new OutPort("outValue");

	/** Sliding window size (s) */
	private final Parameter<RPIdouble> paramDuration = new Parameter<RPIdouble>("Duration", new RPIdouble("1"));

	public RotationAverage() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inReset);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramDuration);
	}

	/**
	 * Creates a Rotation average module (calculating the sliding average of a
	 * rotation in a certain time frame)
	 * 
	 * @param duration Sliding window size (s)
	 */
	public RotationAverage(RPIdouble paramDuration) {
		this();

		// Set the parameters
		setDuration(paramDuration);
	}

	/**
	 * Creates a Rotation average module (calculating the sliding average of a
	 * rotation in a certain time frame)
	 * 
	 * @param duration Sliding window size (s)
	 */
	public RotationAverage(Double paramDuration) {
		this(new RPIdouble(paramDuration));
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
	public final Parameter<RPIdouble> getDuration() {
		return this.paramDuration;
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(RPIdouble value) {
		this.paramDuration.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Sliding window size (s)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDuration(Double value) {
		this.setDuration(new RPIdouble(value));
	}

}
