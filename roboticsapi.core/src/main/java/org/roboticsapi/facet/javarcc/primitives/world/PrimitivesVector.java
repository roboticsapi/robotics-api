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
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVectorArray;

public class PrimitivesVector {

	public static class JVectorArray extends JAbstractArray<RPIVector, RPIVectorArray> {
		@Override
		protected RPIVectorArray createArray(int size) {
			return new RPIVectorArray(size);
		}
	}

	public static class JVectorArrayGet extends JGenericArrayGet<RPIVector, RPIVectorArray> {
	}

	public static class JVectorArrayInterNetcommIn extends JGenericInterNetcommIn<RPIVectorArray> {
	}

	public static class JVectorArrayInterNetcommOut extends JGenericInterNetcommOut<RPIVectorArray> {
	}

	public static class JVectorArrayNetcommIn extends JGenericArrayNetcommIn<RPIVector, RPIVectorArray> {
	}

	public static class JVectorArrayNetcommOut extends JGenericArrayNetcommOut<RPIVector, RPIVectorArray> {
	}

	public static class JVectorArraySet extends JAbstractArraySet<RPIVector, RPIVectorArray> {
		@Override
		protected RPIVectorArray createArray(int size) {
			return new RPIVectorArray(size);
		}
	}

	public static class JVectorArraySlice extends JAbstractArraySlice<RPIVector, RPIVectorArray> {
		@Override
		protected RPIVectorArray createArray(int size) {
			return new RPIVectorArray(size);
		}
	}

	public static class JVectorAtTime extends JGenericAtTime<RPIVector> {
	}

	public static class JVectorConditional extends JGenericConditional<RPIVector> {
	}

	public static class JVectorInterNetcommIn extends JGenericInterNetcommIn<RPIVector> {
	}

	public static class JVectorInterNetcommOut extends JGenericInterNetcommOut<RPIVector> {
	}

	public static class JVectorIsNull extends JGenericIsNull<RPIVector> {
	}

	public static class JVectorNetcommIn extends JGenericNetcommIn<RPIVector> {
	}

	public static class JVectorNetcommOut extends JGenericNetcommOut<RPIVector> {
	}

	public static class JVectorPre extends JGenericPre<RPIVector> {
	}

	public static class JVectorSetNull extends JGenericSetNull<RPIVector> {
	}

	public static class JVectorSnapshot extends JGenericSnapshot<RPIVector> {
		public JVectorSnapshot() {
			super(new RPIVector());
		}
	}

	public static class JVectorValue extends JGenericValue<RPIVector> {
	}
}
