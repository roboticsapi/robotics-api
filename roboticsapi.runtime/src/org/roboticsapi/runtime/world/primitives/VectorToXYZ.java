package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A vector to XYZ module (extracts values for X, Y, Z from a vector datatype)
 */
public class VectorToXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorToXYZ";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Input vector */
	private final InPort inValue = new InPort("inValue");

	/** X position */
	private final OutPort outX = new OutPort("outX");

	/** Y position */
	private final OutPort outY = new OutPort("outY");

	/** Z position */
	private final OutPort outZ = new OutPort("outZ");

	public VectorToXYZ() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
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
	 * Input vector
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * X position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutX() {
		return this.outX;
	}

	/**
	 * Y position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutY() {
		return this.outY;
	}

	/**
	 * Z position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutZ() {
		return this.outZ;
	}

}
