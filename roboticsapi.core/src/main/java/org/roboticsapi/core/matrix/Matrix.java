/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.matrix;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;

public class Matrix {

	private final double[][] values;

	public Matrix(int rowDim, int colDim) {
		checkDimension(rowDim, colDim);

		values = new double[rowDim][colDim];
		for (int i = 0; i < values.length; i++) {
			values[i] = new double[colDim];
		}
	}

	public Matrix(double[][] values) {
		int rowDim = values.length;
		int colDim = values[0].length;
		checkDimension(rowDim, colDim);

		this.values = new double[rowDim][];

		for (int i = 0; i < values.length; i++) {
			int length = values[i].length;

			if (length != colDim) {
				throw new IllegalArgumentException(
						"First row had " + colDim + " columns, but row " + i + " has " + length + " columns.");
			}
			this.values[i] = new double[length];
			for (int j = 0; j < values[i].length; j++) {
				this.values[i][j] = values[i][j];
			}
		}
	}

	public Matrix(Vector vector) {
		this(vectorToMatrix(vector));
	}

	public Matrix(Transformation transformation) {
		this(transformationToMatrix(transformation));
	}

	public int getRowDimension() {
		return values.length;
	}

	public int getColumnDimension() {
		return values[0].length;
	}

	public double get(int rowIndex, int columnIndex) {
		checkRowIndex(rowIndex, this);
		checkColumnIndex(columnIndex, this);

		return values[rowIndex][columnIndex];
	}

	protected void set(int rowIndex, int columnIndex, double value) {
		checkRowIndex(rowIndex, this);
		checkColumnIndex(columnIndex, this);

		values[rowIndex][columnIndex] = value;
	}

	public Matrix with(int rowIndex, int columIndex, double value) {
		Matrix ret = new Matrix(this.values);
		ret.set(rowIndex, columIndex, value);

		return ret;
	}

	public Matrix withRow(final int rowIndex, double... cValues) {
		Matrix ret = new Matrix(this.values);
		checkColumnDimension(rowIndex, cValues.length, this);

		for (int c = 0; c < cValues.length; c++) {
			ret.set(rowIndex, c, cValues[c]);
		}
		return ret;
	}

	public Matrix withColumn(final int columnIndex, double... rValues) {
		Matrix ret = new Matrix(this.values);
		checkRowDimension(columnIndex, rValues.length, this);

		for (int r = 0; r < rValues.length; r++) {
			ret.set(r, columnIndex, rValues[r]);
		}
		return ret;
	}

	public Matrix withBlock(int rowIndex, int columnIndex, Matrix block) {
		Matrix ret = new Matrix(this.values);

		checkRowDimension(columnIndex, rowIndex + block.getRowDimension(), this);
		checkColumnDimension(rowIndex, columnIndex + block.getColumnDimension(), this);

		for (int r = 0; r < block.getRowDimension(); r++) {
			for (int c = 0; c < block.getColumnDimension(); c++) {
				ret.set(rowIndex + r, columnIndex + c, block.get(r, c));
			}
		}
		return ret;
	}

	public Matrix selectRows(int... rows) {
		return select(rows, null);
	}

	public Matrix selectColumns(int... columns) {
		return select(null, columns);
	}

	public Matrix select(int[] rows, int[] columns) {
		Matrix ret = new Matrix(rows.length, columns.length);

		for (int r = 0; r < ret.getRowDimension(); r++) {
			for (int c = 0; c < ret.getColumnDimension(); c++) {
				int rowIndex = r;
				int columnIndex = c;

				if (rows != null) {
					rowIndex = rows[r];
					checkRowIndex(rowIndex, this);
				}
				if (columns != null) {
					columnIndex = columns[c];
					checkColumnIndex(columnIndex, this);
				}
				ret.set(r, c, get(rowIndex, columnIndex));
			}
		}
		return ret;
	}

	public Double[] fillArray(int rowIndex, int columnIndex, int blockWidth, Double[] array) {
		checkRowIndex(rowIndex, getRowDimension());
		checkColumnIndex(columnIndex, getColumnDimension());

		// TODO: Add more checks

		for (int i = 0; i < array.length; i++) {
			array[i] = this.get(rowIndex + i / blockWidth, columnIndex + i % blockWidth);
		}
		return array;
	}

	public double[] fillArray(int rowIndex, int columnIndex, int blockWidth, double[] array) {
		checkRowIndex(rowIndex, getRowDimension());
		checkColumnIndex(columnIndex, getColumnDimension());

		// TODO: Add more checks

		for (int i = 0; i < array.length; i++) {
			array[i] = this.get(rowIndex + i / blockWidth, columnIndex + i % blockWidth);
		}
		return array;
	}

