package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * value injection module
 */
public class FrameValue extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameValue";

	/** Value */
	private final OutPort outValue = new OutPort("outValue");

	/** Value to output */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame>(
			"Value",
			new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame("{pos:{x:0,y:0,z:0},rot:{a:0,b:-0,c:0}}"));

	public FrameValue() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outValue);

		// Add all parameters
		add(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public FrameValue(org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public FrameValue(String paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame(paramValue));
	}

	/**
	 * Value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Value to output
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame(value));
	}

}
