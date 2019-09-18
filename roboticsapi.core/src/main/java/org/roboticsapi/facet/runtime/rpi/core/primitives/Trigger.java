package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A trigger module (providing a Boolean value that can be switched on or off)
 */
public class Trigger extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::Trigger";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Deactivation trigger */
	private final InPort inOff = new InPort("inOff");

	/** Activation trigger */
	private final InPort inOn = new InPort("inOn");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Activation */
	private final OutPort outActive = new OutPort("outActive");

	/** Activation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramOn = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"On", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("false"));

	/** Deactivation */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> paramOff = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool>(
			"Off", new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool("false"));

	public Trigger() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inOff);
		add(inOn);
		add(inReset);
		add(outActive);

		// Add all parameters
		add(paramOn);
		add(paramOff);
	}

	/**
	 * Creates a trigger module (providing a Boolean value that can be switched on
	 * or off)
	 *
	 * @param paramOn  Activation
	 * @param paramOff Deactivation
	 */
	public Trigger(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramOn,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIbool paramOff) {
		this();

		// Set the parameters
		setOn(paramOn);
		setOff(paramOff);
	}

	/**
	 * Creates a trigger module (providing a Boolean value that can be switched on
	 * or off)
	 *
	 * @param paramOn  Activation
	 * @param paramOff Deactivation
	 */
	public Trigger(Boolean paramOn, Boolean paramOff) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramOn),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(paramOff));
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
	 * Deactivation trigger
	 *
	 * @return the input port of the block
	 */
	public final InPort getInOff() {
		return this.inOff;
	}

	/**
	 * Activation trigger
	 *
	 * @return the input port of the block
	 */
	public final InPort getInOn() {
		return this.inOn;
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
	 * Activation
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutActive() {
		return this.outActive;
	}

	/**
	 * Activation
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getOn() {
		return this.paramOn;
	}

	/**
	 * Sets a parameter of the block: Activation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setOn(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramOn.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Activation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setOn(Boolean value) {
		this.setOn(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

	/**
	 * Deactivation
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIbool> getOff() {
		return this.paramOff;
	}

	/**
	 * Sets a parameter of the block: Deactivation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setOff(org.roboticsapi.facet.runtime.rpi.core.types.RPIbool value) {
		this.paramOff.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Deactivation
	 * 
	 * @param value new value of the parameter
	 */
	public final void setOff(Boolean value) {
		this.setOff(new org.roboticsapi.facet.runtime.rpi.core.types.RPIbool(value));
	}

}
