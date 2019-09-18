
package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;

/**
 * Retrieves the value at a given time (age)
 */
public class TimeAtTime extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::TimeAtTime";

	/** Age to return the value for (s) */
	private final InPort inAge = new InPort("inAge");

	/** Current value */
	private final InPort inValue = new InPort("inValue");

	/** Value at the given time */
	private final OutPort outValue = new OutPort("outValue");

	/** Age to return the value for (s) */
	private final Parameter<RPIdouble> paramAge = new Parameter<RPIdouble>("Age", new RPIdouble("0"));

	/** Maximum age supported (s) */
	private final Parameter<RPIdouble> paramMaxAge = new Parameter<RPIdouble>("MaxAge", new RPIdouble("1"));

	public TimeAtTime() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inAge);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramAge);
		add(paramMaxAge);
	}

	/**
	 * Creates retrieves the value at a given time (age)
	 *
	 * @param maxAge Maximum age supported (s)
	 */
	public TimeAtTime(RPIdouble paramMaxAge) {
		this();

		// Set the parameters
		setMaxAge(paramMaxAge);
	}

	/**
	 * Creates retrieves the value at a given time (age)
	 *
	 * @param maxAge Maximum age supported (s)
	 */
	public TimeAtTime(Double paramMaxAge) {
		this(new RPIdouble(paramMaxAge));
	}

	/**
	 * Creates retrieves the value at a given time (age)
	 *
	 * @param paramAge    Age to return the value for (s)
	 * @param paramMaxAge Maximum age supported (s)
	 */
	public TimeAtTime(RPIdouble paramAge, RPIdouble paramMaxAge) {
		this();

		// Set the parameters
		setAge(paramAge);
		setMaxAge(paramMaxAge);
	}

	/**
	 * Creates retrieves the value at a given time (age)
	 *
	 * @param paramAge    Age to return the value for (s)
	 * @param paramMaxAge Maximum age supported (s)
	 */
	public TimeAtTime(Double paramAge, Double paramMaxAge) {
		this(new RPIdouble(paramAge), new RPIdouble(paramMaxAge));
	}

	/**
	 * Age to return the value for (s)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInAge() {
		return this.inAge;
	}

	/**
	 * Current value
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Value at the given time
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Age to return the value for (s)
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getAge() {
		return this.paramAge;
	}

	/**
	 * Sets a parameter of the block: Age to return the value for (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setAge(RPIdouble value) {
		this.paramAge.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Age to return the value for (s)
	 *
	 * @param value new value of the parameter
	 */
	public final void setAge(Double value) {
		this.setAge(new RPIdouble(value));
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
