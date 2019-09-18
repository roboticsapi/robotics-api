package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A rotation splitter module (calculates a quaternion from a rotation)
 */
public class RotationToQuaternion extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationToQuaternion";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Rotation */
	private final InPort inValue = new InPort("inValue");

	/** Quaternion W value */
	private final OutPort outW = new OutPort("outW");

	/** Quaternion X value */
	private final OutPort outX = new OutPort("outX");

	/** Quaternion Y value */
	private final OutPort outY = new OutPort("outY");

	/** Quaternion Z value */
	private final OutPort outZ = new OutPort("outZ");

	public RotationToQuaternion() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outW);
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
	 * Rotation
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Quaternion W value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutW() {
		return this.outW;
	}

	/**
	 * Quaternion X value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutX() {
		return this.outX;
	}

	/**
	 * Quaternion Y value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutY() {
		return this.outY;
	}

	/**
	 * Quaternion Z value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutZ() {
		return this.outZ;
	}

}
