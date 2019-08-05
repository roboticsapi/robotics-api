/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.javarcc.primitives;

import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanAnd;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArray;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayGet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArrayNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArraySet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArraySetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanArraySlice;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanAtTime;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanConditional;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanNot;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanOr;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanPre;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanSetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanSnapshot;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesBoolean.JBooleanValue;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleAdd;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArray;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayGet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArrayNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArraySet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArraySetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleArraySlice;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleAtTime;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleConditional;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleDivide;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleEquals;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleFromInt;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleGreater;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleMultiply;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoublePower;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoublePre;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleSetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleSnapshot;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleSquareRoot;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesDouble.JDoubleValue;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntAdd;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArray;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayGet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArrayNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArraySet;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArraySetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntArraySlice;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntAtTime;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntConditional;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntDivide;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntEquals;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntFromDouble;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntGreater;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntInterNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntInterNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntIsNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntMultiply;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntNetcommIn;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntNetcommOut;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntPre;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntSetNull;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntSnapshot;
import org.roboticsapi.runtime.core.javarcc.primitives.PrimitivesInt.JIntValue;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;

public class CoreExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {

		ep.registerPrimitive("Cancel", JCancel.class);
		ep.registerPrimitive("Takeover", JTakeover.class);

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
		ep.registerPrimitive("Core::OTG2", JOTG2.class);
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
