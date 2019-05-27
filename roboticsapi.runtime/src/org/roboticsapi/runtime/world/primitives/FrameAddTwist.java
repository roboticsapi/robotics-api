package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Applies a twist to a frame
 */
public class FrameAddTwist extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameAddTwist";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Frame */
	private final InPort inFrame = new InPort("inFrame");

	/** Twist to add */
	private final InPort inTwist = new InPort("inTwist");

	/** Changed frame */
	private final OutPort outValue = new OutPort("outValue");

	public FrameAddTwist() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFrame);
		add(inTwist);
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
	 * Frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrame() {
		return this.inFrame;
	}

	/**
	 * Twist to add
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTwist() {
		return this.inTwist;
	}

	/**
	 * Changed frame
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
