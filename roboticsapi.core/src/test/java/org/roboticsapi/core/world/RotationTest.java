/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.junit.Assert;
import org.junit.Test;

public class RotationTest {

	@Test
	public void testRotationABC() {
		Rotation rot = new Rotation(1, 0.5, 0.25);
		Assert.assertEquals(1, rot.getA(), 0.001);
		Assert.assertEquals(0.5, rot.getB(), 0.001);
		Assert.assertEquals(0.25, rot.getC(), 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		Vector axis = new Vector(1, 1, 0).normalize();
		rot = new Rotation(axis, 1);
		Assert.assertEquals(rot.getAngle(), 1, 0.001);
		Assert.assertTrue(rot.getAxis().isEqualVector(axis));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(new Vector(0, 0, 1), 1);
		Assert.assertEquals(rot.getA(), 1, 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = rot.multiply(new Rotation(-0.5, 0, 0));
		Assert.assertEquals(rot.getA(), 0.5, 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = rot.invert();
		Assert.assertEquals(rot.getA(), -0.5, 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(new Vector(0, 1, 0), new Vector(-1, 0, 0), new Vector(0, 0, 1));
		Assert.assertEquals(rot.getA(), Math.toRadians(90), 0.001);
		Assert.assertEquals(rot.getAngle(), Math.toRadians(90), 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		Vector vec = rot.apply(new Vector(1, 0, 0));
		Assert.assertTrue(vec.isEqualVector(new Vector(0, 1, 0)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(0, Math.toRadians(90), 0);
		vec = rot.apply(new Vector(1, 0, 0));
		Assert.assertTrue(vec.isEqualVector(new Vector(0, 0, -1)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(0, 0, 1);
		vec = rot.apply(new Vector(1, 0, 0));
		Assert.assertTrue(vec.isEqualVector(new Vector(1, 0, 0)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(0, 0, Math.toRadians(90));
		vec = rot.apply(new Vector(0, 1, 0));
		Assert.assertTrue(vec.isEqualVector(new Vector(0, 0, 1)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		vec = rot.apply(new Vector(0, 0, 1));
		Assert.assertTrue(vec.isEqualVector(new Vector(0, -1, 0)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(Math.toRadians(90), 0, 0);
		rot = rot.multiply(rot);
		rot = rot.multiply(rot);
		Assert.assertEquals(rot.getA(), 0, 0.001);
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		rot = new Rotation(new Vector(1, 0, 0), Math.PI / 2);
		Assert.assertTrue(rot.getAxis().isEqualVector(new Vector(1, 0, 0)));
		Assert.assertEquals(rot.getDeterminant(), 1, 0.001);

		Matrix3x3 mat = new Matrix3x3(1, 2, 3, 4, 5, 6, 7, 8, 9);
		Matrix3x3 transp = mat.transpose();
		Assert.assertEquals(mat.getDeterminant(), transp.transpose().getDeterminant(), 0.001);
		Assert.assertEquals(transp.transpose().sub(mat).getDeterminant(), 0.0, 0.001);
		Assert.assertEquals(transp.get(0, 0), 1.0, 0.001);
		Assert.assertEquals(transp.get(1, 0), 2.0, 0.001);
		Assert.assertEquals(transp.get(2, 0), 3.0, 0.001);
		Assert.assertEquals(transp.get(0, 1), 4.0, 0.001);
		Assert.assertEquals(transp.get(1, 1), 5.0, 0.001);
		Assert.assertEquals(transp.get(2, 1), 6.0, 0.001);
		Assert.assertEquals(transp.get(0, 2), 7.0, 0.001);
		Assert.assertEquals(transp.get(1, 2), 8.0, 0.001);
		Assert.assertEquals(transp.get(2, 2), 9.0, 0.001);

	}

}
