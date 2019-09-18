/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.util;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

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
