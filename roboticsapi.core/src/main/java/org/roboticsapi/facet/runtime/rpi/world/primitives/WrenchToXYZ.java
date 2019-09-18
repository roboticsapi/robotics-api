package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A wrench to XYZ module (extracts values for X, Y, Z forces and A, B, C
 * torques)
 */
public class WrenchToXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::WrenchToXYZ";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Combined wrench */
	private final InPort inValue = new InPort("inValue");

	/** A torque */
	private final OutPort outA = new OutPort("outA");

	/** B torque */
	private final OutPort outB = new OutPort("outB");

	/** C torque */
	private final OutPort outC = new OutPort("outC");

	/** X force */
	private final OutPort outX = new OutPort("outX");

	/** Y force */
	private final OutPort outY = new OutPort("outY");

	/** Z force */
	private final OutPort outZ = new OutPort("outZ");

	public WrenchToXYZ() {
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
	 * Combined wrench
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * A torque
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutA() {
		return this.outA;
	}

	/**
	 * B torque
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutB() {
		return this.outB;
	}

	/**
	 * C torque
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutC() {
		return this.outC;
	}

	/**
	 * X force
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutX() {
		return this.outX;
	}

	/**
	 * Y force
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutY() {
		return this.outY;
	}

	/**
	 * Z force
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutZ() {
		return this.outZ;
	}

}
