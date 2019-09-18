package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Scales a matrix.
 */
public class MatrixScale extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixScale";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Scalar */
	private final InPort inValue = new InPort("inValue");

	/** Scaled matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Rows count */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("0"));

	/** Column count */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("0"));

	/** Data */
	private final Parameter<RPIdouble> paramValue = new Parameter<RPIdouble>("Value", new RPIdouble("0"));

	public MatrixScale() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(inValue);
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
		add(paramValue);
	}

	/**
	 * Creates scales a matrix.
	 *
	 * @param rows Rows count
	 * @param cols Column count
	 */
	public MatrixScale(RPIint paramRows, RPIint paramCols) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
	}

	/**
	 * Creates scales a matrix.
	 *
	 * @param rows Rows count
	 * @param cols Column count
	 */
	public MatrixScale(Integer paramRows, Integer paramCols) {
		this(new RPIint(paramRows), new RPIint(paramCols));
	}

	/**
	 * Creates scales a matrix.
	 *
	 * @param paramRows  Rows count
	 * @param paramCols  Column count
	 * @param paramValue Data
	 */
	public MatrixScale(RPIint paramRows, RPIint paramCols, RPIdouble paramValue) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
		setValue(paramValue);
	}

	/**
	 * Creates scales a matrix.
	 *
	 * @param paramRows  Rows count
	 * @param paramCols  Column count
	 * @param paramValue Data
	 */
	public MatrixScale(Integer paramRows, Integer paramCols, Double paramValue) {
		this(new RPIint(paramRows), new RPIint(paramCols), new RPIdouble(paramValue));
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
	 * Scalar
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Scaled matrix
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
	 * Data
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdouble> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Data
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(RPIdouble value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Data
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Double value) {
		this.setValue(new RPIdouble(value));
	}

}