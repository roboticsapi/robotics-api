package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A bezier interpolation module for a double value (Interpolates between from
 * and to with given velocities)
 */
public class DoubleBezier extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleBezier";

	/** Start position */
	private final InPort inFrom = new InPort("inFrom");

	/** Velocity at start position */
	private final InPort inFromVel = new InPort("inFromVel");

	/** Destination position */
	private final InPort inTo = new InPort("inTo");

	/** Velocity at destination position */
	private final InPort inToVel = new InPort("inToVel");

	/** Progress value (0 <= inValue <= 1) */
	private final InPort inValue = new InPort("inValue");

	/** Interpolated value */
	private final OutPort outValue = new OutPort("outValue");

	/** Start position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramFrom = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"From", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Destination position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramTo = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"To", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Velocity at start position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramFromVel = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"FromVel", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Velocity at destination position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramToVel = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"ToVel", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public DoubleBezier() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inFrom);
		add(inFromVel);
		add(inTo);
		add(inToVel);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramFrom);
		add(paramTo);
		add(paramFromVel);
		add(paramToVel);
	}

	/**
	 * Creates a bezier interpolation module for a double value (Interpolates
	 * between from and to with given velocities)
	 *
	 * @param paramFrom    Start position
	 * @param paramTo      Destination position
	 * @param paramFromVel Velocity at start position
	 * @param paramToVel   Velocity at destination position
	 */
	public DoubleBezier(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramFrom,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramTo,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramFromVel,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramToVel) {
		this();

		// Set the parameters
		setFrom(paramFrom);
		setTo(paramTo);
		setFromVel(paramFromVel);
		setToVel(paramToVel);
	}

	/**
	 * Creates a bezier interpolation module for a double value (Interpolates
	 * between from and to with given velocities)
	 *
	 * @param paramFrom    Start position
	 * @param paramTo      Destination position
	 * @param paramFromVel Velocity at start position
	 * @param paramToVel   Velocity at destination position
	 */
	public DoubleBezier(Double paramFrom, Double paramTo, Double paramFromVel, Double paramToVel) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramFrom),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramTo),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramFromVel),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramToVel));
	}

	/**
	 * Start position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrom() {
		return this.inFrom;
	}

	/**
	 * Velocity at start position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFromVel() {
		return this.inFromVel;
	}

	/**
	 * Destination position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTo() {
		return this.inTo;
	}

	/**
	 * Velocity at destination position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInToVel() {
		return this.inToVel;
	}

	/**
	 * Progress value (0 <= inValue <= 1)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getFrom() {
		return this.paramFrom;
	}

	/**
	 * Sets a parameter of the block: Start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFrom(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramFrom.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFrom(Double value) {
		this.setFrom(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Destination position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getTo() {
		return this.paramTo;
	}

	/**
	 * Sets a parameter of the block: Destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTo(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramTo.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTo(Double value) {
		this.setTo(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Velocity at start position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getFromVel() {
		return this.paramFromVel;
	}

	/**
	 * Sets a parameter of the block: Velocity at start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFromVel(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramFromVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Velocity at start position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFromVel(Double value) {
		this.setFromVel(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Velocity at destination position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getToVel() {
		return this.paramToVel;
	}

	/**
	 * Sets a parameter of the block: Velocity at destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setToVel(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramToVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Velocity at destination position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setToVel(Double value) {
		this.setToVel(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
