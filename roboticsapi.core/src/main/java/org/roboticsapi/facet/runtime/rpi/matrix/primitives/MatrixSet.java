package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Writes a matrix value.
 */
public class MatrixSet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixSet";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Value to set */
	private final InPort inValue = new InPort("inValue");

	/** Updated matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Rows count */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("0"));

	/** Column count */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("0"));

	/** Row index */
	private final Parameter<RPIint> paramRow = new Parameter<RPIint>("Row", new RPIint("0"));

	/** Column index */
	private final Parameter<RPIint> paramCol = new Parameter<RPIint>("Col", new RPIint("0"));

	public MatrixSet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(inValue);
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
		add(paramRow);
		add(paramCol);
	}

	/**
	 * Creates writes a matrix value.
	 *
	 * @param rows Rows count
	 * @param cols Column count
	 * @param row  Row index
	 * @param col  Column index
	 */
	public MatrixSet(RPIint paramRows, RPIint paramCols, RPIint paramRow, RPIint paramCol) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
		setRow(paramRow);
		setCol(paramCol);
	}

	/**
	 * Creates writes a matrix value.
	 *
	 * @param rows Rows count
	 * @param cols Column count
	 * @param row  Row index
	 * @param col  Column index
	 */
	public MatrixSet(Integer paramRows, Integer paramCols, Integer paramRow, Integer paramCol) {
		this(new RPIint(paramRows), new RPIint(paramCols), new RPIint(paramRow), new RPIint(paramCol));
	}

	/**
	 * Input matrix
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMatrix() {
		return this.inMatrix;
	}

	/**
	 * Value to set
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Updated matrix
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMatrix() {
		return this.outMatrix;
	}

	/**
	 * Rows count
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getRows() {
		return this.paramRows;
	}

	/**
	 * Sets a parameter of the block: Rows count
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRows(RPIint value) {
		this.paramRows.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Rows count
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

	/**
	 * Row index
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getRow() {
		return this.paramRow;
	}

	/**
	 * Sets a parameter of the block: Row index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRow(RPIint value) {
		this.paramRow.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Row index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRow(Integer value) {
		this.setRow(new RPIint(value));
	}

	/**
	 * Column index
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getCol() {
		return this.paramCol;
	}

	/**
	 * Sets a parameter of the block: Column index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCol(RPIint value) {
		this.paramCol.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Column index
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCol(Integer value) {
		this.setCol(new RPIint(value));
	}

}
