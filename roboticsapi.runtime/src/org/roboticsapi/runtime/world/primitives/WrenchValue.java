package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * value injection module
 */
public class WrenchValue extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::WrenchValue";

	/** Value */
	private final OutPort outValue = new OutPort("outValue");

	/** Value to output */
	private final Parameter<org.roboticsapi.runtime.world.types.RPIWrench> paramValue = new Parameter<org.roboticsapi.runtime.world.types.RPIWrench>(
			"Value", new org.roboticsapi.runtime.world.types.RPIWrench("{force:{x:0,y:0,z:0},torque:{x:0,y:0,z:0}}"));

	public WrenchValue() {
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
	public WrenchValue(org.roboticsapi.runtime.world.types.RPIWrench paramValue) {
		this();

		// Set the parameters
		setValue(paramValue);
	}

	/**
	 * Creates value injection module
	 *
	 * @param value Value to output
	 */
	public WrenchValue(String paramValue) {
		this(new org.roboticsapi.runtime.world.types.RPIWrench(paramValue));
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
	public final Parameter<org.roboticsapi.runtime.world.types.RPIWrench> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.runtime.world.types.RPIWrench value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value to output
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.runtime.world.types.RPIWrench(value));
	}

}
