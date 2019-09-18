package org.roboticsapi.facet.runtime.rpi.matrix.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIintArray;

/**
 * Selects rows and columns of a matrix.
 */
public class MatrixSelect extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::MatrixSelect";

	/** Input matrix */
	private final InPort inMatrix = new InPort("inMatrix");

	/** Output matrix with selected rows and columns */
	private final OutPort outMatrix = new OutPort("outMatrix");

	/** Row dimension of output matrix */
	private final Parameter<RPIint> paramRowDimension = new Parameter<RPIint>("RowDimension", new RPIint("0"));

	/** Column dimension of output matrix */
	private final Parameter<RPIint> paramColumnDimension = new Parameter<RPIint>("ColumnDimension", new RPIint("0"));

	/** Array with indices of selected rows */
	private final Parameter<RPIintArray> paramSelectedRows = new Parameter<RPIintArray>("SelectedRows",
			new RPIintArray("[]"));

	/** Array with indices of selected columns */
	private final Parameter<RPIintArray> paramSelectedCols = new Parameter<RPIintArray>("SelectedCols",
			new RPIintArray("[]"));

	public MatrixSelect() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inMatrix);
		add(outMatrix);

		// Add all parameters
		add(paramRowDimension);
		add(paramColumnDimension);
		add(paramSelectedRows);
		add(paramSelectedCols);
	}

	/**
	 * Creates selects rows and columns of a matrix.
	 *
	 * @param rowDimension    Row dimension of output matrix
	 * @param columnDimension Column dimension of output matrix
	 * @param selectedRows    Array with indices of selected rows
	 * @param selectedCols    Array with indices of selected columns
	 */
	public MatrixSelect(RPIint paramRowDimension, RPIint paramColumnDimension, RPIintArray paramSelectedRows,
			RPIintArray paramSelectedCols) {
		this();

		// Set the parameters
		setRowDimension(paramRowDimension);
		setColumnDimension(paramColumnDimension);
		setSelectedRows(paramSelectedRows);
		setSelectedCols(paramSelectedCols);
	}

	/**
	 * Creates selects rows and columns of a matrix.
	 *
	 * @param rowDimension    Row dimension of output matrix
	 * @param columnDimension Column dimension of output matrix
	 * @param selectedRows    Array with indices of selected rows
	 * @param selectedCols    Array with indices of selected columns
	 */
	public MatrixSelect(Integer paramRowDimension, Integer paramColumnDimension, String paramSelectedRows,
			String paramSelectedCols) {
		this(new RPIint(paramRowDimension), new RPIint(paramColumnDimension), new RPIintArray(paramSelectedRows),
				new RPIintArray(paramSelectedCols));
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
	 * Output matrix with selected rows and columns
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutMatrix() {
		return this.outMatrix;
	}

	/**
	 * Row dimension of output matrix
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getRowDimension() {
		return this.paramRowDimension;
	}

	/**
	 * Sets a parameter of the block: Row dimension of output matrix
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRowDimension(RPIint value) {
		this.paramRowDimension.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Row dimension of output matrix
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRowDimension(Integer value) {
		this.setRowDimension(new RPIint(value));
	}

	/**
	 * Column dimension of output matrix
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIint> getColumnDimension() {
		return this.paramColumnDimension;
	}

	/**
	 * Sets a parameter of the block: Column dimension of output matrix
	 * 
	 * @param value new value of the parameter
	 */
	public final void setColumnDimension(RPIint value) {
		this.paramColumnDimension.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Column dimension of output matrix
	 * 
	 * @param value new value of the parameter
	 */
	public final void setColumnDimension(Integer value) {
		this.setColumnDimension(new RPIint(value));
	}

	/**
	 * Array with indices of selected rows
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIintArray> getSelectedRows() {
		return this.paramSelectedRows;
	}

	/**
	 * Sets a parameter of the block: Array with indices of selected rows
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSelectedRows(RPIintArray value) {
		this.paramSelectedRows.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Array with indices of selected rows
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSelectedRows(String value) {
		this.setSelectedRows(new RPIintArray(value));
	}

	/**
	 * Array with indices of selected columns
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<RPIintArray> getSelectedCols() {
		return this.paramSelectedCols;
	}

	/**
	 * Sets a parameter of the block: Array with indices of selected columns
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSelectedCols(RPIintArray value) {
		this.paramSelectedCols.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Array with indices of selected columns
	 * 
	 * @param value new value of the parameter
	 */
	public final void setSelectedCols(String value) {
		this.setSelectedCols(new RPIintArray(value));
	}

}