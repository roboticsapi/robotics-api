
package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A time add module (returns a time after applying a delta)
 */
public class TimeAdd extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeAdd";

	/** The time difference [s] */
	private final InPort inDelta = new InPort("inDelta");

	/** The first timestamp [nsecs] */
	private final InPort inTime = new InPort("inTime");

	/** The added time (first + second) [nsecs] */
	private final OutPort outValue = new OutPort("outValue");

	public TimeAdd() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inDelta);
		add(inTime);
		add(outValue);

		// Add all parameters
	}

	/**
	 * The time difference [s]
	 *
	 * @return the input port of the block
	 */
	public final InPort getInDelta() {
		return this.inDelta;
	}

	/**
	 * The first timestamp [nsecs]
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTime() {
		return this.inTime;
	}

	/**
	 * The added time (first + second) [nsecs]
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
