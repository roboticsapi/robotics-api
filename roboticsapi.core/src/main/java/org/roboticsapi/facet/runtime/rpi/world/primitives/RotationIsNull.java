package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Checks if a value is null
 */
public class RotationIsNull extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationIsNull";

	/** value */
	private final InPort inValue = new InPort("inValue");

	/** true if value is null */
	private final OutPort outValue = new OutPort("outValue");

	public RotationIsNull() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);
		add(outValue);

		// Add all parameters
	}

	/**
	 * value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * true if value is null
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
