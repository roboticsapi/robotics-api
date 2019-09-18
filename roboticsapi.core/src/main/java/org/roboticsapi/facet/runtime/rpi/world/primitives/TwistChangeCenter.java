package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A twist change rotatation center module
 */
public class TwistChangeCenter extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistChangeCenter";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Input twist */
	private final InPort inValue = new InPort("inValue");

	/** Vector to move the rotation center */
	private final InPort inVector = new InPort("inVector");

	/** Changed twist */
	private final OutPort outValue = new OutPort("outValue");

	public TwistChangeCenter() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(inVector);
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
	 * Input twist
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Vector to move the rotation center
	 *
	 * @return the input port of the block
	 */
	public final InPort getInVector() {
		return this.inVector;
	}

	/**
	 * Changed twist
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
