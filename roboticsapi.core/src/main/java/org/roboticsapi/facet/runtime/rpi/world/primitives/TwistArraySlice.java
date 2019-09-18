package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Extracts a part of an array.
 */
public class TwistArraySlice extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistArraySlice";

	/** Input array */
	private final InPort inArray = new InPort("inArray");

	/** Output array */
	private final OutPort outArray = new OutPort("outArray");

	/** Size of the array */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramSize = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Size", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("1"));

	/** Start index */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramStart = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Start", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("0"));

	public TwistArraySlice() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inArray);
		add(outArray);

		// Add all parameters
		add(paramSize);
		add(paramStart);
	}

	/**
	 * Creates extracts a part of an array.
	 *
	 * @param size  Size of the array
	 * @param start Start index
	 */
	public TwistArraySlice(org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramSize,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramStart) {
		this();

		// Set the parameters
		setSize(paramSize);
		setStart(paramStart);
	}

	/**
	 * Creates extracts a part of an array.
	 *
	 * @param size  Size of the array
	 * @param start Start index
	 */
	public TwistArraySlice(Integer paramSize, Integer paramStart) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramSize),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramStart));
	}

	/**
	 * Input array
	 *
	 * @return the input port of the block
	 */
	public final InPort getInArray() {
		return this.inArray;
	}

	/**
	 * Output array
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

	/**
	 * Start index
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getStart() {
		return this.paramStart;
	}

	/**
	 * Sets a parameter of the block: Start index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStart(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramStart.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStart(Integer value) {
		this.setStart(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

}
