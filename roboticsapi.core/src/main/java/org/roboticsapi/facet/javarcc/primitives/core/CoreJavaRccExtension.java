/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.javarcc.primitives.JCancel;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanAnd;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArray;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayGet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArrayNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArraySet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArraySetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanArraySlice;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanAtTime;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanConditional;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanNot;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanOr;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanPre;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanSetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanSnapshot;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesBoolean.JBooleanValue;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleAdd;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArray;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayGet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArrayNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArraySet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArraySetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleArraySlice;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleAtTime;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleConditional;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleDivide;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleEquals;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleFromInt;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleGreater;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleMultiply;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoublePower;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoublePre;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleSetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleSnapshot;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleSquareRoot;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesDouble.JDoubleValue;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntAdd;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArray;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayGet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArrayNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArraySet;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArraySetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntArraySlice;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntAtTime;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntConditional;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntDivide;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntEquals;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntFromDouble;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntGreater;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntInterNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntInterNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntIsNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntMultiply;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntNetcommIn;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntNetcommOut;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntPre;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntSetNull;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntSnapshot;
import org.roboticsapi.facet.javarcc.primitives.core.PrimitivesInt.JIntValue;

public class CoreJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {

		ep.registerPrimitive("Cancel", JCancel.class);

		ep.registerPrimitive("Core::BooleanAnd", JBooleanAnd.class);
		ep.registerPrimitive("Core::BooleanArray", JBooleanArray.class);
		ep.registerPrimitive("Core::BooleanArrayGet", JBooleanArrayGet.class);
		ep.registerPrimitive("Core::BooleanArrayInterNetcommIn", JBooleanArrayInterNetcommIn.class);
		ep.registerPrimitive("Core::BooleanArrayInterNetcommOut", JBooleanArrayInterNetcommOut.class);
		ep.registerPrimitive("Core::BooleanArrayNetcommIn", JBooleanArrayNetcommIn.class);
		ep.registerPrimitive("Core::BooleanArrayNetcommOut", JBooleanArrayNetcommOut.class);
		ep.registerPrimitive("Core::BooleanArraySet", JBooleanArraySet.class);
		ep.registerPrimitive("Core::BooleanArraySlice", JBooleanArraySlice.class);

		ep.registerPrimitive("Core::BooleanArraySetNull", JBooleanArraySetNull.class);
		ep.registerPrimitive("Core::BooleanArrayIsNull", JBooleanArrayIsNull.class);

		ep.registerPrimitive("Core::BooleanAtTime", JBooleanAtTime.class);
		ep.registerPrimitive("Core::BooleanConditional", JBooleanConditional.class);
		ep.registerPrimitive("Core::BooleanHistory", JBooleanHistory.class);
		ep.registerPrimitive("Core::BooleanInterNetcommIn", JBooleanInterNetcommIn.class);
		ep.registerPrimitive("Core::BooleanInterNetcommOut", JBooleanInterNetcommOut.class);
		ep.registerPrimitive("Core::BooleanIsNull", JBooleanIsNull.class);
		ep.registerPrimitive("Core::BooleanLastN", JBooleanLastN.class);
		ep.registerPrimitive("Core::BooleanNetcommIn", JBooleanNetcommIn.class);
		ep.registerPrimitive("Core::BooleanNetcommOut", JBooleanNetcommOut.class);
		ep.registerPrimitive("Core::BooleanNot", JBooleanNot.class);
		ep.registerPrimitive("Core::BooleanOr", JBooleanOr.class);
		ep.registerPrimitive("Core::BooleanPre", JBooleanPre.class);
		ep.registerPrimitive("Core::BooleanSetNull", JBooleanSetNull.class);
		ep.registerPrimitive("Core::BooleanSnapshot", JBooleanSnapshot.class);
		ep.registerPrimitive("Core::BooleanValue", JBooleanValue.class);

