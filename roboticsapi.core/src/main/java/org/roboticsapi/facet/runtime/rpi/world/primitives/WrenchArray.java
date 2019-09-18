package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Creates an array.
 */
public class WrenchArray extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::WrenchArray";

	/** Value */
	private final OutPort outArray = new OutPort("outArray");

	/** Size of the array */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramSize = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Size", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("1"));

	public WrenchArray() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outArray);

		// Add all parameters
		add(paramSize);
	}

	/**
	 * Creates creates an array.
	 *
	 * @param size Size of the array
	 */
	public WrenchArray(org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramSize) {
		this();

		// Set the parameters
		setSize(paramSize);
	}

	/**
	 * Creates creates an array.
	 *
	 * @param size Size of the array
	 */
	public WrenchArray(Integer paramSize) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramSize));
	}

	/**
	 * Value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutArray() {
		return this.outArray;
	}

	/**
	 * Size of the array
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getSize() {
		return this.paramSize;
	}

	/**
	 * Sets a parameter of the block: Size of the array
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramSize.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Size of the array
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(Integer value) {
		this.setSize(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

}
