package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A cubic bezier interpolation module (Interpolates between from and to using
 * two control points)
 */
public class CubicBezier extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::CubicBezier";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** First control position */
	private final InPort inControl1 = new InPort("inControl1");

	/** Second control position */
	private final InPort inControl2 = new InPort("inControl2");

	/** Start position */
	private final InPort inFrom = new InPort("inFrom");

	/** Destination position */
	private final InPort inTo = new InPort("inTo");

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

	/** First control position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramControl1 = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Control1", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Second control position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramControl2 = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Control2", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public CubicBezier() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inControl1);
		add(inControl2);
		add(inFrom);
		add(inTo);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramFrom);
		add(paramTo);
		add(paramControl1);
		add(paramControl2);
	}

	/**
	 * Creates a cubic bezier interpolation module (Interpolates between from and to
	 * using two control points)
	 *
	 * @param paramFrom     Start position
	 * @param paramTo       Destination position
	 * @param paramControl1 First control position
	 * @param paramControl2 Second control position
	 */
	public CubicBezier(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramFrom,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramTo,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramControl1,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramControl2) {
		this();

		// Set the parameters
		setFrom(paramFrom);
		setTo(paramTo);
		setControl1(paramControl1);
		setControl2(paramControl2);
	}

	/**
	 * Creates a cubic bezier interpolation module (Interpolates between from and to
	 * using two control points)
	 *
	 * @param paramFrom     Start position
	 * @param paramTo       Destination position
	 * @param paramControl1 First control position
	 * @param paramControl2 Second control position
	 */
	public CubicBezier(Double paramFrom, Double paramTo, Double paramControl1, Double paramControl2) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramFrom),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramTo),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramControl1),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramControl2));
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
	 * First control position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInControl1() {
		return this.inControl1;
	}

	/**
	 * Second control position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInControl2() {
		return this.inControl2;
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
	 * Destination position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTo() {
		return this.inTo;
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
	 * First control position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getControl1() {
		return this.paramControl1;
	}

	/**
	 * Sets a parameter of the block: First control position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setControl1(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramControl1.setValue(value);
	}

	/**
	 * Sets a parameter of the block: First control position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setControl1(Double value) {
		this.setControl1(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Second control position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getControl2() {
		return this.paramControl2;
	}

	/**
	 * Sets a parameter of the block: Second control position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setControl2(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramControl2.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Second control position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setControl2(Double value) {
		this.setControl2(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
