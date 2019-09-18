package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Gets a matrix block and returns it as an array.
 */
public class MatrixArrayGet extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixArrayGet";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Output array */
	private final OutPort outArray = new OutPort("outArray");

	/** Start row of block */
	private final Parameter<RPIint> paramStartRow = new Parameter<RPIint>("StartRow", new RPIint("0"));

	/** Start column of block */
	private final Parameter<RPIint> paramStartCol = new Parameter<RPIint>("StartCol", new RPIint("0"));

	/** Block width */
	private final Parameter<RPIint> paramBlockWidth = new Parameter<RPIint>("BlockWidth", new RPIint("1"));

	/** Data size */
	private final Parameter<RPIint> paramSize = new Parameter<RPIint>("Size", new RPIint("1"));

	public MatrixArrayGet() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(outArray);

		// Add all parameters
		add(paramStartRow);
		add(paramStartCol);
		add(paramBlockWidth);
		add(paramSize);
	}

	/**
	 * Creates gets a matrix block and returns it as an array.
	 *
	 * @param startRow   Start row of block
	 * @param startCol   Start column of block
	 * @param blockWidth Block width
	 * @param size       Data size
	 */
	public MatrixArrayGet(RPIint paramStartRow, RPIint paramStartCol, RPIint paramBlockWidth, RPIint paramSize) {
		this();

		// Set the parameters
		setStartRow(paramStartRow);
		setStartCol(paramStartCol);
		setBlockWidth(paramBlockWidth);
		setSize(paramSize);
	}

	/**
	 * Creates gets a matrix block and returns it as an array.
	 *
	 * @param startRow   Start row of block
	 * @param startCol   Start column of block
	 * @param blockWidth Block width
	 * @param size       Data size
	 */
	public MatrixArrayGet(Integer paramStartRow, Integer paramStartCol, Integer paramBlockWidth, Integer paramSize) {
		this(new RPIint(paramStartRow), new RPIint(paramStartCol), new RPIint(paramBlockWidth), new RPIint(paramSize));
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
	 * Output array
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutArray() {
		return this.outArray;
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
	 * Data size
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getSize() {
		return this.paramSize;
	}

	/**
	 * Sets a parameter of the block: Data size
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(RPIint value) {
		this.paramSize.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Data size
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSize(Integer value) {
		this.setSize(new RPIint(value));
	}

}