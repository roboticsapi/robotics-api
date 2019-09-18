package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Multiplies two matrices.
 */
public class MatrixMultiply extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixMultiply";

	/** Input matrix 1 (left-hand side) */
	private final InPort inMatrix1 = new InPort("inMatrix1");

	/** Input matrix 2 (right-hand side) */
	private final InPort inMatrix2 = new InPort("inMatrix2");

	/** Resulting matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Row count */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("0"));

	/** Column count */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("0"));

	public MatrixMultiply() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix1);
		add(inMatrix2);
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
	}

	/**
	 * Creates multiplies two matrices.
	 *
	 * @param rows Row count
	 * @param cols Column count
	 */
	public MatrixMultiply(RPIint paramRows, RPIint paramCols) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
	}

	/**
	 * Creates multiplies two matrices.
	 *
	 * @param rows Row count
	 * @param cols Column count
	 */
	public MatrixMultiply(Integer paramRows, Integer paramCols) {
		this(new RPIint(paramRows), new RPIint(paramCols));
	}

	/**
	 * Input matrix 1 (left-hand side)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMatrix1() {
		return this.inMatrix1;
	}

	/**
	 * Input matrix 2 (right-hand side)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMatrix2() {
		return this.inMatrix2;
	}

	/**
	 * Resulting matrix
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMatrix() {
		return this.outMatrix;
	}

	/**
	 * Row count
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getRows() {
		return this.paramRows;
	}

	/**
	 * Sets a parameter of the block: Row count
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRows(RPIint value) {
		this.paramRows.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Row count
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRows(Integer value) {
		this.setRows(new RPIint(value));
	}

	/**
	 * Column count
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getCols() {
		return this.paramCols;
	}

	/**
	 * Sets a parameter of the block: Column count
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(RPIint value) {
		this.paramCols.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Column count
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(Integer value) {
		this.setCols(new RPIint(value));
	}

}