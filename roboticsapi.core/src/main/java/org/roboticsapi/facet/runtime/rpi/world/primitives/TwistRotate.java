package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A twist rotation module
 */
public class TwistRotate extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistRotate";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Input rotation */
	private final InPort inRot = new InPort("inRot");

	/** Input twist */
	private final InPort inValue = new InPort("inValue");

	/** Rotated twist */
	private final OutPort outValue = new OutPort("outValue");

	public TwistRotate() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inRot);
		add(inValue);
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
	 * Input rotation
	 *
	 * @return the input port of the block
	 */
	public final InPort getInRot() {
		return this.inRot;
	}

	/**
	 * Input twist
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Rotated twist
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
