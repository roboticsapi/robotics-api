package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A twist to velocities module (extracts translational and rotational
 * velocities from a twist data type)
 */
public class TwistToVelocities extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistToVelocities";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Input twist */
	private final InPort inValue = new InPort("inValue");

	/** Rotational velocity */
	private final OutPort outRotVel = new OutPort("outRotVel");

	/** Translational velocity */
	private final OutPort outTransVel = new OutPort("outTransVel");

	public TwistToVelocities() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outRotVel);
		add(outTransVel);

		// Add all parameters
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
	 * Input twist
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Rotational velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutRotVel() {
		return this.outRotVel;
	}

	/**
	 * Translational velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutTransVel() {
		return this.outTransVel;
	}

}
