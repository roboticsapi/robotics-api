package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Solves a linear equation system with the given matrix and result
 */
public class MatrixSolve extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixSolve";

	/** Matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Vector */
	private final InPort inResult = new InPort("inResult");

	/** Result (Matrix * Result = Vector) */
	private final OutPort outValue = new OutPort("outValue");

	/** Size of the resulting vector */
	private final Parameter<RPIint> paramSize = new Parameter<RPIint>("Size", new RPIint("0"));

	public MatrixSolve() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(inResult);
		add(outValue);

		// Add all parameters
		add(paramSize);
	}

	/**
	 * Creates solves a linear equation system with the given matrix and result
	 *
	 * @param size Size of the resulting vector
	 */
	public MatrixSolve(RPIint paramSize) {
		this();

		// Set the parameters
		setSize(paramSize);
	}

	/**
	 * Creates solves a linear equation system with the given matrix and result
	 *
	 * @param size Size of the resulting vector
	 */
	public MatrixSolve(Integer paramSize) {
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
	public final InPort getInResult() {
		return this.inResult;
	}

	/**
	 * Result (Matrix * Result = Vector)
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
