package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A frame splitter module (extracts position and rotation form a frame
 * datatype)
 */
public class FrameToPosRot extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameToPosRot";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Combined frame */
	private final InPort inValue = new InPort("inValue");

	/** Position */
	private final OutPort outPosition = new OutPort("outPosition");

	/** Rotation */
	private final OutPort outRotation = new OutPort("outRotation");

	public FrameToPosRot() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outPosition);
		add(outRotation);

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
	 * Combined frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Position
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutPosition() {
		return this.outPosition;
	}

	/**
	 * Rotation
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutRotation() {
		return this.outRotation;
	}

}
