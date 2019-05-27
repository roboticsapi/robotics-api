package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A vector scale module
 */
public class VectorScale extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorScale";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Scale factor */
	private final InPort inFactor = new InPort("inFactor");

	/** Input vector */
	private final InPort inValue = new InPort("inValue");

	/** Scaled vector (Vector * Factor) */
	private final OutPort outValue = new OutPort("outValue");

	/** Scale factor */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramFactor = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Factor", new org.roboticsapi.runtime.core.types.RPIdouble("1"));

	public VectorScale() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inFactor);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramFactor);
	}

	/**
	 * Creates a vector scale module
	 *
	 * @param paramFactor Scale factor
	 */
	public VectorScale(org.roboticsapi.runtime.core.types.RPIdouble paramFactor) {
		this();

		// Set the parameters
		setFactor(paramFactor);
	}

	/**
	 * Creates a vector scale module
	 *
	 * @param paramFactor Scale factor
	 */
	public VectorScale(Double paramFactor) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramFactor));
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
	 * Scale factor
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFactor() {
		return this.inFactor;
	}

	/**
	 * Input vector
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Scaled vector (Vector * Factor)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Scale factor
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getFactor() {
		return this.paramFactor;
	}

	/**
	 * Sets a parameter of the block: Scale factor
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFactor(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramFactor.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Scale factor
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFactor(Double value) {
		this.setFactor(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
