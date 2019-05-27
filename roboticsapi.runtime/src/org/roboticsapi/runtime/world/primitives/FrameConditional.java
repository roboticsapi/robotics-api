package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Conditional
 */
public class FrameConditional extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameConditional";

	/** Condition */
	private final InPort inCondition = new InPort("inCondition");

	/** Frame if condition is false */
	private final InPort inFalse = new InPort("inFalse");

	/** Frame if condition is true */
	private final InPort inTrue = new InPort("inTrue");

	/** Result */
	private final OutPort outValue = new OutPort("outValue");

	/** Value if true */
	private final Parameter<org.roboticsapi.runtime.world.types.RPIFrame> paramTrue = new Parameter<org.roboticsapi.runtime.world.types.RPIFrame>(
			"True", new org.roboticsapi.runtime.world.types.RPIFrame("{pos:{x:0,y:0,z:0},rot:{a:0,b:-0,c:0}}"));

	/** Value if false */
	private final Parameter<org.roboticsapi.runtime.world.types.RPIFrame> paramFalse = new Parameter<org.roboticsapi.runtime.world.types.RPIFrame>(
			"False", new org.roboticsapi.runtime.world.types.RPIFrame("{pos:{x:0,y:0,z:0},rot:{a:0,b:-0,c:0}}"));

	public FrameConditional() {
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
	public FrameConditional(org.roboticsapi.runtime.world.types.RPIFrame paramTrue,
			org.roboticsapi.runtime.world.types.RPIFrame paramFalse) {
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
	public FrameConditional(String paramTrue, String paramFalse) {
		this(new org.roboticsapi.runtime.world.types.RPIFrame(paramTrue),
				new org.roboticsapi.runtime.world.types.RPIFrame(paramFalse));
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
	public final Parameter<org.roboticsapi.runtime.world.types.RPIFrame> getTrue() {
		return this.paramTrue;
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(org.roboticsapi.runtime.world.types.RPIFrame value) {
		this.paramTrue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if true
	 * 
	 * @param value new value of the parameter
	 */
	public final void setTrue(String value) {
		this.setTrue(new org.roboticsapi.runtime.world.types.RPIFrame(value));
	}

	/**
	 * Value if false
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.world.types.RPIFrame> getFalse() {
		return this.paramFalse;
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(org.roboticsapi.runtime.world.types.RPIFrame value) {
		this.paramFalse.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value if false
	 * 
	 * @param value new value of the parameter
	 */
	public final void setFalse(String value) {
		this.setFalse(new org.roboticsapi.runtime.world.types.RPIFrame(value));
	}

}
