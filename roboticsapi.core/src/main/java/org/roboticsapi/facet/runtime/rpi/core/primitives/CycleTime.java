package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * 
 */
public class CycleTime extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::CycleTime";

	/** cycle time in current net */
	private final OutPort outValue = new OutPort("outValue");

	public CycleTime() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outValue);

		// Add all parameters
	}

	/**
	 * cycle time in current net
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
