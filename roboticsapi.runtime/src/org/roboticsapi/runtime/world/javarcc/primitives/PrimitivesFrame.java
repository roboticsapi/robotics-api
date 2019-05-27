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
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPIFrameArray;

public class PrimitivesFrame {

	public static class JFrameArray extends JAbstractArray<RPIFrame, RPIFrameArray> {
		@Override
		protected RPIFrameArray createArray(int size) {
			return new RPIFrameArray(size);
		}
	}

	public static class JFrameArrayGet extends JGenericArrayGet<RPIFrame, RPIFrameArray> {
	}

	public static class JFrameArrayInterNetcommIn extends JGenericInterNetcommIn<RPIFrameArray> {
	}

	public static class JFrameArrayInterNetcommOut extends JGenericInterNetcommOut<RPIFrameArray> {
	}

	public static class JFrameArrayNetcommIn extends JGenericArrayNetcommIn<RPIFrame, RPIFrameArray> {
	}

	public static class JFrameArrayNetcommOut extends JGenericArrayNetcommOut<RPIFrame, RPIFrameArray> {
	}

	public static class JFrameArraySet extends JAbstractArraySet<RPIFrame, RPIFrameArray> {
		@Override
		protected RPIFrameArray createArray(int size) {
			return new RPIFrameArray(size);
		}
	}

	public static class JFrameArraySlice extends JAbstractArraySlice<RPIFrame, RPIFrameArray> {
		@Override
		protected RPIFrameArray createArray(int size) {
			return new RPIFrameArray(size);
		}
	}

	public static class JFrameAtTime extends JGenericAtTime<RPIFrame> {
	}

	public static class JFrameConditional extends JGenericConditional<RPIFrame> {
	}

	public static class JFrameInterNetcommIn extends JGenericInterNetcommIn<RPIFrame> {
	}

	public static class JFrameInterNetcommOut extends JGenericInterNetcommOut<RPIFrame> {
	}

	public static class JFrameIsNull extends JGenericIsNull<RPIFrame> {
	}

	public static class JFrameNetcommIn extends JGenericNetcommIn<RPIFrame> {
	}

	public static class JFrameNetcommOut extends JGenericNetcommOut<RPIFrame> {
	}

	public static class JFramePre extends JGenericPre<RPIFrame> {
	}

	public static class JFrameSetNull extends JGenericSetNull<RPIFrame> {
	}

	public static class JFrameSnapshot extends JGenericSnapshot<RPIFrame> {
		public JFrameSnapshot() {
			super(new RPIFrame());
		}
	}

	public static class JFrameValue extends JGenericValue<RPIFrame> {
	}
}
