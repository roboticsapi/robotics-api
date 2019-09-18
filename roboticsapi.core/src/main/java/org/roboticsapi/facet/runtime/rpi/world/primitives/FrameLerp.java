package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Linear frame interpolation module
 */
public class FrameLerp extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameLerp";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Amount (0 = inFrom, 1 = inTo) */
	private final InPort inAmount = new InPort("inAmount");

	/** Start frame */
	private final InPort inFrom = new InPort("inFrom");

	/** Destination frame */
	private final InPort inTo = new InPort("inTo");

	/** Interpolated frame */
	private final OutPort outValue = new OutPort("outValue");

	public FrameLerp() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inAmount);
		add(inFrom);
		add(inTo);
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
	 * Amount (0 = inFrom, 1 = inTo)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInAmount() {
		return this.inAmount;
	}

	/**
	 * Start frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFrom() {
		return this.inFrom;
	}

	/**
	 * Destination frame
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTo() {
		return this.inTo;
	}

	/**
	 * Interpolated frame
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
