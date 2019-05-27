/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArray;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArraySet;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractArraySlice;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractEquals;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractStrictBinaryOp;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractStrictBinaryPredicate;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractStrictUnaryOp;
import org.roboticsapi.runtime.core.javarcc.primitives.generic.JAbstractUnaryOp;
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
import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIdoubleArray;
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.rpi.types.ArrayType;

public class PrimitivesDouble {

	public static class JDoubleAdd extends JAbstractStrictBinaryOp<RPIdouble> {
		@Override
		protected RPIdouble op(RPIdouble first, RPIdouble second) {
			return new RPIdouble(first.get() + second.get());
		}
	}

	public static class JDoubleMultiply extends JAbstractStrictBinaryOp<RPIdouble> {
		@Override
		protected RPIdouble op(RPIdouble first, RPIdouble second) {
			return new RPIdouble(first.get() * second.get());
		}
	}

	public static class JDoubleDivide extends JAbstractStrictBinaryOp<RPIdouble> {
		@Override
		protected RPIdouble op(RPIdouble first, RPIdouble second) {
			return new RPIdouble(first.get() / second.get());
		}
	}

	public static class JDoubleEquals extends JAbstractEquals<RPIdouble> {
		@Override
		protected RPIbool equals(RPIdouble first, RPIdouble second, RPIdouble epsilon) {
			return new RPIbool(Math.abs(first.get() - second.get()) <= epsilon.get());
		}
	}

	public static class JDoubleGreater extends JAbstractStrictBinaryPredicate<RPIdouble> {
		@Override
		protected RPIbool pred(RPIdouble first, RPIdouble second) {
			return new RPIbool(first.get() > second.get());
		}
	}

	public static class JDoubleFromInt extends JAbstractUnaryOp<RPIint, RPIdouble> {
		@Override
		protected RPIdouble op(RPIint value) {
			if (value == null)
				return null;
			return new RPIdouble((double) value.get());
		}
	}

	public static class JDoublePower extends JAbstractStrictBinaryOp<RPIdouble> {
		@Override
		protected RPIdouble op(RPIdouble first, RPIdouble second) {
			return new RPIdouble(Math.pow(first.get(), second.get()));
		}
	}

	public static class JDoubleSquareRoot extends JAbstractStrictUnaryOp<RPIdouble, RPIdouble> {
		@Override
		protected RPIdouble op(RPIdouble value) {
			return new RPIdouble(Math.sqrt(value.get()));
		}
	}

	public static class JDoubleArray extends JAbstractArray<RPIdouble, RPIdoubleArray> {
		@Override
		protected RPIdoubleArray createArray(int size) {
			return new RPIdoubleArray(size);
		}
	}

	public static class JDoubleArrayGet extends JGenericArrayGet<RPIdouble, RPIdoubleArray> {
	}

	public static class JDoubleArraySet extends JAbstractArraySet<RPIdouble, RPIdoubleArray> {
		@Override
		protected RPIdoubleArray createArray(int size) {
			return new RPIdoubleArray(size);
		}
	}

	public static class JDoubleArraySlice extends JAbstractArraySlice<RPIdouble, RPIdoubleArray> {
		@Override
		protected RPIdoubleArray createArray(int size) {
			return new RPIdoubleArray(size);
		}
	}

	public static class JDoubleArrayInterNetcommIn extends JGenericInterNetcommIn<RPIdoubleArray> {
	}

	public static class JDoubleArrayInterNetcommOut extends JGenericInterNetcommOut<RPIdoubleArray> {
	}

	public static class JDoubleArrayNetcommIn extends JGenericArrayNetcommIn<RPIdouble, RPIdoubleArray> {
	}

	public static class JDoubleArrayNetcommOut extends JGenericArrayNetcommOut<RPIdouble, RPIdoubleArray> {
	}

	public static class JDoubleAtTime extends JGenericAtTime<RPIdouble> {
	}

	public static class JDoubleConditional extends JGenericConditional<RPIdouble> {
	}

	public static class JDoubleIsNull extends JGenericIsNull<RPIdouble> {
	}

	public static class JDoublePre extends JGenericPre<RPIdouble> {
	}

	public static class JDoubleSetNull extends JGenericSetNull<RPIdouble> {
	}

	public static class JDoubleArrayIsNull extends JGenericIsNull<ArrayType<RPIdouble>> {
	}

	public static class JDoubleArraySetNull extends JGenericSetNull<ArrayType<RPIdouble>> {
		JParameter<Integer> size = add("Size", new JParameter<Integer>());
	}

	public static class JDoubleSnapshot extends JGenericSnapshot<RPIdouble> {
		public JDoubleSnapshot() {
			super(new RPIdouble());
		}
	}

	public static class JDoubleValue extends JGenericValue<RPIdouble> {
	}

	public static class JDoubleInterNetcommIn extends JGenericInterNetcommIn<RPIdouble> {
	}

	public static class JDoubleInterNetcommOut extends JGenericInterNetcommOut<RPIdouble> {
	}

	public static class JDoubleNetcommIn extends JGenericNetcommIn<RPIdouble> {
	}

	public static class JDoubleNetcommOut extends JGenericNetcommOut<RPIdouble> {
	}

}
