package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A twist to XYZ module (extracts values for X, Y, Z velocities and A, B, C
 * velocities) [deprecated]
 */
@Deprecated
public class TwistToXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistToXYZ";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Combined twist */
	private final InPort inValue = new InPort("inValue");

	/** A velocity */
	private final OutPort outA = new OutPort("outA");

	/** B velocity */
	private final OutPort outB = new OutPort("outB");

	/** C velocity */
	private final OutPort outC = new OutPort("outC");

	/** X velocity */
	private final OutPort outX = new OutPort("outX");

	/** Y velocity */
	private final OutPort outY = new OutPort("outY");

	/** Z velocity */
	private final OutPort outZ = new OutPort("outZ");

	public TwistToXYZ() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outA);
		add(outB);
		add(outC);
		add(outX);
		add(outY);
		add(outZ);

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
	 * Combined twist
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * A velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutA() {
		return this.outA;
	}

	/**
	 * B velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutB() {
		return this.outB;
	}

	/**
	 * C velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutC() {
		return this.outC;
	}

	/**
	 * X velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutX() {
		return this.outX;
	}

	/**
	 * Y velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutY() {
		return this.outY;
	}

	/**
	 * Z velocity
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutZ() {
		return this.outZ;
	}

}
