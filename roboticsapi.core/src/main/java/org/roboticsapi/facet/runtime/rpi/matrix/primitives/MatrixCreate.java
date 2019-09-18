package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Creates an matrix.
 */
public class MatrixCreate extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixCreate";

	/** Value */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Number of rows */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("1"));

	/** Number of cols */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("1"));

	public MatrixCreate() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
	}

	/**
	 * Creates creates an matrix.
	 *
	 * @param rows Number of rows
	 * @param cols Number of cols
	 */
	public MatrixCreate(RPIint paramRows, RPIint paramCols) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
	}

	/**
	 * Creates creates an matrix.
	 *
	 * @param rows Number of rows
	 * @param cols Number of cols
	 */
	public MatrixCreate(Integer paramRows, Integer paramCols) {
		this(new RPIint(paramRows), new RPIint(paramCols));
	}

	/**
	 * Value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMatrix() {
		return this.outMatrix;
	}

	/**
	 * Number of rows
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getRows() {
		return this.paramRows;
	}

	/**
	 * Sets a parameter of the block: Number of rows
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRows(RPIint value) {
		this.paramRows.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Number of rows
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRows(Integer value) {
		this.setRows(new RPIint(value));
	}

	/**
	 * Number of cols
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getCols() {
		return this.paramCols;
	}

	/**
	 * Sets a parameter of the block: Number of cols
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(RPIint value) {
		this.paramCols.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Number of cols
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(Integer value) {
		this.setCols(new RPIint(value));
	}

}