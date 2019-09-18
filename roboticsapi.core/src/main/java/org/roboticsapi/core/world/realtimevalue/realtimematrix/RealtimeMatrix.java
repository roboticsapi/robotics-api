/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.matrix.NotImplementedException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public abstract class RealtimeMatrix extends RealtimeValue<Matrix> {

	protected final int rows;
	protected final int cols;

	public RealtimeMatrix(RoboticsRuntime runtime, int rows, int cols) {
		super(runtime);
		this.rows = rows;
		this.cols = cols;
	}

	public RealtimeMatrix(int rows, int cols, RealtimeValue<?>... values) {
		super(values);
		this.rows = rows;
		this.cols = cols;
	}

	public int getRowDimension() {
		return rows;
	}

	public int getColumnDimension() {
		return cols;
	}

	public int getSize() {
		return rows * cols;
	}

	public RealtimeDouble get(int rowIndex, int colIndex) {
		Matrix.checkRowIndex(rowIndex, getRowDimension());
		Matrix.checkColumnIndex(colIndex, getColumnDimension());

		return new GetFromMatrixRealtimeDouble(this, rowIndex, colIndex);
	}

	public RealtimeMatrix multiply(double scalar) {
		return multiply(RealtimeDouble.createFromConstant(scalar));
	}

	public RealtimeMatrix multiply(RealtimeDouble scalar) {
		return new ScaledRealtimeMatrix(this, scalar);
	}

	public RealtimeMatrix multiply(Matrix other) {
		return multiply(createFromConstant(other));
	}

	/**
	 * Returns a {@link RealtimeMatrix} that calculates <code>this * other</code>.
	 *
	 * @param other
	 * @return
	 */
	public RealtimeMatrix multiply(RealtimeMatrix other) {
		return new MultipliedRealtimeMatrix(this, other);
	}

	public RealtimeMatrix transpose() {
		return new TransposedRealtimeMatrix(this);
	}

	public RealtimeMatrix with(int rowIndex, int columIndex, double value) {
		return with(rowIndex, columIndex, RealtimeDouble.createFromConstant(value));
	}

	public RealtimeMatrix with(int rowIndex, int columnIndex, RealtimeDouble value) {
		Matrix.checkRowIndex(rowIndex, getRowDimension());
		Matrix.checkColumnIndex(columnIndex, getColumnDimension());

		return new SetValueRealtimeMatrix(this, value, rowIndex, columnIndex);
	}

	public RealtimeMatrix withRow(final int rowIndex, double... cValues) {
		return withRow(rowIndex, RealtimeDoubleArray.createFromConstants(cValues));
	}

	public RealtimeMatrix withRow(final int rowIndex, RealtimeDoubleArray cValues) {
		Matrix.checkRowIndex(rowIndex, getRowDimension());
		Matrix.checkColumnDimension(rowIndex, cValues.getSize(), this.getColumnDimension());

		return new SetArrayRealtimeMatrix(this, cValues, rowIndex, 0, getColumnDimension());
	}

	public RealtimeMatrix withColumn(final int columnIndex, double... rValues) {
		return withColumn(columnIndex, RealtimeDoubleArray.createFromConstants(rValues));
	}

	public RealtimeMatrix withColumn(final int columnIndex, RealtimeDoubleArray rValues) {
		Matrix.checkColumnIndex(columnIndex, getColumnDimension());
		Matrix.checkRowDimension(columnIndex, rValues.getSize(), this.getRowDimension());

		return new SetArrayRealtimeMatrix(this, rValues, 0, columnIndex, 1);
	}

	public RealtimeMatrix withBlock(int rowIndex, int columnIndex, Matrix block) {
		return withBlock(rowIndex, columnIndex, createFromConstant(block));
	}

	public RealtimeMatrix withBlock(int rowIndex, int columnIndex, RealtimeMatrix block) {
		Matrix.checkRowIndex(rowIndex, getRowDimension());
		Matrix.checkRowDimension(columnIndex, rowIndex + block.getRowDimension(), this.getRowDimension());
		Matrix.checkColumnIndex(columnIndex, getColumnDimension());
		Matrix.checkColumnDimension(rowIndex, columnIndex + block.getColumnDimension(), this.getColumnDimension());

		return new SetBlockRealtimeMatrix(this, block, rowIndex, columnIndex);
	}

	public RealtimeMatrix selectRows(int... rows) {
		return select(rows, null);
	}

	public RealtimeMatrix selectColumns(int... columns) {
		return select(null, columns);
	}

	public RealtimeMatrix select(int[] rows, int[] columns) {
		if (rows != null) {
			for (int i = 0; i < rows.length; i++) {
				Matrix.checkRowIndex(rows[i], getRowDimension());
			}
		}

		if (columns != null) {
			for (int i = 0; i < columns.length; i++) {
				Matrix.checkColumnIndex(columns[i], getColumnDimension());
			}
		}
		return new SelectionRealtimeMatrix(this, rows, columns);
	}

	public static RealtimeMatrix createFromMultipleColumns(RealtimeMatrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int rowDim = matrices[0].getRowDimension();
		int colDim = 0;

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			if (rowDim != matrix.getRowDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + rowDim
						+ " rows, but matrix " + i + " has " + matrix.getRowDimension() + " rows.");
			}
			colDim += matrix.getColumnDimension();
		}

		RealtimeMatrix ret = createEmpty(rowDim, colDim);
		int cid = 0;

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			ret = ret.withBlock(0, cid, matrix);
			cid += matrix.getColumnDimension();
		}
		return ret;
	}

	public static RealtimeMatrix createFromMultipleRows(RealtimeMatrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int colDim = matrices[0].getColumnDimension();
		int rowDim = 0;

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			if (colDim != matrix.getColumnDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + colDim
						+ " columns, but matrix " + i + " has " + matrix.getColumnDimension() + " columns.");
			}
			rowDim += matrix.getRowDimension();
		}

		RealtimeMatrix ret = createEmpty(rowDim, colDim);
		int rid = 0;

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			ret = ret.withBlock(rid, 0, matrix);
			rid += matrix.getRowDimension();
		}
		return ret;
	}

	public static RealtimeMatrix createIdentity(int size) {
		return createFromConstant(Matrix.createIdentityMatrix(size));
	}

	public static RealtimeMatrix createEmpty(int rowDim, int colDim) {
		return new EmptyRealtimeMatrix(rowDim, colDim);
	}

	public static RealtimeMatrix createFromConstant(Matrix matrix) {
		return new ConstantRealtimeMatrix(matrix);
	}

	public static RealtimeMatrix createFromConstant(Vector vector) {
		return createFromConstant(new Matrix(vector));
	}

	public static RealtimeMatrix createFromConstant(Transformation transformation) {
		return createFromConstant(new Matrix(transformation));
	}

	public static RealtimeMatrix createFromArray(int rowDim, int colDim, RealtimeDoubleArray doubles) {
		Matrix.checkSize(rowDim, colDim, doubles.getSize());

		RealtimeMatrix empty = createEmpty(rowDim, colDim);
		return new SetArrayRealtimeMatrix(empty, doubles, 0, 0, colDim);
	}

	public static RealtimeMatrix createFromColumnValues(RealtimeDoubleArray... columnValues) {
		int colDim = columnValues.length;
		int rowDim = 0;

		if (colDim > 0) {
			rowDim = columnValues[0].getSize();
		}
		Matrix.checkDimension(rowDim, colDim);

		RealtimeMatrix ret = createEmpty(rowDim, colDim);

		for (int i = 0; i < columnValues.length; i++) {
			Matrix.checkRowDimension(i, columnValues[i].getSize(), rowDim);
			ret = ret.withColumn(i, columnValues[i]);
		}
		return ret;
	}

	public static RealtimeMatrix createFromRowValues(RealtimeDoubleArray... rowValues) {
		int rowDim = rowValues.length;
		int colDim = 0;

		if (rowDim > 0) {
			colDim = rowValues[0].getSize();
		}
		Matrix.checkDimension(rowDim, colDim);

		RealtimeMatrix ret = createEmpty(rowDim, colDim);

		for (int i = 0; i < rowValues.length; i++) {
			Matrix.checkColumnDimension(i, rowValues[i].getSize(), colDim);
			ret = ret.withRow(i, rowValues[i]);
		}
		return ret;
	}

	public static RealtimeMatrix createFromComponents(RealtimeDouble[][] doubles) {
		RealtimeDoubleArray[] rowSensors = new RealtimeDoubleArray[doubles.length];
		for (int i = 0; i < rowSensors.length; i++) {
			rowSensors[i] = RealtimeDoubleArray.createFromComponents(doubles[i]);
		}
		return createFromRowValues(rowSensors);
	}

	/**
	 * Returns the result of applying the matrix to the given vector
	 * <code>this * x</code>
	 *
	 * @param x vector to apply the matrix to
	 * @return
	 */
	public RealtimeDoubleArray apply(RealtimeDoubleArray x) {
		return new AppliedMatrixRealtimeDoubleArray(this, x);
	}

	/**
	 * Returns the solution x of the linear equation system
	 * <code>this * x = rhs</code>
	 *
	 * @param rhs result after applying the matrix
	 * @return x that results in rhs when applying the matrix
	 */
	public RealtimeDoubleArray solve(RealtimeDoubleArray rhs) {
		return new SolvedMatrixRealtimeDoubleArray(this, rhs);
	}

	public static RealtimeMatrix createFromRealtimeVector(RealtimeVector vector) {
		// TODO implement
		throw new NotImplementedException();
	}

	public static RealtimeMatrix createFromRealtimeTransformation(RealtimeTransformation transformation) {
		// TODO implement
		throw new NotImplementedException();
	}

	protected static int checkColumnDimension(RealtimeMatrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int colDim = matrices[0].getColumnDimension();

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			if (colDim != matrix.getColumnDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + colDim
						+ " columns, but matrix " + i + " has " + matrix.getColumnDimension() + " columns.");
			}
		}
		return colDim;
	}

	protected static int checkRowDimension(RealtimeMatrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int rowDim = matrices[0].getRowDimension();

		for (int i = 0; i < matrices.length; i++) {
			RealtimeMatrix matrix = matrices[i];

			if (rowDim != matrix.getRowDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + rowDim
						+ " rows, but matrix " + i + " has " + matrix.getRowDimension() + " rows.");
			}
		}
		return rowDim;
	}

	@Override
	public final RealtimeMatrix substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeMatrix) substitutionMap.get(this);
		}
		return performSubstitution(substitutionMap);
	}

	protected RealtimeMatrix performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (getDependencies().isEmpty()) {
			return this;
		}
		throw new IllegalArgumentException(getClass() + " does not support substitution.");
	}

	@Override
	public RealtimeMatrix fromHistory(double age) {
		return (RealtimeMatrix) super.fromHistory(age);
	}

	@Override
	public RealtimeMatrix fromHistory(RealtimeDouble age, double maxAge) {
		// TODO implement
		throw new NotImplementedException();
	}

	@Override
	public RealtimeBoolean isNull() {
		if (CONSTANT_FOLDING && this.isConstant()) {
			return RealtimeBoolean.createFromConstant(this.getCheapValue() == null);
		}
		// TODO implement
		throw new NotImplementedException();
	}

}
