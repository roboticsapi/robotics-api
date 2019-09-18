
package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

/**
 * A Time range history module (from the history of the two given time ranges,
 * finds a bounded time range that contains both)
 */
public class TimeConsistentRange extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeConsistentRange";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** End time of the first range */
	private final InPort inFirstEnd = new InPort("inFirstEnd");

	/** Start time of the first range */
	private final InPort inFirstStart = new InPort("inFirstStart");

	/** Reset port */
	private final InPort inReset = new InPort("inReset");

	/** End time of the second range */
	private final InPort inSecondEnd = new InPort("inSecondEnd");

	/** Start time of the second range */
	private final InPort inSecondStart = new InPort("inSecondStart");

	/** End time of the resulting range */
	private final OutPort outEnd = new OutPort("outEnd");

	/** Start time of the resulting range */
	private final OutPort outStart = new OutPort("outStart");

	/** Maximum duration of the returned time range (s) */
	private final Parameter<RPIdouble> paramMaxSize = new Parameter<RPIdouble>("MaxSize", new RPIdouble("1"));

	/** Maximum age supported (s) */
	private final Parameter<RPIdouble> paramMaxAge = new Parameter<RPIdouble>("MaxAge", new RPIdouble("1"));

	public TimeConsistentRange() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFirstEnd);
		add(inFirstStart);
		add(inReset);
		add(inSecondEnd);
		add(inSecondStart);
		add(outEnd);
		add(outStart);

		// Add all parameters
		add(paramMaxSize);
		add(paramMaxAge);
	}

	/**
	 * Creates a Time range history module (from the history of the two given time
	 * ranges, finds a bounded time range that contains both)
	 *
	 * @param maxSize Maximum duration of the returned time range (s)
	 * @param maxAge  Maximum age supported (s)
	 */
	public TimeConsistentRange(RPIdouble paramMaxSize, RPIdouble paramMaxAge) {
		this();

		// Set the parameters
		setMaxSize(paramMaxSize);
		setMaxAge(paramMaxAge);
	}

	/**
	 * Creates a Time range history module (from the history of the two given time
	 * ranges, finds a bounded time range that contains both)
	 *
	 * @param maxSize Maximum duration of the returned time range (s)
	 * @param maxAge  Maximum age supported (s)
	 */
	public TimeConsistentRange(Double paramMaxSize, Double paramMaxAge) {
		this(new RPIdouble(paramMaxSize), new RPIdouble(paramMaxAge));
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
	 * End time of the first range
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirstEnd() {
		return this.inFirstEnd;
	}

	/**
	 * Start time of the first range
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFirstStart() {
		return this.inFirstStart;
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
	 * End time of the second range
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecondEnd() {
		return this.inSecondEnd;
	}

	/**
	 * Start time of the second range
	 *
	 * @return the input port of the block
	 */
	public final InPort getInSecondStart() {
		return this.inSecondStart;
	}

	/**
	 * End time of the resulting range
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutEnd() {
		return this.outEnd;
	}

	/**
	 * Start time of the resulting range
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutStart() {
		return this.outStart;
	}

	/**
	 * Maximum duration of the returned time range (s)
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getMaxSize() {
		return this.paramMaxSize;
	}

	/**
	 * Sets a parameter of the block: Maximum duration of the returned time range
	 * (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxSize(RPIdouble value) {
		this.paramMaxSize.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Maximum duration of the returned time range
	 * (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setMaxSize(Double value) {
		this.setMaxSize(new RPIdouble(value));
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
