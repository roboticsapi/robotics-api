package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Reads a matrix value.
 */
public class MatrixGet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixGet";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Extracted value */
	private final OutPort outValue = new OutPort("outValue");

	/** Row index */
	private final Parameter<RPIint> paramRow = new Parameter<RPIint>("Row", new RPIint("0"));

	/** Column index */
	private final Parameter<RPIint> paramCol = new Parameter<RPIint>("Col", new RPIint("0"));

	public MatrixGet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(outValue);

		// Add all parameters
		add(paramRow);
		add(paramCol);
	}

	/**
	 * Creates reads a matrix value.
	 *
	 * @param row Row index
	 * @param col Column index
	 */
	public MatrixGet(RPIint paramRow, RPIint paramCol) {
		this();

		// Set the parameters
		setRow(paramRow);
		setCol(paramCol);
	}

	/**
	 * Creates reads a matrix value.
	 *
	 * @param row Row index
	 * @param col Column index
	 */
	public MatrixGet(Integer paramRow, Integer paramCol) {
		this(new RPIint(paramRow), new RPIint(paramCol));
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
	 * Extracted value
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
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
