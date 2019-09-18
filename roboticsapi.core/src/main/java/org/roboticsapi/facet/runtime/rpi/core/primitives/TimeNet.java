package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A time module, returns the current (logical, net) time
 */
public class TimeNet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeNet";

	/** The current time in ticks */
	private final OutPort outValue = new OutPort("outValue");

	public TimeNet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outValue);

		// Add all parameters
	}

	/**
	 * The current time in ticks
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
