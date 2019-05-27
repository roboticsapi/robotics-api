package org.roboticsapi.runtime.core.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * An interval scaler module (scales values from interval [Min,Max] to [0,1])
 */
public class Interval extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::Interval";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Maximum value (mapped to 1) */
	private final InPort inMax = new InPort("inMax");

	/** Minimum value (mapped to 0) */
	private final InPort inMin = new InPort("inMin");

	/** Current value */
	private final InPort inValue = new InPort("inValue");

	/** Follow-up activating (true if Min <= inValue <= Max) */
	private final OutPort outActive = new OutPort("outActive");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Start tick */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramMin = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Min", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** End tick */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramMax = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Max", new org.roboticsapi.runtime.core.types.RPIdouble("1000"));

	public Interval() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inMax);
		add(inMin);
		add(inValue);
		add(outActive);
		add(outValue);

		// Add all parameters
		add(paramMin);
		add(paramMax);
	}

	/**
	 * Creates an interval scaler module (scales values from interval [Min,Max] to
	 * [0,1])
	 *
	 * @param paramMin Start tick
	 * @param paramMax End tick
	 */
	public Interval(org.roboticsapi.runtime.core.types.RPIdouble paramMin,
			org.roboticsapi.runtime.core.types.RPIdouble paramMax) {
		this();

		// Set the parameters
		setMin(paramMin);
		setMax(paramMax);
	}

	/**
	 * Creates an interval scaler module (scales values from interval [Min,Max] to
	 * [0,1])
	 *
	 * @param paramMin Start tick
	 * @param paramMax End tick
	 */
	public Interval(Double paramMin, Double paramMax) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramMin),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramMax));
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
	 * Maximum value (mapped to 1)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMax() {
		return this.inMax;
	}

	/**
	 * Minimum value (mapped to 0)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMin() {
		return this.inMin;
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
	 * Follow-up activating (true if Min <= inValue <= Max)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutActive() {
		return this.outActive;
	}

	/**
	 * Result
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Start tick
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getMin() {
		return this.paramMin;
	}

	/**
	 * Sets a parameter of the block: Start tick
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMin(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramMin.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start tick
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMin(Double value) {
		this.setMin(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * End tick
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getMax() {
		return this.paramMax;
	}

	/**
	 * Sets a parameter of the block: End tick
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMax(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramMax.setValue(value);
	}

	/**
	 * Sets a parameter of the block: End tick
	 * 
	 * @param value new value of the parameter
	 */
	public final void setMax(Double value) {
		this.setMax(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
