package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A twist add module
 */
public class TwistAdd extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistAdd";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** summand one */
	private final InPort inFirst = new InPort("inFirst");

	/** summand two */
	private final InPort inSecond = new InPort("inSecond");

	/** sum */
	private final OutPort outValue = new OutPort("outValue");

	public TwistAdd() {
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
	 * summand one
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * summand two
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * sum
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
