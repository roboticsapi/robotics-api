package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A bezier interpolation module for a rotation (Interpolates between from and
 * to with given rotational velocities)
 */
public class RotationBezier extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationBezier";

	/** Start rotation */
	private final InPort inFrom = new InPort("inFrom");

	/** Velocity at start position */
	private final InPort inFromVel = new InPort("inFromVel");

	/** Destination rotation */
	private final InPort inTo = new InPort("inTo");

	/** Velocity at destination position */
	private final InPort inToVel = new InPort("inToVel");

	/** Progress value (0 <= inValue <= 1) */
	private final InPort inValue = new InPort("inValue");

	/** Interpolated value */
	private final OutPort outValue = new OutPort("outValue");

	public RotationBezier() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inFrom);
		add(inFromVel);
		add(inTo);
		add(inToVel);
		add(inValue);
		add(outValue);

		// Add all parameters
	}

	/**
	 * Start rotation
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
	 * Destination rotation
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

}
