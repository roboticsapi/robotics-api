/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.mapper;

import java.util.Map;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAdd;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleConditional;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleGreater;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleMultiply;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleSnapshot;
import org.roboticsapi.facet.runtime.rpi.core.primitives.EdgeDetection;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Interval;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Rampify;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.framework.multijoint.action.JointErrorCorrection;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;

public class JointErrorCorrectionMapper implements ActionMapper<JointErrorCorrection> {

	@Override
	public ActionResult map(JointErrorCorrection action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, final RealtimeDouble time,
			Map<PlannedAction<?>, Plan> plans) throws MappingException, RpiException {

		ActionResult innerResult = registry.mapAction(action.getWrappedMotion(), parameters, cancel, override, time,
				plans);

		if (innerResult instanceof MultiJointActionResult) {
			MultiJointActionResult inner = (MultiJointActionResult) innerResult;
			ActionResult[] innerResults = inner.getJointResults();

			final double[] startPos = action.getStart();
			final RealtimeDouble[] realPos = new RealtimeDouble[action.getJoints().size()];
			final IdentityRealtimeDouble[] correctPos = new IdentityRealtimeDouble[realPos.length];
			final RealtimeDouble[] innerPos = new RealtimeDouble[realPos.length];

			for (int i = 0; i < innerResults.length; i++) {
				if (innerResults[i] instanceof JointPositionActionResult) {
					innerPos[i] = ((JointPositionActionResult) innerResults[i]).getPosition();
					realPos[i] = action.getJoints().get(i).getCommandedRealtimePosition();
					correctPos[i] = new IdentityRealtimeDouble("<<resynchronize>>(" + innerPos[i] + ")",
							time.getRuntime());
					innerResults[i] = new JointPositionActionResult(null, null, correctPos[i]);
				} else {
					throw new MappingException("Inner action did not return JointPositionActionResult.");
				}
			}

			final double duration = plans.get(action.getWrappedMotion()) == null ? 1
					: plans.get(action.getWrappedMotion()).getTotalTime() * 0.45;

			MultiJointActionResult ret = new MultiJointActionResult(action, RealtimeBoolean.TRUE, innerResults);
			ret.addRealtimeValueSource(innerResult);
			ret.addRealtimeValueFragmentFactory(new TypedRealtimeValueFragmentFactory<Double, IdentityRealtimeDouble>(
					IdentityRealtimeDouble.class) {
				@Override
				protected RealtimeValueFragment<Double> createFragment(IdentityRealtimeDouble value)
						throws MappingException, RpiException {
					for (int i = 0; i < correctPos.length; i++) {
						if (value == correctPos[i]) {

							double start = startPos[i];
							RealtimeDouble real = realPos[i];
							RealtimeDouble inner = innerPos[i];

							double resyncTime = duration;
							double starttime = 0;

							RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value);
							FragmentInPort inTime = ret.addInPort("inTime");
							ret.addDependency(time, inTime);

							FragmentInPort inPos = ret.addInPort("inPos");
							ret.addDependency(inner, inPos);

							// snapshot time
							DoubleGreater t = ret.add(new DoubleGreater(0.0, starttime));
							ret.connect(inTime.getInternalOutPort(), t.getInFirst());

							EdgeDetection snapshot = ret.add(new EdgeDetection(true));
							ret.connect(t.getOutValue(), snapshot.getInValue());

							// resynchronize
							Interval interval = ret.add(new Interval(starttime, starttime + resyncTime));
							ret.connect(inTime.getInternalOutPort(), interval.getInValue());

							Rampify ramp = ret.add(new Rampify(0.0));
							ret.connect(interval.getOutValue(), ramp.getInValue());

							// calculate position for each axis
							DoubleSnapshot ssp = ret.add(new DoubleSnapshot());
							ret.connect(snapshot.getOutValue(), ssp.getInSnapshot());
							ret.addDependency(real, "inRealPos", ssp.getInValue());

							DoubleAdd intervalMinusOne = ret.add(new DoubleAdd(0.0, -1.0));
							ret.connect(ramp.getOutValue(), intervalMinusOne.getInFirst());
							DoubleMultiply intervalInverted = ret.add(new DoubleMultiply(0.0, -1.0));
							ret.connect(intervalMinusOne.getOutValue(), intervalInverted.getInFirst());

							DoubleAdd diff = ret.add(new DoubleAdd(0.0, -start));
							ret.connect(ssp.getOutValue(), diff.getInFirst());

							DoubleMultiply delta = ret.add(new DoubleMultiply());
							ret.connect(diff.getOutValue(), delta.getInFirst());
							ret.connect(intervalInverted.getOutValue(), delta.getInSecond());

							DoubleAdd add = ret.add(new DoubleAdd());
							ret.connect(delta.getOutValue(), add.getInFirst());

							DoubleConditional result = ret.add(new DoubleConditional());
							ret.connect(t.getOutValue(), result.getInCondition());
							ret.connect(add.getOutValue(), result.getInTrue());

							ret.connect(inPos.getInternalOutPort(), add.getInSecond());
							ret.connect(inPos.getInternalOutPort(), result.getInFalse());
							ret.defineResult(result.getOutValue());

							return ret;
						}
					}
					return null;
				}
			});
			return ret;

		} else {
			throw new MappingException("Mapping of inner action did not return JointPositionActionResult.");
		}

	}
}
