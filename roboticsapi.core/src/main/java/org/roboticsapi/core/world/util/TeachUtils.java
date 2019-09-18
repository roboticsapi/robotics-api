/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.world.Matrix3x3;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;

/**
 * Utility class to teach tools and frames
 */
public class TeachUtils {

	/**
	 * Teaches a frame using 3 points
	 * 
	 * @param pointTrans Base-Flange-transformation of the origin of the frame to
	 *                   teach
	 * @param dirTrans   Base-Flange-transformation for a point on the X axis of the
	 *                   frame to teach
	 * @param planeTrans Base-Flange-transformation for a point in the positive
	 *                   XY-plane of the frame to teach
	 * @param tool       Flange-Tool-transformation for the tool used to teach the
	 *                   frame
	 * @return Base-Frame-transformation of the taught frame
	 */
	public static Transformation teachFrame(Transformation pointTrans, Transformation dirTrans,
			Transformation planeTrans, Transformation tool) {
		// apply tool transformation
		pointTrans = pointTrans.multiply(tool);
		dirTrans = dirTrans.multiply(tool);
		planeTrans = planeTrans.multiply(tool);

		// origin of the frame
		Vector pointVect = pointTrans.getTranslation();

		// x direction
		Vector xVect = (dirTrans.getTranslation().add(pointTrans.getTranslation().invert())).normalize();

		// xy direction
		Vector yVect = planeTrans.getTranslation().add(pointTrans.getTranslation().invert());

		// orthonormalising y direction
		double factor = yVect.dot(xVect);
		Vector temp = new Vector(xVect.getX(), xVect.getY(), xVect.getZ());
		temp = temp.scale(factor);
		yVect = yVect.add(temp.invert()).normalize();

		// z direction as cross product
		Vector zVect = xVect.cross(yVect).normalize();

		// taught transformation
		Rotation rotation = new Rotation(xVect, yVect, zVect);
		Transformation tm = new Transformation(rotation, pointVect);

		return tm;
	}

	/**
	 * Teaches a robot tool (vector) using N (>= 3) measured transformations using a
	 * best-fit method
	 * 
	 * @param points various Base-Flange-transformations that make the tool point to
	 *               the same position, in different orientations
	 * @return best-fit vector describing the translation of the tool center point
	 *         relative to the flange
	 */
	public static Vector teachTool(Transformation... points) {
		List<double[]> rows = new ArrayList<double[]>();

		// build equations that describe the best-fit problem:
		// first three columns (x) are rotation between two measurements,
		// last column (y) is translation between measurements
		for (int i = 0; i < points.length - 1; i++) {
			for (int j = i + 1; j < points.length; j++) {
				Matrix3x3 rot = points[i].getRotation().sub(points[j].getRotation());
				Vector trans = points[j].getTranslation().sub(points[i].getTranslation());

				rows.add(new double[] { rot.get(0, 0), rot.get(0, 1), rot.get(0, 2), trans.getX() });
				rows.add(new double[] { rot.get(1, 0), rot.get(1, 1), rot.get(1, 2), trans.getY() });
				rows.add(new double[] { rot.get(2, 0), rot.get(2, 1), rot.get(2, 2), trans.getZ() });
			}
		}

		// we try to optimize x * tcp = y using linear least squares method.
		// compute xTx : x transpose times x
		float[] xTx = new float[9];
		for (int i = 0; i < 3; i++) { // xTx row
			for (int j = 0; j < 3; j++) { // xTx column
				xTx[i * 3 + j] = 0;
				for (int k = 0; k < rows.size(); k++) { // x rows
					xTx[i * 3 + j] += rows.get(k)[i] * rows.get(k)[j];
				}
			}
		}

		// compute xTy : x transpose times y
		float[] xTy = new float[3];
		for (int i = 0; i < 3; i++) { // xTy rows
			xTy[i] = 0;
			for (int k = 0; k < rows.size(); k++) { // x rows
				xTy[i] += rows.get(k)[3] * rows.get(k)[i];
			}
		}

		// solve xTx * tcp = xTy
		Matrix3x3 tx = new Matrix3x3(xTx[0], xTx[1], xTx[2], xTx[3], xTx[4], xTx[5], xTx[6], xTx[7], xTx[8]);
		Vector ty = new Vector(xTy[0], xTy[1], xTy[2]);
		Vector tcp = tx.invert().apply(ty);

		// here is our resulting tool center point
		return tcp;

	}
}