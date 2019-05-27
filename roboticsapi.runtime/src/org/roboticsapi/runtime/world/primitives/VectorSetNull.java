package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Returns a value or null if requested
 */
public class VectorSetNull extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorSetNull";

	/** return null */
	private final InPort inNull = new InPort("inNull");

	/** value to return */
	private final InPort inValue = new InPort("inValue");

	/** null if inNull is true, otherwise value */
	private final OutPort outValue = new OutPort("outValue");

	public VectorSetNull() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inNull);
		add(inValue);
		add(outValue);

		// Add all parameters
	}

	/**
	 * return null
	 *
	 * @return the input port of the block
	 */
	public final InPort getInNull() {
		return this.inNull;
	}

	/**
	 * value to return
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * null if inNull is true, otherwise value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
