package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

/**
 * An online trajectory generator for frames
 */
public class FrameOTG extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameOTG";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/**
	 * Current position (transformation from reference frame to moving frame)
	 */
	private final InPort inCurPos = new InPort("inCurPos");

	/**
	 * Current velocity (velocity of moving frame relative to reference frame,
	 * expressed in reference frame)
	 */
	private final InPort inCurVel = new InPort("inCurVel");

	/**
	 * Destination position (transformation from reference frame to moving frame)
	 */
	private final InPort inDestPos = new InPort("inDestPos");

	/**
	 * Destination velocity (velocity of target frame relative to reference frame,
	 * expressed in reference frame)
	 */
	private final InPort inDestVel = new InPort("inDestVel");

	/** Maximum rotational acceleration */
	private final InPort inMaxRotAcc = new InPort("inMaxRotAcc");

	/** Maximum rotational velocity */
	private final InPort inMaxRotVel = new InPort("inMaxRotVel");

	/** Maximum translational acceleration */
	private final InPort inMaxTransAcc = new InPort("inMaxTransAcc");

	/** Maximum translational velocity */
	private final InPort inMaxTransVel = new InPort("inMaxTransVel");

	/** New position (position of moving frame relative to reference frame) */
	private final OutPort outPos = new OutPort("outPos");

	/**
	 * New velocity (velocity of moving frame relative to reference frame, expressed
	 * in reference frame)
	 */
	private final OutPort outVel = new OutPort("outVel");

	/** Maximum translational velocity */
	private final Parameter<RPIdouble> paramMaxTransVel = new Parameter<RPIdouble>("MaxTransVel", new RPIdouble("1"));

	/** Maximum translational acceleration */
	private final Parameter<RPIdouble> paramMaxTransAcc = new Parameter<RPIdouble>("MaxTransAcc", new RPIdouble("1"));

	/** Maximum rotational velocity */
	private final Parameter<RPIdouble> paramMaxRotVel = new Parameter<RPIdouble>("MaxRotVel", new RPIdouble("1"));

	/** Maximum rotational acceleration */
	private final Parameter<RPIdouble> paramMaxRotAcc = new Parameter<RPIdouble>("MaxRotAcc", new RPIdouble("1"));

	public FrameOTG() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inCurPos);
		add(inCurVel);
		add(inDestPos);
		add(inDestVel);
		add(inMaxRotAcc);
		add(inMaxRotVel);
		add(inMaxTransAcc);
		add(inMaxTransVel);
		add(outPos);
		add(outVel);

		// Add all parameters
		add(paramMaxTransVel);
		add(paramMaxTransAcc);
		add(paramMaxRotVel);
		add(paramMaxRotAcc);
	}

	/**
	 * Creates an online trajectory generator for frames
	 *
	 * @param paramMaxTransVel Maximum translational velocity
	 * @param paramMaxTransAcc Maximum translational acceleration
	 * @param paramMaxRotVel   Maximum rotational velocity
	 * @param paramMaxRotAcc   Maximum rotational acceleration
	 */
	public FrameOTG(RPIdouble paramMaxTransVel, RPIdouble paramMaxTransAcc, RPIdouble paramMaxRotVel,
			RPIdouble paramMaxRotAcc) {
		this();

		// Set the parameters
		setMaxTransVel(paramMaxTransVel);
		setMaxTransAcc(paramMaxTransAcc);
		setMaxRotVel(paramMaxRotVel);
		setMaxRotAcc(paramMaxRotAcc);
	}

	/**
	 * Creates an online trajectory generator for frames
	 *
	 * @param paramMaxTransVel Maximum translational velocity
	 * @param paramMaxTransAcc Maximum translational acceleration
	 * @param paramMaxRotVel   Maximum rotational velocity
	 * @param paramMaxRotAcc   Maximum rotational acceleration
	 */
	public FrameOTG(Double paramMaxTransVel, Double paramMaxTransAcc, Double paramMaxRotVel, Double paramMaxRotAcc) {
		this(new RPIdouble(paramMaxTransVel), new RPIdouble(paramMaxTransAcc), new RPIdouble(paramMaxRotVel),
				new RPIdouble(paramMaxRotAcc));
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
	 * Current position (transformation from reference frame to moving frame)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurPos() {
		return this.inCurPos;
	}

	/**
	 * Current velocity (velocity of moving frame relative to reference frame,
	 * expressed in reference frame)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCurVel() {
		return this.inCurVel;
	}

	/**
	 * Destination position (transformation from reference frame to moving frame)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDestPos() {
		return this.inDestPos;
	}

	/**
	 * Destination velocity (Destination velocity (velocity of target frame relative
	 * to reference frame, expressed in reference frame)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDestVel() {
		return this.inDestVel;
	}

	/**
	 * Maximum rotational acceleration
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxRotAcc() {
		return this.inMaxRotAcc;
	}

	/**
	 * Maximum rotational velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxRotVel() {
		return this.inMaxRotVel;
	}

	/**
	 * Maximum translational acceleration
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxTransAcc() {
		return this.inMaxTransAcc;
	}

	/**
	 * Maximum translational velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMaxTransVel() {
		return this.inMaxTransVel;
	}

	/**
	 * New position (position of moving frame relative to reference frame)
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutPos() {
		return this.outPos;
	}

	/**
	 * New velocity (velocity of moving frame relative to reference frame, expressed
	 * in reference frame)
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutVel() {
		return this.outVel;
	}

	/**
	 * Maximum translational velocity
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxTransVel() {
		return this.paramMaxTransVel;
	}

	/**
	 * Sets a parameter of the block: Maximum translational velocity
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxTransVel(RPIdouble value) {
		this.paramMaxTransVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum translational velocity
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxTransVel(Double value) {
		this.setMaxTransVel(new RPIdouble(value));
	}

	/**
	 * Maximum translational acceleration
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxTransAcc() {
		return this.paramMaxTransAcc;
	}

	/**
	 * Sets a parameter of the block: Maximum translational acceleration
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxTransAcc(RPIdouble value) {
		this.paramMaxTransAcc.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum translational acceleration
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxTransAcc(Double value) {
		this.setMaxTransAcc(new RPIdouble(value));
	}

	/**
	 * Maximum rotational velocity
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxRotVel() {
		return this.paramMaxRotVel;
	}

	/**
	 * Sets a parameter of the block: Maximum rotational velocity
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxRotVel(RPIdouble value) {
		this.paramMaxRotVel.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum rotational velocity
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxRotVel(Double value) {
		this.setMaxRotVel(new RPIdouble(value));
	}

	/**
	 * Maximum rotational acceleration
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxRotAcc() {
		return this.paramMaxRotAcc;
	}

	/**
	 * Sets a parameter of the block: Maximum rotational acceleration
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxRotAcc(RPIdouble value) {
		this.paramMaxRotAcc.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum rotational acceleration
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxRotAcc(Double value) {
		this.setMaxRotAcc(new RPIdouble(value));
	}

}
