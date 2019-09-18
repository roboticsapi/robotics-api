/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArray;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArraySet;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractArraySlice;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractBinaryOp;
import org.roboticsapi.facet.javarcc.primitives.generic.JAbstractUnaryOp;
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
import org.roboticsapi.facet.runtime.rpi.ArrayType;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIboolArray;

public class PrimitivesBoolean {

	private static boolean notNullAnd(RPIbool bool, boolean value) {
		return bool != null && bool.get() == value;
	}

	public static class JBooleanAnd extends JAbstractBinaryOp<RPIbool> {
		@Override
		protected RPIbool op(RPIbool first, RPIbool second) {
			if (notNullAnd(first, false) || notNullAnd(second, false))
				return new RPIbool(false);
			if (notNullAnd(first, true) && notNullAnd(second, true))
				return new RPIbool(true);
			return null;
		}
	}

	public static class JBooleanNot extends JAbstractUnaryOp<RPIbool, RPIbool> {
		@Override
		protected RPIbool op(RPIbool value) {
			if (value == null)
				return null;
			return new RPIbool(!value.get());
		}
	}

	public static class JBooleanOr extends JAbstractBinaryOp<RPIbool> {
		@Override
		protected RPIbool op(RPIbool first, RPIbool second) {
			if (notNullAnd(first, true) || notNullAnd(second, true))
				return new RPIbool(true);
			if (notNullAnd(first, false) && notNullAnd(second, false))
				return new RPIbool(false);
			return null;
		}
	}

	public static class JBooleanArray extends JAbstractArray<RPIbool, RPIboolArray> {
		@Override
		protected RPIboolArray createArray(int size) {
			return new RPIboolArray(size);
		}
	}

	public static class JBooleanArrayGet extends JGenericArrayGet<RPIbool, RPIboolArray> {
	}

	public static class JBooleanArraySet extends JAbstractArraySet<RPIbool, RPIboolArray> {
		@Override
		protected RPIboolArray createArray(int size) {
			return new RPIboolArray(size);
		}
	}

	public static class JBooleanArraySlice extends JAbstractArraySlice<RPIbool, RPIboolArray> {
		@Override
		protected RPIboolArray createArray(int size) {
			return new RPIboolArray(size);
		}
	}

	public static class JBooleanArrayInterNetcommIn extends JGenericInterNetcommIn<RPIboolArray> {
	}

	public static class JBooleanArrayInterNetcommOut extends JGenericInterNetcommOut<RPIboolArray> {
	}

	public static class JBooleanArrayNetcommIn extends JGenericArrayNetcommIn<RPIbool, RPIboolArray> {
	}

	public static class JBooleanArrayNetcommOut extends JGenericArrayNetcommOut<RPIbool, RPIboolArray> {
	}

	public static class JBooleanAtTime extends JGenericAtTime<RPIbool> {
	}

	public static class JBooleanConditional extends JGenericConditional<RPIbool> {
	}

	public static class JBooleanIsNull extends JGenericIsNull<RPIbool> {
	}

	public static class JBooleanPre extends JGenericPre<RPIbool> {
	}

	public static class JBooleanSetNull extends JGenericSetNull<RPIbool> {
	}

	public static class JBooleanArraySetNull extends JGenericSetNull<ArrayType<RPIbool>> {
		JParameter<Integer> size = add("Size", new JParameter<Integer>());
	}

	public static class JBooleanArrayIsNull extends JGenericIsNull<ArrayType<RPIbool>> {
	}

	public static class JBooleanSnapshot extends JGenericSnapshot<RPIbool> {
		public JBooleanSnapshot() {
			super(new RPIbool());
		}
	}

	public static class JBooleanValue extends JGenericValue<RPIbool> {
	}

	public static class JBooleanInterNetcommIn extends JGenericInterNetcommIn<RPIbool> {
	}

	public static class JBooleanInterNetcommOut extends JGenericInterNetcommOut<RPIbool> {
	}

	public static class JBooleanNetcommIn extends JGenericNetcommIn<RPIbool> {
	}

	public static class JBooleanNetcommOut extends JGenericNetcommOut<RPIbool> {
	}

}
