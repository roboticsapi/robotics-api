package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Sets an array value to null.
 */
public class DoubleArraySetNull extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleArraySetNull";

	/** whether to set array to null */
	private final InPort inNull = new InPort("inNull");

	/** Input array */
	private final InPort inValue = new InPort("inValue");

	/** Output array */
	private final OutPort outValue = new OutPort("outValue");

	/** Size of the array */
	private final Parameter<RPIint> paramSize = new Parameter<RPIint>("Size", new RPIint("1"));

	public DoubleArraySetNull() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inNull);
		add(inValue);
		add(outValue);

		// Add all parameters
		add(paramSize);
	}

	/**
	 * Creates sets an array value to null.
	 *
	 * @param size Size of the array
	 */
	public DoubleArraySetNull(RPIint paramSize) {
		this();

		// Set the parameters
		setSize(paramSize);
	}

	/**
	 * Creates sets an array value to null.
	 *
	 * @param size Size of the array
	 */
	public DoubleArraySetNull(Integer paramSize) {
		this(new RPIint(paramSize));
	}

	/**
	 * whether to set array to null
	 *
	 * @return the input port of the block
	 */
	public final InPort getInNull() {
		return this.inNull;
	}

	/**
	 * Input array
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Output array
	 *
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Size of the array
	 *
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getSize() {
		return this.paramSize;
	}

	/**
	 * Sets a parameter of the block: Size of the array
	 *
	 * @param value new value of the parameter
	 */
	public final void setSize(RPIint value) {
		this.paramSize.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Size of the array
	 *
	 * @param value new value of the parameter
	 */
	public final void setSize(Integer value) {
		this.setSize(new RPIint(value));
	}

}
