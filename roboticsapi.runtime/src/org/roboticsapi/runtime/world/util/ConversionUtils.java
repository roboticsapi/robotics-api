/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.util;

import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

public final class ConversionUtils {

	public static final Transformation toTransformation(RPIFrame frame) {
		return new Transformation(toRotation(frame.getRot()), toVector(frame.getPos()));
	}

	public static final Rotation toRotation(RPIRotation rot) {
		return new Rotation(rot.getA().get(), rot.getB().get(), rot.getC().get());
	}

	public static final Vector toVector(RPIVector pos) {
		return new Vector(pos.getX().get(), pos.getY().get(), pos.getZ().get());
	}

}
