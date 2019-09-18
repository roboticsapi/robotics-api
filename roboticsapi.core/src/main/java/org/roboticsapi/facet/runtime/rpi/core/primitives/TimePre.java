package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * 
 */
public class TimePre extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimePre";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Input value */
	private final InPort inValue = new InPort("inValue");

	/** Output value */
	private final OutPort outValue = new OutPort("outValue");

	public TimePre() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
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
	 * Reset port
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInReset() {
		return this.inReset;
	}

	/**
	 * Input value
	 * 
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Output value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
