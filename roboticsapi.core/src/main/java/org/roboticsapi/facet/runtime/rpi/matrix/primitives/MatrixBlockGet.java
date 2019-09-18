package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Gets a matrix block and returns it as a matrix.
 */
public class MatrixBlockGet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixBlockGet";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Output matrix */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Start row of block */
	private final Parameter<RPIint> paramStartRow = new Parameter<RPIint>("StartRow", new RPIint("0"));

	/** Start column of block */
	private final Parameter<RPIint> paramStartCol = new Parameter<RPIint>("StartCol", new RPIint("0"));

	/** Block width (i.e. number of columns) */
	private final Parameter<RPIint> paramBlockWidth = new Parameter<RPIint>("BlockWidth", new RPIint("1"));

	/** Block height (i.e. number of rows) */
	private final Parameter<RPIint> paramBlockHeight = new Parameter<RPIint>("BlockHeight", new RPIint("1"));

	public MatrixBlockGet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(outMatrix);

		// Add all parameters
		add(paramStartRow);
		add(paramStartCol);
		add(paramBlockWidth);
		add(paramBlockHeight);
	}

	/**
	 * Creates gets a matrix block and returns it as a matrix.
	 *
	 * @param startRow    Start row of block
	 * @param startCol    Start column of block
	 * @param blockWidth  Block width (i.e. number of columns)
	 * @param blockHeight Block height (i.e. number of rows)
	 */
	public MatrixBlockGet(RPIint paramStartRow, RPIint paramStartCol, RPIint paramBlockWidth, RPIint paramBlockHeight) {
		this();

		// Set the parameters
		setStartRow(paramStartRow);
		setStartCol(paramStartCol);
		setBlockWidth(paramBlockWidth);
		setBlockHeight(paramBlockHeight);
	}

	/**
	 * Creates gets a matrix block and returns it as a matrix.
	 *
	 * @param startRow    Start row of block
	 * @param startCol    Start column of block
	 * @param blockWidth  Block width (i.e. number of columns)
	 * @param blockHeight Block height (i.e. number of rows)
	 */
	public MatrixBlockGet(Integer paramStartRow, Integer paramStartCol, Integer paramBlockWidth,
			Integer paramBlockHeight) {
		this(new RPIint(paramStartRow), new RPIint(paramStartCol), new RPIint(paramBlockWidth),
				new RPIint(paramBlockHeight));
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