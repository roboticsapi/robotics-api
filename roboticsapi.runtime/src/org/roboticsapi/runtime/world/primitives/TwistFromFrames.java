package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Calculates a twist from two frames
 */
public class TwistFromFrames extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistFromFrames";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Current frame */
	private final InPort inFrame = new InPort("inFrame");

	/** Previous frame */
	private final InPort inPrevFrame = new InPort("inPrevFrame");

	/** Twist applied between the two frames */
	private final OutPort outValue = new OutPort("outValue");

	public TwistFromFrames() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFrame);
		add(inPrevFrame);
		add(outValue);

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
	 * Current frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrame() {
		return this.inFrame;
	}

	/**
	 * Previous frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInPrevFrame() {
		return this.inPrevFrame;
	}

	/**
	 * Twist applied between the two frames
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
