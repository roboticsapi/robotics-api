package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Transforms a vector by a matrix
 */
public class MatrixApply extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixApply";

	/** Matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Vector */
	private final InPort inVector = new InPort("inVector");

	/** Result (Matrix * Vector) */
	private final OutPort outValue = new OutPort("outValue");

	/** Size of the resulting vector */
	private final Parameter<RPIint> paramSize = new Parameter<RPIint>("Size", new RPIint("0"));

	public MatrixApply() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(inVector);
		add(outValue);

		// Add all parameters
		add(paramSize);
	}

	/**
	 * Creates transforms a vector by a matrix
	 *
	 * @param size Size of the resulting vector
	 */
	public MatrixApply(RPIint paramSize) {
		this();

		// Set the parameters
		setSize(paramSize);
	}

	/**
	 * Creates transforms a vector by a matrix
	 *
	 * @param size Size of the resulting vector
	 */
	public MatrixApply(Integer paramSize) {
		this(new RPIint(paramSize));
	}

	/**
	 * Matrix
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMatrix() {
		return this.inMatrix;
	}

	/**
	 * Vector
	 *
	 * @return the input port of the block
	 */
	public final InPort getInVector() {
		return this.inVector;
	}

	/**
	 * Result (Matrix * Vector)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Size of the resulting vector
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getSize() {
		return this.paramSize;
	}

	/**
	 * Sets a parameter of the block: Size of the resulting vector
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(RPIint value) {
		this.paramSize.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Size of the resulting vector
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(Integer value) {
		this.setSize(new RPIint(value));
	}

}
