package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A rotation splitter (extracts A, B, C values from Rotation type)
 */
public class RotationToABC extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationToABC";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Combined rotation */
	private final InPort inValue = new InPort("inValue");

	/** A rotation (around Z) */
	private final OutPort outA = new OutPort("outA");

	/** B rotation (around Y) */
	private final OutPort outB = new OutPort("outB");

	/** C rotation (around X) */
	private final OutPort outC = new OutPort("outC");

	public RotationToABC() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outA);
		add(outB);
		add(outC);

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
	 * Combined rotation
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * A rotation (around Z)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutA() {
		return this.outA;
	}

	/**
	 * B rotation (around Y)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutB() {
		return this.outB;
	}

	/**
	 * C rotation (around X)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutC() {
		return this.outC;
	}

}
