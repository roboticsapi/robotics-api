package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Sets a matrix block using another matrix.
 */
public class MatrixBlockSet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixBlockSet";

	/** Block matrix */
	private final InPort inBlock = new InPort("inBlock");

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Output matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Number of rows */
	private final Parameter<RPIint> paramRows = new Parameter<RPIint>("Rows", new RPIint("0"));

	/** Number of columns */
	private final Parameter<RPIint> paramCols = new Parameter<RPIint>("Cols", new RPIint("0"));

	/** Start row of block */
	private final Parameter<RPIint> paramStartRow = new Parameter<RPIint>("StartRow", new RPIint("0"));

	/** Start column of block */
	private final Parameter<RPIint> paramStartCol = new Parameter<RPIint>("StartCol", new RPIint("0"));

	/** Block width (i.e. number of columns) */
	private final Parameter<RPIint> paramBlockWidth = new Parameter<RPIint>("BlockWidth", new RPIint("1"));

	/** Block height (i.e. number of rows) */
	private final Parameter<RPIint> paramBlockHeight = new Parameter<RPIint>("BlockHeight", new RPIint("1"));

	public MatrixBlockSet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inBlock);
		add(inMatrix);
		add(outMatrix);

		// Add all parameters
		add(paramRows);
		add(paramCols);
		add(paramStartRow);
		add(paramStartCol);
		add(paramBlockWidth);
		add(paramBlockHeight);
	}

	/**
	 * Creates sets a matrix block using another matrix.
	 *
	 * @param rows        Number of rows
	 * @param cols        Number of columns
	 * @param startRow    Start row of block
	 * @param startCol    Start column of block
	 * @param blockWidth  Block width (i.e. number of columns)
	 * @param blockHeight Block height (i.e. number of rows)
	 */
	public MatrixBlockSet(RPIint paramRows, RPIint paramCols, RPIint paramStartRow, RPIint paramStartCol,
			RPIint paramBlockWidth, RPIint paramBlockHeight) {
		this();

		// Set the parameters
		setRows(paramRows);
		setCols(paramCols);
		setStartRow(paramStartRow);
		setStartCol(paramStartCol);
		setBlockWidth(paramBlockWidth);
		setBlockHeight(paramBlockHeight);
	}

	/**
	 * Creates sets a matrix block using another matrix.
	 *
	 * @param rows        Number of rows
	 * @param cols        Number of columns
	 * @param startRow    Start row of block
	 * @param startCol    Start column of block
	 * @param blockWidth  Block width (i.e. number of columns)
	 * @param blockHeight Block height (i.e. number of rows)
	 */
	public MatrixBlockSet(Integer paramRows, Integer paramCols, Integer paramStartRow, Integer paramStartCol,
			Integer paramBlockWidth, Integer paramBlockHeight) {
		this(new RPIint(paramRows), new RPIint(paramCols), new RPIint(paramStartRow), new RPIint(paramStartCol),
				new RPIint(paramBlockWidth), new RPIint(paramBlockHeight));
	}

	/**
	 * Block matrix
	 *
	 * @return the input port of the block
	 */
	public final InPort getInBlock() {
		return this.inBlock;
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
	 * Output matrix
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
	 * Start column of block
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getStartCol() {
		return this.paramStartCol;
	}

	/**
	 * Sets a parameter of the block: Start column of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartCol(RPIint value) {
		this.paramStartCol.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Start column of block
	 * 
	 * @param value new value of the parameter
	 */
	public final void setStartCol(Integer value) {
		this.setStartCol(new RPIint(value));
	}

	/**
	 * Block width (i.e. number of columns)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getBlockWidth() {
		return this.paramBlockWidth;
	}

	/**
	 * Sets a parameter of the block: Block width (i.e. number of columns)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockWidth(RPIint value) {
		this.paramBlockWidth.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Block width (i.e. number of columns)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockWidth(Integer value) {
		this.setBlockWidth(new RPIint(value));
	}

	/**
	 * Block height (i.e. number of rows)
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getBlockHeight() {
		return this.paramBlockHeight;
	}

	/**
	 * Sets a parameter of the block: Block height (i.e. number of rows)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockHeight(RPIint value) {
		this.paramBlockHeight.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Block height (i.e. number of rows)
	 * 
	 * @param value new value of the parameter
	 */
	public final void setBlockHeight(Integer value) {
		this.setBlockHeight(new RPIint(value));
	}

}