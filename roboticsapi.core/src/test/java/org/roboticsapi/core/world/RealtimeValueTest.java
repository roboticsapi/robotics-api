/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.junit.Test;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public class RealtimeValueTest {

	@Test
	public void testTransformation() {
		RealtimeTransformation ident = RealtimeTransformation.createfromConstant(Transformation.IDENTITY);
		RealtimeTransformation move = RealtimeTransformation.createfromConstant(new Transformation(1, 0, 0, 0, 0, 0));
		RealtimeTransformation rot = RealtimeTransformation
				.createfromConstant(new Transformation(0, 0, 0, Math.PI / 2, 0, 0));

		RealtimeTransformation vident = RealtimeTransformation.createFromVectorRotation(
				RealtimeVector.createFromConstant(new Vector()),
				RealtimeRotation.createFromAxisAngle(new Vector(), RealtimeDouble.createWritable(0)));
		RealtimeTransformation vmove = RealtimeTransformation.createFromVectorRotation(
				RealtimeVector.createFromConstant(new Vector(1, 0, 0)),
				RealtimeRotation.createFromAxisAngle(new Vector(), RealtimeDouble.createWritable(0)));
		RealtimeTransformation vrot = RealtimeTransformation.createFromVectorRotation(
				RealtimeVector.createFromConstant(new Vector()),
				RealtimeRotation.createFromAxisAngle(new Vector(0, 0, 1), RealtimeDouble.createWritable(Math.PI / 2)));

		System.out.println(ident.multiply(move));
		System.out.println(vident.multiply(vmove));
		System.out.println(vident.multiply(move));
		System.out.println(ident.multiply(vmove));
		System.out.println();
		System.out.println(ident.multiply(ident));
		System.out.println(vident.multiply(vident));
		System.out.println(vident.multiply(ident));
		System.out.println(ident.multiply(vident));
		System.out.println();
		System.out.println(move.multiply(ident));
		System.out.println(vmove.multiply(vident));
		System.out.println(vmove.multiply(ident));
		System.out.println(move.multiply(vident));
		System.out.println();
		System.out.println(move.multiply(rot));
		System.out.println(vmove.multiply(vrot));
		System.out.println(vmove.multiply(rot));
		System.out.println(move.multiply(vrot));

	}

	@Test
	public void testRotation() {
		RealtimeRotation ident = RealtimeRotation.createFromConstant(Rotation.getIdentity());
		RealtimeRotation ninety = RealtimeRotation.createFromConstant(new Rotation(Math.PI / 2, 0, 0));

		System.out.println(ident.multiply(ninety));
		System.out.println(ninety.multiply(ninety));
		System.out.println(ninety.multiply(ident));
		System.out.println(ninety.invert());
		System.out.println(ninety.invert().multiply(ninety));
		System.out.println();
	}

	@Test
	public void testRealtimeDouble() {
		RealtimeDouble zero = RealtimeDouble.createFromConstant(0);
		RealtimeDouble one = RealtimeDouble.createFromConstant(1);
		RealtimeDouble two = RealtimeDouble.createFromConstant(2);
		RealtimeDouble vzero = RealtimeDouble.createWritable(0);
		RealtimeDouble vone = RealtimeDouble.createWritable(1);
		RealtimeDouble vtwo = RealtimeDouble.createWritable(2);

		System.out.println(zero.add(one).toString());
		System.out.println(vzero.add(vone).toString());
		System.out.println(zero.add(vone).toString());
		System.out.println(vzero.add(one).toString());
		System.out.println();
		System.out.println(one.add(two).toString());
		System.out.println(vone.add(vtwo).toString());
		System.out.println(one.add(vtwo).toString());
		System.out.println(vone.add(two).toString());
		System.out.println();
		System.out.println(two.add(two).toString());
		System.out.println(vtwo.add(vtwo).toString());
		System.out.println(two.add(vtwo).toString());
		System.out.println(vtwo.add(two).toString());
		System.out.println();
		System.out.println(one.divide(two).toString());
		System.out.println(vone.divide(vtwo).toString());
		System.out.println(one.divide(vtwo).toString());
		System.out.println(vone.divide(two).toString());
		System.out.println();
		System.out.println(two.divide(one).toString());
		System.out.println(vtwo.divide(vone).toString());
		System.out.println(two.divide(vone).toString());
		System.out.println(vtwo.divide(one).toString());
		System.out.println();
		System.out.println(two.divide(two).toString());
		System.out.println(vtwo.divide(vtwo).toString());
		System.out.println(two.divide(vtwo).toString());
		System.out.println(vtwo.divide(two).toString());
		System.out.println();
		System.out.println(two.minus(zero).toString());
		System.out.println(vtwo.minus(vzero).toString());
		System.out.println(two.minus(vzero).toString());
		System.out.println(vtwo.minus(zero).toString());
		System.out.println();
		System.out.println(two.minus(one).toString());
		System.out.println(vtwo.minus(vone).toString());
		System.out.println(two.minus(vone).toString());
		System.out.println(vtwo.minus(one).toString());
		System.out.println();
		System.out.println(zero.minus(one).toString());
		System.out.println(vzero.minus(vone).toString());
		System.out.println(zero.minus(vone).toString());
		System.out.println(vzero.minus(one).toString());
		System.out.println();
	}

}
