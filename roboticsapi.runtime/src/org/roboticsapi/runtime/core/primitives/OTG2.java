package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Online trajectory generator for single axis
 */
public class OTG2 extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::OTG2";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Current position */
	private final InPort inCurPos = new InPort("inCurPos");

	/** Current velocity */
	private final InPort inCurVel = new InPort("inCurVel");

	/** Desired position */
	private final InPort inDesPos = new InPort("inDesPos");

	/** Maximum acceleration planned by OTG */
	private final InPort inMaxAcc = new InPort("inMaxAcc");

	/** Maximum velocity planned by OTG */
	private final InPort inMaxVel = new InPort("inMaxVel");

	/** New acceleration */
	private final OutPort outAcc = new OutPort("outAcc");

	/** New position */
	private final OutPort outPos = new OutPort("outPos");

	/** New velocity */
	private final OutPort outVel = new OutPort("outVel");

	/** Maximum velocity planned by OTG */
	private final Parameter<RPIdouble> paramMaxVel = new Parameter<RPIdouble>("maxVel", new RPIdouble("0"));

	/** Maximum acceleration planned by OTG */
	private final Parameter<RPIdouble> paramMaxAcc = new Parameter<RPIdouble>("maxAcc", new RPIdouble("0"));

	public OTG2() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inCurPos);
		add(inCurVel);
		add(inDesPos);
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
	 * Creates online trajectory generator for single axis
	 *
	 * @param maxVel Maximum velocity planned by OTG
	 * @param maxAcc Maximum acceleration planned by OTG
	 */
	public OTG2(RPIdouble paramMaxVel, RPIdouble paramMaxAcc) {
		this();

		// Set the parameters
		setmaxVel(paramMaxVel);
		setmaxAcc(paramMaxAcc);
	}

	/**
	 * Creates online trajectory generator for single axis
	 *
	 * @param maxVel Maximum velocity planned by OTG
	 * @param maxAcc Maximum acceleration planned by OTG
	 */
	public OTG2(Double paramMaxVel, Double paramMaxAcc) {
		this(new RPIdouble(paramMaxVel), new RPIdouble(paramMaxAcc));
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
	 * Current position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurPos() {
		return this.inCurPos;
	}

	/**
	 * Current velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurVel() {
		return this.inCurVel;
	}

	/**
	 * Desired position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDesPos() {
		return this.inDesPos;
	}

	/**
	 * Maximum acceleration planned by OTG
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxAcc() {
		return this.inMaxAcc;
	}

	/**
	 * Maximum velocity planned by OTG
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxVel() {
		return this.inMaxVel;
	}

	/**
	 * New acceleration
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutAcc() {
		return this.outAcc;
	}

	/**
	 * New position
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutPos() {
		return this.outPos;
	}

	/**
	 * New velocity
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
	public final Parameter<RPIdouble> getmaxVel() {
		return this.paramMaxVel;
	}

	/**
	 * Sets a parameter of the block: Maximum velocity planned by OTG
	 *
	 * @param value new value of the parameter
	 */
	public final void setmaxVel(RPIdouble value) {
		this.paramMaxVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum velocity planned by OTG
	 *
	 * @param value new value of the parameter
	 */
	public final void setmaxVel(Double value) {
		this.setmaxVel(new RPIdouble(value));
	}

	/**
	 * Maximum acceleration planned by OTG
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getmaxAcc() {
		return this.paramMaxAcc;
	}

	/**
	 * Sets a parameter of the block: Maximum acceleration planned by OTG
	 *
	 * @param value new value of the parameter
	 */
	public final void setmaxAcc(RPIdouble value) {
		this.paramMaxAcc.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum acceleration planned by OTG
	 *
	 * @param value new value of the parameter
	 */
	public final void setmaxAcc(Double value) {
		this.setmaxAcc(new RPIdouble(value));
	}

}
