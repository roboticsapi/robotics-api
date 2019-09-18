
package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

/**
 * A Time history module (finds when a certain time was provided)
 */
public class TimeHistory extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeHistory";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** Time stamp to search for (needle) */
	private final InPort inTime = new InPort("inTime");

	/** Time stamp provided (haystack) */
	private final InPort inValue = new InPort("inValue");

	/** Time when the time stamp occured [s] */
	private final OutPort outAge = new OutPort("outAge");

	/** Maximum age supported (s) */
	private final Parameter<RPIdouble> paramMaxAge = new Parameter<RPIdouble>("MaxAge", new RPIdouble("1"));

	public TimeHistory() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inReset);
		add(inTime);
		add(inValue);
		add(outAge);

		// Add all parameters
		add(paramMaxAge);
	}

	/**
	 * Creates a Time history module (finds when a certain time was provided)
	 *
	 * @param maxAge Maximum age supported (s)
	 */
	public TimeHistory(RPIdouble paramMaxAge) {
		this();

		// Set the parameters
		setMaxAge(paramMaxAge);
	}

	/**
	 * Creates a Time history module (finds when a certain time was provided)
	 *
	 * @param maxAge Maximum age supported (s)
	 */
	public TimeHistory(Double paramMaxAge) {
		this(new RPIdouble(paramMaxAge));
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
	 * Time stamp to search for (needle)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTime() {
		return this.inTime;
	}

	/**
	 * Time stamp provided (haystack)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Time when the time stamp occured [s]
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutAge() {
		return this.outAge;
	}

	/**
	 * Maximum age supported (s)
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxAge() {
		return this.paramMaxAge;
	}

	/**
	 * Sets a parameter of the block: Maximum age supported (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxAge(RPIdouble value) {
		this.paramMaxAge.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum age supported (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxAge(Double value) {
		this.setMaxAge(new RPIdouble(value));
	}

}
