/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import java.util.List;

import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline.AtPoint;
import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline.SplinePoint;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CartesianBezierFragment;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CartesianConditionalFragment;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CartesianConstantPositionFragment;
import org.roboticsapi.runtime.cartesianmotion.mapper.fragments.CartesianMotionFragment;
import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.impl.AbstractStatePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class CartesianBezierSplineMapper implements ActionMapper<SoftRobotRuntime, CartesianBezierSpline> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, CartesianBezierSpline action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);
		final List<SplinePoint> points = action.getPoints();
		if (mp == null) {
			throw new MappingException("No motion center parameters given");
		}

		// calculate internal directions and times
		calculateDirections(points);
		final double[] times = calculateTimes(points, action.getTranslationalVelocity(),
				action.getRotationalVelocity());

		final Frame origin = points.get(0).getFrame();

		// build a net
		final NetFragment spl = new NetFragment("Spline");

		// tick counter
		DataflowOutPort overridePort = ports.overridePort;

		final Clock clock = spl.add(new Clock());

		spl.connect(overridePort, spl.addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
		DataflowOutPort timePort = spl.addOutPort(new DoubleDataflow(), false, clock.getOutValue());

		CartesianMotionFragment ret = null;
		Transformation lastTrans = null;
		for (int i = 0; i < points.size() - 1; i++) {
			try {
				final SplinePoint from = points.get(i), to = points.get(i + 1);
				Transformation tFrom = origin.getTransformationTo(from.getFrame(), false);
				final Transformation tTo = lastTrans = origin.getTransformationTo(to.getFrame(), false);
				Vector vFrom = tFrom.getRotation().apply(from.getDirection().scale(from.getVelocity()));
				Vector rFrom = tFrom.getRotation().apply(from.getRotation().scale(from.getVelocity()));
				Vector vTo = tTo.getRotation().apply(to.getDirection().scale(to.getVelocity()));
				Vector rTo = tTo.getRotation().apply(to.getRotation().scale(to.getVelocity()));

				CartesianBezierFragment fragment = spl.add(new CartesianBezierFragment(tFrom, new Twist(vFrom, rFrom),
						tTo, new Twist(vTo, rTo), times[i], times[i + 1] - times[i], timePort));

				if (ret == null) {
					ret = fragment;
				} else {
					CartesianConditionalFragment conditional = spl.add(new CartesianConditionalFragment(fragment, ret,
							times[i], times[i + 1] - times[i], timePort));
					ret = conditional;
				}
			} catch (TransformationException e) {
				throw new MappingException(e);
			}
		}

		CartesianConditionalFragment conditional = spl.add(new CartesianConditionalFragment(ret,
				spl.add(new CartesianConstantPositionFragment(lastTrans)), 0, times[times.length - 1], timePort));
		ret = conditional;

		// for (double t = 0; t < times[times.length - 1]; t += 0.1) {
		// Transformation trans = ret.getTransformationAt(t);
		// new Frame("Zeit " + t, origin, trans);
		// }

		// total progress
		final Interval progress = spl.add(new Interval(0d, times[times.length - 1]));
		progress.getInValue().connectTo(clock.getOutValue());

		// completed logic
		final BooleanNot completed = spl.add(new BooleanNot());
		completed.getInValue().connectTo(progress.getOutActive());

		DataflowOutPort resultPort = spl.reinterpret(ret.getResultPort(),
				new RelationDataflow(origin, mp.getMotionCenter()));
		ActionResult result = new CartesianPositionActionResult(resultPort);

		final PlannedActionMapperResult mapperResult = new PlannedActionMapperResult(action, spl, result,
				spl.addOutPort(new StateDataflow(), false, completed.getOutValue()),
				spl.addOutPort(new DoubleDataflow(), false, progress.getOutValue()));

		mapperResult.addStatePortFactory(AtPoint.class, new AbstractStatePortFactory<AtPoint>() {
			@Override
			public List<DataflowOutPort> createTypedStatePort(AtPoint state) throws MappingException {
				double timePercent = times[state.getPoint()] / times[times.length - 1];
				return mapperResult.getStatePort(new PlannedAction.TimeProgressState(timePercent));
			}
		});
		return mapperResult;

		// // total progress
		// final Interval progress = new Interval(0d, times[times.length - 1]);
		// progress.getInValue().connectTo(tc.getOutValue());
		// spl.add(progress);
		//
		// // completed logic
		// final BooleanNot stopped = new BooleanNot();
		// stopped.getInValue().connectTo(progress.getOutActive());
		// spl.add(stopped);
		//
		// // motion
		// OutPort outframe = null; // result port
		// InPort inframe = null;
		// for (int i = 0; i < points.size() - 1; i++) {
		// try {
		// final SplinePoint from = points.get(i), to = points.get(i + 1);
		// final Transformation tFrom = origin.getTransformationTo(
		// from.getFrame(), false);
		// final Transformation tTo = origin.getTransformationTo(
		// to.getFrame(), false);
		// // create bezier spline from p0 over p1 and p2 to p3 during
		// // duration
		// final Vector p0 = tFrom.getTranslation(), p3 = tTo
		// .getTranslation();
		// final Vector t0 = tFrom.apply(from.getDirection()).add(
		// tFrom.getTranslation().invert());
		// final Vector t3 = tTo.apply(to.getDirection()).add(
		// tTo.getTranslation().invert());
		// Vector p1, p2;
		// final double lambda = (times[i + 1] - times[i]);
		// p1 = p0.add(t0.scale(from.getVelocity() / 3 * lambda));
		// p2 = p3.add(t3.scale(-to.getVelocity() / 3 * lambda));
		//
		// // tick scaler
		// final Interval ts = new Interval();
		// spl.add(ts);
		// ts.getInValue().connectTo(tc.getOutValue());
		// ts.setMin(times[i]);
		// ts.setMax(times[i + 1]);
		//
		// // bezier
		// final CubicBezier cbx = new CubicBezier(p0.getX(), p3.getX(),
		// p1.getX(), p2.getX());
		// spl.add(cbx);
		// cbx.getInActive().connectTo(ts.getOutActive());
		// cbx.getInValue().connectTo(ts.getOutValue());
		//
		// final CubicBezier cby = new CubicBezier(p0.getY(), p3.getY(),
		// p1.getY(), p2.getY());
		// spl.add(cby);
		// cby.getInActive().connectTo(ts.getOutActive());
		// cby.getInValue().connectTo(ts.getOutValue());
		//
		// final CubicBezier cbz = new CubicBezier(p0.getZ(), p3.getZ(),
		// p1.getZ(), p2.getZ());
		// spl.add(cbz);
		// cbz.getInActive().connectTo(ts.getOutActive());
		// cbz.getInValue().connectTo(ts.getOutValue());
		//
		// // frame combiner
		// final VectorFromXYZ pos = spl.add(new VectorFromXYZ());
		// pos.getInX().connectTo(cbx.getOutValue());
		// pos.getInY().connectTo(cby.getOutValue());
		// pos.getInZ().connectTo(cbz.getOutValue());
		//
		// final FrameFromPosRot fc = spl.add(new FrameFromPosRot());
		// fc.getInPos().connectTo(pos.getOutValue());
		//
		// // merge
		// if (i == points.size() - 2) {
		// inframe.connectTo(fc.getOutValue());
		// } else {
		// final FrameConditional merge = new FrameConditional();
		// spl.add(merge);
		// merge.getInCondition().connectTo(ts.getOutActive());
		// merge.getInTrue().connectTo(fc.getOutValue());
		// if (outframe == null) {
		// outframe = merge.getOutValue();
		// } else {
		// inframe.connectTo(merge.getOutValue());
		// }
		// inframe = merge.getInFalse();
		// }
		// } catch (RoboticsException e) {
		// throw new MappingException(e);
		// }
		// }
		//
		// DataflowOutPort resultPort = spl.addOutPort(new RelationDataflow(
		// origin, mp.getMotionCenter()), true, outframe);
		// DataflowOutPort timePort = spl.addOutPort(new DoubleDataflow(),
		// false,
		// tc.getOutValue());
		// DataflowOutPort progressPort = spl.addOutPort(new DoubleDataflow(),
		// false, progress.getOutValue());
		// DataflowOutPort completedPort = spl.addOutPort(new StateDataflow(),
		// false, stopped.getOutValue());
		// return new CartesianBezierSplineMapperResult(action, spl,
		// new JointPositionActionResult(resultPort), completedPort,
		// progressPort, timePort);
	}

	/**
	 * Calculates the directions at the control points (based on previous and next
	 * control point)
	 *
	 * @throws MappingException if start or end point have velocity, but no
	 *                          direction
	 */
	private void calculateDirections(final List<SplinePoint> points) throws MappingException {
		// fill velocity and direction for start and end point
		if (points.get(0).getVelocity() == 0 || points.get(0).getDirection() == null) {
			points.get(0).setDirection(new Vector());
			points.get(0).setRotation(new Vector());
			points.get(0).setVelocity(0);
		}

		if (points.get(points.size() - 1).getVelocity() == 0 || points.get(points.size() - 1).getDirection() == null) {
			points.get(points.size() - 1).setDirection(new Vector());
			points.get(points.size() - 1).setRotation(new Vector());
			points.get(points.size() - 1).setVelocity(0);
		}

		// set internal directions (equal time instants)
		for (int i = 1; i < points.size() - 1; i++) {
			try {
				final Transformation next = points.get(i).getFrame().getTransformationTo(points.get(i + 1).getFrame());
				final Transformation prev = points.get(i).getFrame().getTransformationTo(points.get(i - 1).getFrame());
				Transformation diff = points.get(i - 1).getFrame().getTransformationTo(points.get(i + 1).getFrame());
				if (points.get(i).getDirection() == null) {
					points.get(i).setDirection(next.getTranslation().add(prev.getTranslation().invert()).normalize());
				}
				if (points.get(i).getRotation() == null) {
					Rotation rot = prev.multiply(diff).getRotation();
					points.get(i).setRotation(rot.getAxis());
				}
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
	}

	/**
	 * Calculates the times when control points are reached
	 *
	 * @param rotVel
	 *
	 * @return array of milliseconds when all the control points are reached
	 * @throws MappingException when some destination frames are dynamically linked
	 */
	private double[] calculateTimes(final List<SplinePoint> points, final double transVel, final double rotVel)
			throws MappingException {
		final double[] ret = new double[points.size()];
		int nr = 0;
		double time = 0;
		ret[nr++] = time;
		// calculate durations
		final Frame origin = points.get(0).getFrame();
		for (int i = 0; i < points.size() - 1; i++) {
			try {
				final SplinePoint from = points.get(i), to = points.get(i + 1);
				final Transformation tFrom = origin.getTransformationTo(from.getFrame(), false);
				final Transformation tTo = origin.getTransformationTo(to.getFrame(), false);
				if (tFrom == null) {
					throw new MappingException("Spline switches between different dynamic frames.");
				}
				final Vector p0 = tFrom.getTranslation(), p3 = tTo.getTranslation();
				double duration;
				final Vector dp = p3.add(p0.invert());
				final double angle = Math.abs(tTo.getRotation().multiply(tFrom.getRotation().invert()).getAngle());
				if (from.getVelocity() == 0 && to.getVelocity() == 0) {
					// use overall velocity in center
					duration = Math.max(dp.getLength() / transVel, angle / rotVel) * 3 / 2;
				} else {
					// center velocity is average of start and end speed
					final Vector t0 = tFrom.apply(from.getDirection()).add(tFrom.getTranslation().invert());
					final Vector t3 = tTo.apply(to.getDirection()).add(tTo.getTranslation().invert());
					final Vector st = t3.scale(to.getVelocity() / 3).add(t0.scale(from.getVelocity() / 3));
					final double a = (from.getVelocity() + to.getVelocity()) * (from.getVelocity() + to.getVelocity())
							- (st.getLength() * st.getLength()) / 4;
					final double b = 3 * (dp.getX() * st.getX() + dp.getY() * st.getY() + dp.getZ() * st.getZ());
					final double c = -9 * dp.getLength() * dp.getLength();
					final double lambda = -b + Math.sqrt(b * b - 4 * a * c) / 2 / a;
					duration = Math.max(lambda, angle * 3 / 2 / rotVel);
				}
				time += duration;
				ret[nr++] = time;
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
		}
		return ret;
	}
}
