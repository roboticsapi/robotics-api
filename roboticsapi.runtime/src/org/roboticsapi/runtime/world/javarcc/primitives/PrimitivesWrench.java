/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArray;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArraySet;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArraySlice;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericArrayGet;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericArrayNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericArrayNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericAtTime;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericConditional;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericPre;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericSetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericSnapshot;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JGenericValue;
import org.roboticsapi.runtime.world.types.RPIWrench;
import org.roboticsapi.runtime.world.types.RPIWrenchArray;

public class PrimitivesWrench {

	public static class JWrenchArray extends JAbstractArray<RPIWrench, RPIWrenchArray> {
		@Override
		protected RPIWrenchArray createArray(int size) {
			return new RPIWrenchArray(size);
		}
	}

	public static class JWrenchArrayGet extends JGenericArrayGet<RPIWrench, RPIWrenchArray> {
	}

	public static class JWrenchArrayInterNetcommIn extends JGenericInterNetcommIn<RPIWrenchArray> {
	}

	public static class JWrenchArrayInterNetcommOut extends JGenericInterNetcommOut<RPIWrenchArray> {
	}

	public static class JWrenchArrayNetcommIn extends JGenericArrayNetcommIn<RPIWrench, RPIWrenchArray> {
	}

	public static class JWrenchArrayNetcommOut extends JGenericArrayNetcommOut<RPIWrench, RPIWrenchArray> {
	}

	public static class JWrenchArraySet extends JAbstractArraySet<RPIWrench, RPIWrenchArray> {
		@Override
		protected RPIWrenchArray createArray(int size) {
			return new RPIWrenchArray(size);
		}
	}

	public static class JWrenchArraySlice extends JAbstractArraySlice<RPIWrench, RPIWrenchArray> {
		@Override
		protected RPIWrenchArray createArray(int size) {
			return new RPIWrenchArray(size);
		}
	}

	public static class JWrenchAtTime extends JGenericAtTime<RPIWrench> {
	}

	public static class JWrenchConditional extends JGenericConditional<RPIWrench> {
	}

	public static class JWrenchInterNetcommIn extends JGenericInterNetcommIn<RPIWrench> {
	}

	public static class JWrenchInterNetcommOut extends JGenericInterNetcommOut<RPIWrench> {
	}

	public static class JWrenchIsNull extends JGenericIsNull<RPIWrench> {
	}

	public static class JWrenchNetcommIn extends JGenericNetcommIn<RPIWrench> {
	}

	public static class JWrenchNetcommOut extends JGenericNetcommOut<RPIWrench> {
	}

	public static class JWrenchPre extends JGenericPre<RPIWrench> {
	}

	public static class JWrenchSetNull extends JGenericSetNull<RPIWrench> {
	}

	public static class JWrenchSnapshot extends JGenericSnapshot<RPIWrench> {
		public JWrenchSnapshot() {
			super(new RPIWrench());
		}
	}

	public static class JWrenchValue extends JGenericValue<RPIWrench> {
	}
}