		ep.registerPrimitive("Core::DoubleAdd", JDoubleAdd.class);
		ep.registerPrimitive("Core::DoubleArray", JDoubleArray.class);
		ep.registerPrimitive("Core::DoubleArrayGet", JDoubleArrayGet.class);
		ep.registerPrimitive("Core::DoubleArrayInterNetcommIn", JDoubleArrayInterNetcommIn.class);
		ep.registerPrimitive("Core::DoubleArrayInterNetcommOut", JDoubleArrayInterNetcommOut.class);
		ep.registerPrimitive("Core::DoubleArrayNetcommIn", JDoubleArrayNetcommIn.class);
		ep.registerPrimitive("Core::DoubleArrayNetcommOut", JDoubleArrayNetcommOut.class);
		ep.registerPrimitive("Core::DoubleArraySet", JDoubleArraySet.class);
		ep.registerPrimitive("Core::DoubleArraySlice", JDoubleArraySlice.class);
		ep.registerPrimitive("Core::DoubleArraySetNull", JDoubleArraySetNull.class);
		ep.registerPrimitive("Core::DoubleArrayIsNull", JDoubleArrayIsNull.class);
		ep.registerPrimitive("Core::DoubleAtTime", JDoubleAtTime.class);
		ep.registerPrimitive("Core::DoubleAsin", JDoubleAsin.class);
		ep.registerPrimitive("Core::DoubleAcos", JDoubleAcos.class);
		ep.registerPrimitive("Core::DoubleAtan2", JDoubleAtan2.class);
		ep.registerPrimitive("Core::DoubleAverage", JDoubleAverage.class);
		ep.registerPrimitive("Core::DoubleBezier", JDoubleBezier.class);
		ep.registerPrimitive("Core::DoubleConditional", JDoubleConditional.class);
		ep.registerPrimitive("Core::DoubleCos", JDoubleCos.class);
		ep.registerPrimitive("Core::DoubleDivide", JDoubleDivide.class);
		ep.registerPrimitive("Core::DoubleEquals", JDoubleEquals.class);
		ep.registerPrimitive("Core::DoubleFromInt", JDoubleFromInt.class);
		ep.registerPrimitive("Core::DoubleGreater", JDoubleGreater.class);
		ep.registerPrimitive("Core::DoubleInterNetcommIn", JDoubleInterNetcommIn.class);
		ep.registerPrimitive("Core::DoubleInterNetcommOut", JDoubleInterNetcommOut.class);
		ep.registerPrimitive("Core::DoubleIsNull", JDoubleIsNull.class);
		ep.registerPrimitive("Core::DoubleMultiply", JDoubleMultiply.class);
		ep.registerPrimitive("Core::DoubleNetcommIn", JDoubleNetcommIn.class);
		ep.registerPrimitive("Core::DoubleNetcommOut", JDoubleNetcommOut.class);
		ep.registerPrimitive("Core::DoublePower", JDoublePower.class);
		ep.registerPrimitive("Core::DoublePre", JDoublePre.class);
		ep.registerPrimitive("Core::DoubleSetNull", JDoubleSetNull.class);
		ep.registerPrimitive("Core::DoubleSin", JDoubleSin.class);
		ep.registerPrimitive("Core::DoubleSnapshot", JDoubleSnapshot.class);
		ep.registerPrimitive("Core::DoubleSquareRoot", JDoubleSquareRoot.class);
		ep.registerPrimitive("Core::DoubleTan", JDoubleTan.class);
		ep.registerPrimitive("Core::DoubleValue", JDoubleValue.class);

		ep.registerPrimitive("Core::IntAdd", JIntAdd.class);
		ep.registerPrimitive("Core::IntArray", JIntArray.class);
		ep.registerPrimitive("Core::IntArrayGet", JIntArrayGet.class);
		ep.registerPrimitive("Core::IntArrayInterNetcommIn", JIntArrayInterNetcommIn.class);
		ep.registerPrimitive("Core::IntArrayInterNetcommOut", JIntArrayInterNetcommOut.class);
		ep.registerPrimitive("Core::IntArrayNetcommIn", JIntArrayNetcommIn.class);
		ep.registerPrimitive("Core::IntArrayNetcommOut", JIntArrayNetcommOut.class);
		ep.registerPrimitive("Core::IntArraySet", JIntArraySet.class);
		ep.registerPrimitive("Core::IntArraySlice", JIntArraySlice.class);
		ep.registerPrimitive("Core::IntArraySetNull", JIntArraySetNull.class);
		ep.registerPrimitive("Core::IntArrayIsNull", JIntArrayIsNull.class);
		ep.registerPrimitive("Core::IntAtTime", JIntAtTime.class);
		ep.registerPrimitive("Core::IntConditional", JIntConditional.class);
		ep.registerPrimitive("Core::IntDivide", JIntDivide.class);
		ep.registerPrimitive("Core::IntEquals", JIntEquals.class);
		ep.registerPrimitive("Core::IntFromDouble", JIntFromDouble.class);
		ep.registerPrimitive("Core::IntGreater", JIntGreater.class);
		ep.registerPrimitive("Core::IntInterNetcommIn", JIntInterNetcommIn.class);
		ep.registerPrimitive("Core::IntInterNetcommOut", JIntInterNetcommOut.class);
		ep.registerPrimitive("Core::IntIsNull", JIntIsNull.class);
		ep.registerPrimitive("Core::IntMultiply", JIntMultiply.class);
		ep.registerPrimitive("Core::IntNetcommIn", JIntNetcommIn.class);
		ep.registerPrimitive("Core::IntNetcommOut", JIntNetcommOut.class);
		ep.registerPrimitive("Core::IntPre", JIntPre.class);
		ep.registerPrimitive("Core::IntSetNull", JIntSetNull.class);
		ep.registerPrimitive("Core::IntSnapshot", JIntSnapshot.class);
		ep.registerPrimitive("Core::IntValue", JIntValue.class);

		ep.registerPrimitive("Core::Clock", JClock.class);
		ep.registerPrimitive("Core::CubicBezier", JCubicBezier.class);
		ep.registerPrimitive("Core::CycleTime", JCycleTime.class);
		ep.registerPrimitive("Core::EdgeDetection", JEdgeDetection.class);
		ep.registerPrimitive("Core::Interval", JInterval.class);
		ep.registerPrimitive("Core::Lerp", JLerp.class);
		ep.registerPrimitive("Core::Rampify", JRampify.class);
		ep.registerPrimitive("Core::OTG", JOTG.class);
		ep.registerPrimitive("Core::TimeDiff", JTimeDiff.class);
		ep.registerPrimitive("Core::TimeNet", JTimeNet.class);
		ep.registerPrimitive("Core::TimeWall", JTimeWall.class);
		ep.registerPrimitive("Core::TimePre", JTimePre.class);
		ep.registerPrimitive("Core::Trigger", JTrigger.class);
		ep.registerPrimitive("Core::TimeHistory", JTimeHistoryEfficient.class);
		ep.registerPrimitive("Core::TimeAdd", JTimeAdd.class);
		ep.registerPrimitive("Core::TimeConsistentRange", JTimeConsistentRangeEfficient.class);

	}

}
