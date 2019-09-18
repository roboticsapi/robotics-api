package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Set a matrix block using an array.
 */
public class MatrixArraySet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixArraySet";

	/** Input array */
	private final InPort inArray = new InPort("inArray");

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Modified matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Number of rows */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("0"));

	/** Number of columns */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("0"));

	/** Start row of block */
	private final Parameter<RPIint> paramStartRow = new Parameter<RPIint>("StartRow", new RPIint("0"));

	/** Start col of block */
	private final Parameter<RPIint> paramStartCol = new Parameter<RPIint>("StartCol", new RPIint("0"));

	/** Block width */
	private final Parameter<RPIint> paramBlockWidth = new Parameter<RPIint>("BlockWidth", new RPIint("1"));

	/** Data */
	private final Parameter<RPIdoubleArray> paramArray = new Parameter<RPIdoubleArray>("Array",
			new RPIdoubleArray("[]"));

	public MatrixArraySet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inArray);
		add(inMatrix);
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
		add(paramStartRow);
		add(paramStartCol);
		add(paramBlockWidth);
		add(paramArray);
	}

	/**
	 * Creates set a matrix block using an array.
	 *
	 * @param rows       Number of rows
	 * @param cols       Number of columns
	 * @param startRow   Start row of block
	 * @param startCol   Start col of block
	 * @param blockWidth Block width
	 */
	public MatrixArraySet(RPIint paramRows, RPIint paramCols, RPIint paramStartRow, RPIint paramStartCol,
			RPIint paramBlockWidth) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
		setStartRow(paramStartRow);
		setStartCol(paramStartCol);
		setBlockWidth(paramBlockWidth);
	}

	/**
	 * Creates set a matrix block using an array.
	 *
	 * @param rows       Number of rows
	 * @param cols       Number of columns
	 * @param startRow   Start row of block
	 * @param startCol   Start col of block
	 * @param blockWidth Block width
	 */
	public MatrixArraySet(Integer paramRows, Integer paramCols, Integer paramStartRow, Integer paramStartCol,
			Integer paramBlockWidth) {
		this(new RPIint(paramRows), new RPIint(paramCols), new RPIint(paramStartRow), new RPIint(paramStartCol),
				new RPIint(paramBlockWidth));
	}

	/**
	 * Creates set a matrix block using an array.
	 *
	 * @param paramRows       Number of rows
	 * @param paramCols       Number of columns
	 * @param paramStartRow   Start row of block
	 * @param paramStartCol   Start col of block
	 * @param paramBlockWidth Block width
	 * @param paramArray      Data
	 */
	public MatrixArraySet(RPIint paramRows, RPIint paramCols, RPIint paramStartRow, RPIint paramStartCol,
			RPIint paramBlockWidth, RPIdoubleArray paramArray) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
		setStartRow(paramStartRow);
		setStartCol(paramStartCol);
		setBlockWidth(paramBlockWidth);
		setArray(paramArray);
	}

	/**
	 * Creates set a matrix block using an array.
	 *
	 * @param paramRows       Number of rows
	 * @param paramCols       Number of columns
	 * @param paramStartRow   Start row of block
	 * @param paramStartCol   Start col of block
	 * @param paramBlockWidth Block width
	 * @param paramArray      Data
	 */
	public MatrixArraySet(Integer paramRows, Integer paramCols, Integer paramStartRow, Integer paramStartCol,
			Integer paramBlockWidth, String paramArray) {
		this(new RPIint(paramRows), new RPIint(paramCols), new RPIint(paramStartRow), new RPIint(paramStartCol),
				new RPIint(paramBlockWidth), new RPIdoubleArray(paramArray));
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
	 * Input matrix
	 *
	 * @return the input port of the block
	 */
	public final InPort getInMatrix() {
		return this.inMatrix;
	}

	/**
	 * Modified matrix
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
	 * Number of columns
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getCols() {
		return this.paramCols;
	}

	/**
	 * Sets a parameter of the block: Number of columns
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(RPIint value) {
		this.paramCols.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Number of columns
	 * 
	 * @param value new value of the parameter
	 */
	public final void setCols(Integer value) {
		this.setCols(new RPIint(value));
	}

	/**
	 * Start row of block
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getStartRow() {
		return this.paramStartRow;
	}

	/**
	 * Sets a parameter of the block: Start row of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartRow(RPIint value) {
		this.paramStartRow.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start row of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartRow(Integer value) {
		this.setStartRow(new RPIint(value));
	}

	/**
	 * Start col of block
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getStartCol() {
		return this.paramStartCol;
	}

	/**
	 * Sets a parameter of the block: Start col of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartCol(RPIint value) {
		this.paramStartCol.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start col of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartCol(Integer value) {
		this.setStartCol(new RPIint(value));
	}

	/**
	 * Block width
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getBlockWidth() {
		return this.paramBlockWidth;
	}

	/**
	 * Sets a parameter of the block: Block width
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockWidth(RPIint value) {
		this.paramBlockWidth.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Block width
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockWidth(Integer value) {
		this.setBlockWidth(new RPIint(value));
	}

	/**
	 * Data
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIdoubleArray> getArray() {
		return this.paramArray;
	}

	/**
	 * Sets a parameter of the block: Data
	 * 
	 * @param value new value of the parameter
	 */
	public final void setArray(RPIdoubleArray value) {
		this.paramArray.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Data
	 * 
	 * @param value new value of the parameter
	 */
	public final void setArray(String value) {
		this.setArray(new RPIdoubleArray(value));
	}

}