	public double[] asArray() {
		return asArray(0, 0, getColumnDimension(), getRowDimension() * getColumnDimension());
	}

	public double[] asArray(int rowIndex, int columnIndex, int blockWidth, int length) {
		double[] ret = new double[length];
		return fillArray(rowIndex, columnIndex, blockWidth, ret);
	}

	public Matrix multiply(double scalar) {
		int rows = getRowDimension();
		int cols = getColumnDimension();
		Matrix ret = new Matrix(rows, cols);

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ret.set(r, c, scalar * get(r, c));
			}
		}
		return ret;
	}

	public Matrix multiply(Matrix other) {
		if (this.getColumnDimension() != other.getRowDimension()) {
			throw new IllegalArgumentException(
					"Argument has " + other.getRowDimension() + " rows, but this Matrix has " + getColumnDimension());
		}

		Matrix ret = new Matrix(getRowDimension(), other.getColumnDimension());

		for (int i = 0; i < ret.getRowDimension(); i++) {
			for (int k = 0; k < ret.getColumnDimension(); k++) {
				for (int j = 0; j < getColumnDimension(); j++) {
					double c = ret.get(i, k);
					double a = get(i, j);
					double b = other.get(j, k);

					ret.set(i, k, c + (a * b));
				}
			}
		}
		return ret;
	}

	public Matrix transpose() {
		int rows = getRowDimension();
		int cols = getColumnDimension();
		Matrix ret = new Matrix(cols, rows);

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ret.set(c, r, get(r, c));
			}
		}
		return ret;
	}

	public static Matrix createIdentityMatrix(int size) {
		Matrix ret = new Matrix(size, size);

		for (int i = 0; i < size; i++) {
			ret.set(i, i, 1);
		}
		return ret;
	}

	public static Matrix createFromRows(double[]... rows) {
		int rowDim = rows.length;
		int colDim = rows[0].length;
		checkDimension(rowDim, colDim);

		Matrix ret = new Matrix(rowDim, colDim);

		for (int i = 0; i < rows.length; i++) {
			checkColumnDimension(i, rows[i].length, colDim);

			for (int j = 0; j < rows[i].length; j++) {
				ret.set(i, j, rows[i][j]);
			}
		}
		return ret;
	}

	public static Matrix createFromColumns(double[]... columns) {
		int colDim = columns.length;
		int rowDim = columns[0].length;
		checkDimension(rowDim, colDim);

		Matrix ret = new Matrix(rowDim, colDim);

		for (int i = 0; i < columns.length; i++) {
			checkRowDimension(i, columns[i].length, rowDim);

			for (int j = 0; j < columns[i].length; j++) {
				ret.set(j, i, columns[j][i]);
			}
		}
		return ret;
	}

	public static Matrix createFromArray(int rowDim, int colDim, Double... values) {
		checkDimension(rowDim, colDim);
		checkSize(rowDim, colDim, values.length);

		Matrix ret = new Matrix(rowDim, colDim);

		for (int i = 0; i < values.length; i++) {
			ret.set(i / colDim, i % colDim, values[i]);
		}
		return ret;
	}

	private static double[][] vectorToMatrix(Vector vector) {
		double[][] v = new double[3][1];
		v[0][0] = vector.getX();
		v[1][0] = vector.getY();
		v[2][0] = vector.getZ();
		return v;
	}

	private static double[][] transformationToMatrix(Transformation trans) {
		double[][] t = new double[4][4];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				t[i][j] = trans.getRotation().get(i, j);
			}
		}
		t[0][3] = trans.getTranslation().getX();
		t[1][3] = trans.getTranslation().getY();
		t[2][3] = trans.getTranslation().getZ();
		t[3][0] = 0;
		t[3][1] = 0;
		t[3][2] = 0;
		t[3][3] = 1;

		return t;
	}

	public static void checkRowIndex(int rowIndex, int rowDimension) {
		if (rowIndex >= rowDimension) {
			throw new IllegalArgumentException(
					"Row index " + rowIndex + " requested, but Matrix has " + rowDimension + " rows.");
		}
	}

	private static void checkRowIndex(int rowIndex, Matrix matrix) {
		checkRowIndex(rowIndex, matrix.getRowDimension());
	}

	public static void checkColumnIndex(int columnIndex, int columnDimension) {
		if (columnIndex >= columnDimension) {
			throw new IllegalArgumentException(
					"Column index " + columnIndex + " requested, but Matrix has " + columnDimension + " columns.");
		}
	}

	private static void checkColumnIndex(int columnIndex, Matrix matrix) {
		checkColumnIndex(columnIndex, matrix.getColumnDimension());
	}

	public static void checkDimension(int rowDim, int colDim) {
		if (rowDim <= 0) {
			throw new IllegalArgumentException("Cannot create matrix with less than one row");
		}

		if (colDim <= 0) {
			throw new IllegalArgumentException("Cannot create matrix with less than one column");
		}
	}

	public static void checkSize(int rowDim, int colDim, int expectedSize) {
		int size = rowDim * colDim;

		if (size != expectedSize) {
			throw new IllegalArgumentException("The size of the supplied array does not match the matrix size.");
		}
	}

	public static int checkColumnDimension(Matrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int colDim = matrices[0].getColumnDimension();

		for (int i = 0; i < matrices.length; i++) {
			Matrix matrix = matrices[i];

			if (colDim != matrix.getColumnDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + colDim
						+ " columns, but matrix " + i + " has " + matrix.getColumnDimension() + " columns.");
			}
		}
		return colDim;
	}

	public static void checkColumnDimension(int rowIndex, int columnLength, int expectedColumnDimension) {
		if (expectedColumnDimension != columnLength) {
			throw new IllegalArgumentException("Matrix is expected to have " + expectedColumnDimension
					+ " columns, but " + columnLength + " columns for row  " + rowIndex + " have been supplied.");
		}
	}

	private static void checkColumnDimension(int rowIndex, int columnLength, Matrix matrix) {
		int expColDim = matrix.getColumnDimension();
		checkColumnDimension(rowIndex, columnLength, expColDim);
	}

	public static int checkRowDimension(Matrix... matrices) {
		if (matrices.length == 0) {
			throw new IllegalArgumentException("No matrices have been specified.");
		}
		int rowDim = matrices[0].getRowDimension();

		for (int i = 0; i < matrices.length; i++) {
			Matrix matrix = matrices[i];

			if (rowDim != matrix.getRowDimension()) {
				throw new IllegalArgumentException("Combined matrix is expected to have " + rowDim
						+ " rows, but matrix " + i + " has " + matrix.getRowDimension() + " rows.");
			}
		}
		return rowDim;
	}

	public static void checkRowDimension(int columnIndex, int rowLength, int expectedRowDimension) {
		if (expectedRowDimension != rowLength) {
			throw new IllegalArgumentException("Matrix is expected to have " + expectedRowDimension + " rows, but "
					+ rowLength + " rows for column " + columnIndex + " have been supplied.");
		}
	}

	private static void checkRowDimension(int columnIndex, int rowLength, Matrix matrix) {
		int expRowDim = matrix.getRowDimension();
		checkRowDimension(columnIndex, rowLength, expRowDim);
	}

	public double[] apply(double[] vector) {
		double[] ret = new double[getRowDimension()];
		for (int i = 0; i < ret.length; i++) {
			double elem = 0;
			for (int j = 0; j < vector.length; j++) {
				elem += get(i, j) * vector[j];
			}
			ret[i] = elem;
		}
		return ret;
	}

	public double[] solve(double[] value) {
		double[] ret = new double[getColumnDimension()];
		double[][] matrix = new double[getRowDimension()][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = values[i];
		}

		int n = matrix.length;
		for (int p = 0; p < n; p++) {
			// find pivot element
			int max = p;
			for (int i = p + 1; i < n; i++) {
				if (Math.abs(matrix[i][p]) > Math.abs(matrix[max][p])) {
					max = i;
				}
			}

			// swap rows and result
			double[] temp = matrix[p];
			matrix[p] = matrix[max];
			matrix[max] = temp;
			double t = value[p];
			value[p] = value[max];
			value[max] = t;

			// near singular
			if (Math.abs(matrix[p][p]) <= 1e-5) {
				return null;
			}

			// eliminate
			for (int i = p + 1; i < n; i++) {
				double alpha = matrix[i][p] / matrix[p][p];
				value[i] -= alpha * value[p];
				for (int j = p; j < n; j++) {
					matrix[i][j] -= alpha * matrix[p][j];
				}
			}
		}

		// back substitution
		double[] x = new double[n];
		for (int i = n - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < n; j++) {
				sum += matrix[i][j] * x[j];
			}
			ret[i] = (value[i] - sum) / matrix[i][i];
		}

		return ret;
	}
}
