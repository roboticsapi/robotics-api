/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.mutable;

public class MutableMatrix {

	private final double[][] values;

	public MutableMatrix(int rowDim, int colDim) {
		values = new double[rowDim][colDim];
		for (int i = 0; i < values.length; i++) {
			values[i] = new double[colDim];
		}
	}

	public int getRowDimension() {
		return values.length;
	}

	public int getColumnDimension() {
		return values[0].length;
	}

	public double get(int rowIndex, int columnIndex) {
		return values[rowIndex][columnIndex];
	}

	public void set(int rowIndex, int columnIndex, double value) {
		values[rowIndex][columnIndex] = value;
	}

	public void copyTo(MutableMatrix ret) {
		if (getColumnDimension() != ret.getColumnDimension() || getRowDimension() != ret.getRowDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}
		for (int row = 0; row < getRowDimension(); row++) {
			for (int col = 0; col < getColumnDimension(); col++) {
				ret.set(row, col, get(row, col));
			}
		}
	}

	public void multiplyTo(MutableMatrix other, MutableMatrix ret) {
		if (this.getColumnDimension() != other.getRowDimension() || ret.getRowDimension() != getRowDimension()
				|| ret.getColumnDimension() != other.getColumnDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}

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
	}

	public void transposeTo(MutableMatrix ret) {
		if (getRowDimension() != ret.getColumnDimension() || getColumnDimension() != ret.getRowDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}

		for (int row = 0; row < getRowDimension(); row++) {
			for (int col = 0; col < getColumnDimension(); col++) {
				ret.set(col, row, get(row, col));
			}
		}
	}

	public void applyTo(double[] vector, double[] ret) {
		if (ret.length != getRowDimension() || vector.length != getColumnDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}
		for (int i = 0; i < ret.length; i++) {
			double elem = 0;
			for (int j = 0; j < vector.length; j++) {
				elem += get(i, j) * vector[j];
			}
			ret[i] = elem;
		}
	}

	public boolean solveTo(double[] value, double[] ret) {
		if (ret.length != getColumnDimension() || value.length != getRowDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}

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
				return false;
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

		return true;
	}

	public void copyTo(int startRow, int startCol, MutableMatrix ret) {
		if (startCol + getColumnDimension() > ret.getColumnDimension()
				|| startRow + getRowDimension() > ret.getRowDimension()) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}
		for (int row = 0; row < getRowDimension(); row++) {
			for (int col = 0; col < getColumnDimension(); col++) {
				ret.set(startRow + row, startCol + col, get(row, col));
			}
		}
	}

	public void selectTo(int[] rows, int[] cols, MutableMatrix ret) {
		for (int row = 0; row < ret.getRowDimension(); row++) {
			for (int col = 0; col < ret.getColumnDimension(); col++) {
				ret.set(row, col, get(rows[row], cols[col]));
			}
		}
	}

}
