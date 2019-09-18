/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArray;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArraySet;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArraySlice;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericArrayGet;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericArrayNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericArrayNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericAtTime;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericConditional;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericIsNull;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericPre;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericSetNull;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericSnapshot;
import org.roboticsapi.facet.javarcc.primitives.generic.JGenericValue;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotationArray;

public class PrimitivesRotation {

	public static class JRotationArray extends JAbstractArray<RPIRotation, RPIRotationArray> {
		@Override
		protected RPIRotationArray createArray(int size) {
			return new RPIRotationArray(size);
		}
	}

	public static class JRotationArrayGet extends JGenericArrayGet<RPIRotation, RPIRotationArray> {
	}

	public static class JRotationArrayInterNetcommIn extends JGenericInterNetcommIn<RPIRotationArray> {
	}

	public static class JRotationArrayInterNetcommOut extends JGenericInterNetcommOut<RPIRotationArray> {
	}

	public static class JRotationArrayNetcommIn extends JGenericArrayNetcommIn<RPIRotation, RPIRotationArray> {
	}

	public static class JRotationArrayNetcommOut extends JGenericArrayNetcommOut<RPIRotation, RPIRotationArray> {
	}

	public static class JRotationArraySet extends JAbstractArraySet<RPIRotation, RPIRotationArray> {
		@Override
		protected RPIRotationArray createArray(int size) {
			return new RPIRotationArray(size);
		}
	}

	public static class JRotationArraySlice extends JAbstractArraySlice<RPIRotation, RPIRotationArray> {
		@Override
		protected RPIRotationArray createArray(int size) {
			return new RPIRotationArray(size);
		}
	}

	public static class JRotationAtTime extends JGenericAtTime<RPIRotation> {
	}

	public static class JRotationConditional extends JGenericConditional<RPIRotation> {
	}

	public static class JRotationInterNetcommIn extends JGenericInterNetcommIn<RPIRotation> {
	}

	public static class JRotationInterNetcommOut extends JGenericInterNetcommOut<RPIRotation> {
	}

	public static class JRotationIsNull extends JGenericIsNull<RPIRotation> {
	}

	public static class JRotationNetcommIn extends JGenericNetcommIn<RPIRotation> {
	}

	public static class JRotationNetcommOut extends JGenericNetcommOut<RPIRotation> {
	}

	public static class JRotationPre extends JGenericPre<RPIRotation> {
	}

	public static class JRotationSetNull extends JGenericSetNull<RPIRotation> {
	}

	public static class JRotationSnapshot extends JGenericSnapshot<RPIRotation> {
		public JRotationSnapshot() {
			super(new RPIRotation());
		}
	}

	public static class JRotationValue extends JGenericValue<RPIRotation> {
	}
}
