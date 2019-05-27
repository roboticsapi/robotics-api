package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A time module (returns the current time)
 */
public class TimeDiff extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeDiff";

	/** The first timestamp in nsecs */
	private final InPort inFirst = new InPort("inFirst");

	/** The second timestamp in nsecs */
	private final InPort inSecond = new InPort("inSecond");

	/** The time difference (second - first) in seconds */
	private final OutPort outValue = new OutPort("outValue");

	public TimeDiff() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inFirst);
		add(inSecond);
		add(outValue);

		// Add all parameters
	}

	/**
	 * The first timestamp in nsecs
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirst() {
		return this.inFirst;
	}

	/**
	 * The second timestamp in nsecs
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecond() {
		return this.inSecond;
	}

	/**
	 * The time difference (second - first) in seconds
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

}
