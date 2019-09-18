package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A vector cross product module
 */
public class VectorCross extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorCross";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** First vector */
	private final InPort inFirst = new InPort("inFirst");

	/** Second vector */
	private final InPort inSecond = new InPort("inSecond");

	/** Combined vector (First x Second) */
	private final OutPort outValue = new OutPort("outValue");

	public VectorCross() {
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
	 * Combined vector (First x Second)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
