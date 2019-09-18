package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Conditional
 */
public class TwistConditional extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistConditional";

	/** Condition */
	private final InPort inCondition = new InPort("inCondition");

	/** Frame if condition is false */
	private final InPort inFalse = new InPort("inFalse");

	/** Frame if condition is true */
	private final InPort inTrue = new InPort("inTrue");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Value if true */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist> paramTrue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist>(
			"True",
			new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist("{vel:{x:0,y:0,z:0},rot:{x:0,y:0,z:0}}"));

	/** Value if false */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist> paramFalse = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist>(
			"False",
			new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist("{vel:{x:0,y:0,z:0},rot:{x:0,y:0,z:0}}"));

	public TwistConditional() {
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
	public TwistConditional(org.roboticsapi.facet.runtime.rpi.world.types.RPITwist paramTrue,
			org.roboticsapi.facet.runtime.rpi.world.types.RPITwist paramFalse) {
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
	public TwistConditional(String paramTrue, String paramFalse) {
		this(new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist(paramTrue),
				new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist(paramFalse));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist> getTrue() {
		return this.paramTrue;
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(org.roboticsapi.facet.runtime.rpi.world.types.RPITwist value) {
		this.paramTrue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(String value) {
		this.setTrue(new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist(value));
	}

	/**
	 * Value if false
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPITwist> getFalse() {
		return this.paramFalse;
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(org.roboticsapi.facet.runtime.rpi.world.types.RPITwist value) {
		this.paramFalse.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(String value) {
		this.setFalse(new org.roboticsapi.facet.runtime.rpi.world.types.RPITwist(value));
	}

}
