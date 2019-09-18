package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Conditional
 */
public class VectorConditional extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorConditional";

	/** Condition */
	private final InPort inCondition = new InPort("inCondition");

	/** Frame if condition is false */
	private final InPort inFalse = new InPort("inFalse");

	/** Frame if condition is true */
	private final InPort inTrue = new InPort("inTrue");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Value if true */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector> paramTrue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector>(
			"True", new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector("{x:0,y:0,z:0}"));

	/** Value if false */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector> paramFalse = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector>(
			"False", new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector("{x:0,y:0,z:0}"));

	public VectorConditional() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inCondition);
		add(inFalse);
		add(inTrue);
		add(outValue);

		// Add all parameters
		add(paramTrue);
		add(paramFalse);
	}

	/**
	 * Creates conditional
	 *
	 * @param paramTrue  Value if true
	 * @param paramFalse Value if false
	 */
	public VectorConditional(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector paramTrue,
			org.roboticsapi.facet.runtime.rpi.world.types.RPIVector paramFalse) {
		this();

		// Set the parameters
		setTrue(paramTrue);
		setFalse(paramFalse);
	}

	/**
	 * Creates conditional
	 *
	 * @param paramTrue  Value if true
	 * @param paramFalse Value if false
	 */
	public VectorConditional(String paramTrue, String paramFalse) {
		this(new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector(paramTrue),
				new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector(paramFalse));
	}

	/**
	 * Condition
	 *
	 * @return the input port of the block
	 */
	public final InPort getInCondition() {
		return this.inCondition;
	}

	/**
	 * Frame if condition is false
	 *
	 * @return the input port of the block
	 */
	public final InPort getInFalse() {
		return this.inFalse;
	}

	/**
	 * Frame if condition is true
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTrue() {
		return this.inTrue;
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
	 * Value if true
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector> getTrue() {
		return this.paramTrue;
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector value) {
		this.paramTrue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(String value) {
		this.setTrue(new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector(value));
	}

	/**
	 * Value if false
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIVector> getFalse() {
		return this.paramFalse;
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector value) {
		this.paramFalse.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(String value) {
		this.setFalse(new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector(value));
	}

}
