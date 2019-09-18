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
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwistArray;

public class PrimitivesTwist {

	public static class JTwistArray extends JAbstractArray<RPITwist, RPITwistArray> {
		@Override
		protected RPITwistArray createArray(int size) {
			return new RPITwistArray(size);
		}
	}

	public static class JTwistArrayGet extends JGenericArrayGet<RPITwist, RPITwistArray> {
	}

	public static class JTwistArrayInterNetcommIn extends JGenericInterNetcommIn<RPITwistArray> {
	}

	public static class JTwistArrayInterNetcommOut extends JGenericInterNetcommOut<RPITwistArray> {
	}

	public static class JTwistArrayNetcommIn extends JGenericArrayNetcommIn<RPITwist, RPITwistArray> {
	}

	public static class JTwistArrayNetcommOut extends JGenericArrayNetcommOut<RPITwist, RPITwistArray> {
	}

	public static class JTwistArraySet extends JAbstractArraySet<RPITwist, RPITwistArray> {
		@Override
		protected RPITwistArray createArray(int size) {
			return new RPITwistArray(size);
		}
	}

	public static class JTwistArraySlice extends JAbstractArraySlice<RPITwist, RPITwistArray> {
		@Override
		protected RPITwistArray createArray(int size) {
			return new RPITwistArray(size);
		}
	}

	public static class JTwistAtTime extends JGenericAtTime<RPITwist> {
	}

	public static class JTwistConditional extends JGenericConditional<RPITwist> {
	}

	public static class JTwistInterNetcommIn extends JGenericInterNetcommIn<RPITwist> {
	}

	public static class JTwistInterNetcommOut extends JGenericInterNetcommOut<RPITwist> {
	}

	public static class JTwistIsNull extends JGenericIsNull<RPITwist> {
	}

	public static class JTwistNetcommIn extends JGenericNetcommIn<RPITwist> {
	}

	public static class JTwistNetcommOut extends JGenericNetcommOut<RPITwist> {
	}

	public static class JTwistPre extends JGenericPre<RPITwist> {
	}

	public static class JTwistSetNull extends JGenericSetNull<RPITwist> {
	}

	public static class JTwistSnapshot extends JGenericSnapshot<RPITwist> {
		public JTwistSnapshot() {
			super(new RPITwist());
		}
	}

	public static class JTwistValue extends JGenericValue<RPITwist> {
	}
}
