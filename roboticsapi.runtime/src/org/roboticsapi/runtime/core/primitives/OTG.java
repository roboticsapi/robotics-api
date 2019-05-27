package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Online Trajectory Generator for single axis
 */
public class OTG extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::OTG";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** current acceleration */
	private final InPort inCurAcc = new InPort("inCurAcc");

	/** current position */
	private final InPort inCurPos = new InPort("inCurPos");

	/** current velocity */
	private final InPort inCurVel = new InPort("inCurVel");

	/** destination position */
	private final InPort inDestPos = new InPort("inDestPos");

	/** destination velocity */
	private final InPort inDestVel = new InPort("inDestVel");

	/** maximum acceleration planned by OTG */
	private final InPort inMaxAcc = new InPort("inMaxAcc");

	/** maximum velocity planned by OTG */
	private final InPort inMaxVel = new InPort("inMaxVel");

	/** new acceleration */
	private final OutPort outAcc = new OutPort("outAcc");

	/** new position */
	private final OutPort outPos = new OutPort("outPos");

	/** new velocity */
	private final OutPort outVel = new OutPort("outVel");

	/** Maximum velocity planned by OTG */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramMaxVel = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"maxVel", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Maximum acceleration planned by OTG */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramMaxAcc = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"maxAcc", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public OTG() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inCurAcc);
		add(inCurPos);
		add(inCurVel);
		add(inDestPos);
		add(inDestVel);
		add(inMaxAcc);
		add(inMaxVel);
		add(outAcc);
		add(outPos);
		add(outVel);

		// Add all parameters
		add(paramMaxVel);
		add(paramMaxAcc);
	}

	/**
	 * Creates online Trajectory Generator for single axis
	 *
	 * @param maxVel Maximum velocity planned by OTG
	 * @param maxAcc Maximum acceleration planned by OTG
	 */
	public OTG(org.roboticsapi.runtime.core.types.RPIdouble paramMaxVel,
			org.roboticsapi.runtime.core.types.RPIdouble paramMaxAcc) {
		this();

		// Set the parameters
		setmaxVel(paramMaxVel);
		setmaxAcc(paramMaxAcc);
	}

	/**
	 * Creates online Trajectory Generator for single axis
	 *
	 * @param maxVel Maximum velocity planned by OTG
	 * @param maxAcc Maximum acceleration planned by OTG
	 */
	public OTG(Double paramMaxVel, Double paramMaxAcc) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramMaxVel),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramMaxAcc));
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
	 * current acceleration
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurAcc() {
		return this.inCurAcc;
	}

	/**
	 * current position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurPos() {
		return this.inCurPos;
	}

	/**
	 * current velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurVel() {
		return this.inCurVel;
	}

	/**
	 * destination position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDestPos() {
		return this.inDestPos;
	}

	/**
	 * destination velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDestVel() {
		return this.inDestVel;
	}

	/**
	 * maximum acceleration planned by OTG
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxAcc() {
		return this.inMaxAcc;
	}

	/**
	 * maximum velocity planned by OTG
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxVel() {
		return this.inMaxVel;
	}

	/**
	 * new acceleration
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAcc() {
		return this.outAcc;
	}

	/**
	 * new position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutPos() {
		return this.outPos;
	}

	/**
	 * new velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutVel() {
		return this.outVel;
	}

	/**
	 * Maximum velocity planned by OTG
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getmaxVel() {
		return this.paramMaxVel;
	}

	/**
	 * Sets a parameter of the block: Maximum velocity planned by OTG
	 * 
	 * @param value new value of the parameter
	 */
	public final void setmaxVel(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramMaxVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum velocity planned by OTG
	 * 
	 * @param value new value of the parameter
	 */
	public final void setmaxVel(Double value) {
		this.setmaxVel(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Maximum acceleration planned by OTG
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getmaxAcc() {
		return this.paramMaxAcc;
	}

	/**
	 * Sets a parameter of the block: Maximum acceleration planned by OTG
	 * 
	 * @param value new value of the parameter
	 */
	public final void setmaxAcc(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramMaxAcc.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum acceleration planned by OTG
	 * 
	 * @param value new value of the parameter
	 */
	public final void setmaxAcc(Double value) {
		this.setmaxAcc(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
