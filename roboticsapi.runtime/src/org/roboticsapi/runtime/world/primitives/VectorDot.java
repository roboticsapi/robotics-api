package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A vector dot product module
 */
public class VectorDot extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorDot";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** First vector */
	private final InPort inFirst = new InPort("inFirst");

	/** Second vector */
	private final InPort inSecond = new InPort("inSecond");

	/** Dot product (First * Second) */
	private final OutPort outValue = new OutPort("outValue");

	public VectorDot() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFirst);
		add(inSecond);
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
	 * First vector
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * Second vector
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * Dot product (First * Second)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
