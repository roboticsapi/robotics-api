package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A clock module (counts the duration the module has been active)
 */
public class Clock extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::Clock";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Increment per second */
	private final InPort inIncrement = new InPort("inIncrement");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Duration the module has been active */
	private final OutPort outValue = new OutPort("outValue");

	/** Increment per second */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramIncrement = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Increment", new org.roboticsapi.runtime.core.types.RPIdouble("1"));

	public Clock() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inIncrement);
		add(inReset);
		add(outValue);

		// Add all parameters
		add(paramIncrement);
	}

	/**
	 * Creates a clock module (counts the duration the module has been active)
	 *
	 * @param paramIncrement Increment per second
	 */
	public Clock(org.roboticsapi.runtime.core.types.RPIdouble paramIncrement) {
		this();

		// Set the parameters
		setIncrement(paramIncrement);
	}

	/**
	 * Creates a clock module (counts the duration the module has been active)
	 *
	 * @param paramIncrement Increment per second
	 */
	public Clock(Double paramIncrement) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramIncrement));
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
	 * Increment per second
	 *
	 * @return the input port of the block
	 */
	public final InPort getInIncrement() {
		return this.inIncrement;
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
	 * Duration the module has been active
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Increment per second
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getIncrement() {
		return this.paramIncrement;
	}

	/**
	 * Sets a parameter of the block: Increment per second
	 * 
	 * @param value new value of the parameter
	 */
	public final void setIncrement(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramIncrement.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Increment per second
	 * 
	 * @param value new value of the parameter
	 */
	public final void setIncrement(Double value) {
		this.setIncrement(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
