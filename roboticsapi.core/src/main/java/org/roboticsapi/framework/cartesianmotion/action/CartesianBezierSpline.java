/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.plan.CartesianBezierPlan;
import org.roboticsapi.framework.cartesianmotion.plan.CartesianBezierSplinePlan;
import org.roboticsapi.framework.cartesianmotion.plan.CartesianConditionalPlan;
import org.roboticsapi.framework.cartesianmotion.plan.CartesianConstantPositionPlan;

/**
 * A Cartesian bezier spline through a set of points (optionally with given
 * direction and velocity)
 */
public class CartesianBezierSpline extends PathMotion<CartesianBezierSplinePlan> {
	/** control points of the motion */
	private final List<SplinePoint> points = new ArrayList<SplinePoint>();

	/** overall velocity */
	private final double transVel;

	private final double rotVel;

	public double getTranslationalVelocity() {
		return transVel;
	}

	public double getRotationalVelocity() {
		return rotVel;
	}

	public static class AtPointRealtimeBoolean extends ActionRealtimeBoolean {
		private final int point;

		public AtPointRealtimeBoolean(Command scope, Action action, final int point) {
			super(scope, action);
			this.point = point;
		}

		public int getPoint() {
			return point;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && point == ((AtPointRealtimeBoolean) obj).point;
		}

		@Override
		public int hashCode() {
			return HashCodeUtil.hash(super.hashCode(), point);
		}
	}

	/**
	 * creates a new Cartesian bezier spline
	 *
	 * @param transVel overall / default translational velocity
	 * @param rotVel   overall / default rotational velocity
	 */
	public CartesianBezierSpline(final double transVel, final double rotVel, SplinePoint... points) {
		super(0);
		this.transVel = transVel;
		this.rotVel = rotVel;
		for (SplinePoint p : points) {
			this.points.add(p);
		}
		from = this.points.get(0).frame;
		to = this.points.get(this.points.size()).frame;
	}

	/**
	 * creates a new Cartesian bezier spline
	 *
	 * @param transVel overall / default translational velocity
	 * @param rotVel   overall / default rotational velocity
	 */
	public CartesianBezierSpline(final double transVel, final double rotVel, Pose... points) {
		super(0);
		this.transVel = transVel;
		this.rotVel = rotVel;
		for (Pose p : points) {
			this.points.add(new SplinePoint(p));
		}
		from = this.points.get(0).frame;
		to = this.points.get(this.points.size() - 1).frame;
	}

	/**
	 * Retrieves the list of spline points
	 *
	 * @return read-only list of spline points
	 */
	public List<SplinePoint> getPoints() {
		return Collections.unmodifiableList(points);
	}

	/**
	 * Action event when a certain control point is reached
	 *
	 * @param nr number of point
	 * @return Action event at the given point
	 */
	public ActionRealtimeBoolean getAtPoint(Command scope, final int nr) {
		return new AtPointRealtimeBoolean(scope, this, nr);
	}

	/**
	 * A point in a Bezier spline
	 */
	public static class SplinePoint {
		/** destination frame */
		Pose frame;

		/** direction to pass through frame */
		Vector direction;

		/** axis of rotation when passing through frame */
		Vector rotation;

		/** velocity at the point (m/s) */
		double transVelocity;

		/** rotation velocity at the point (rad/s) */
		double rotVelocity;

		public Pose getPose() {
			return frame;
		}

		public Vector getDirection() {
			return direction;
		}

		public double getTranslationVelocity() {
			return transVelocity;
		}

		public double getRotationVelocity() {
			return rotVelocity;
		}

		public void setDirection(final Vector direction) {
			this.direction = direction;
		}

		public void setTranslationVelocity(final double velocity) {
			this.transVelocity = velocity;
		}

		public void setRotationVelocity(final double velocity) {
			this.rotVelocity = velocity;
		}

		public Vector getRotation() {
			return rotation;
		}

		public void setRotation(Vector rotation) {
			this.rotation = rotation;
		}

		/**
		 * Creates a spline point with given frame, direction and velocity
		 *
		 * @param frame     point
		 * @param direction direction
		 * @param velocity  velocity (m/s)
		 */
		public SplinePoint(final Pose frame, final Vector direction, final double velocity) {
			this.frame = frame;
			this.direction = (direction != null ? direction.normalize() : null);
			this.transVelocity = velocity;
		}

		/**
		 * Creates a spline point (velocity = 0)
		 *
		 * @param frame point to stop at
		 */
		public SplinePoint(final Pose frame) {
			this(frame, null, 0);
		}

		/**
		 * Creates a spline point with a given velocity
		 *
		 * @param frame    point
		 * @param velocity velocity (m/s)
		 */
		public SplinePoint(final Pose frame, final double velocity) {
			this(frame, null, velocity);
		}
	}

	@Override
	public void calculatePlan(Map<PlannedAction<?>, Plan> plans, DeviceParameterBag parameters)
			throws RoboticsException {

		final MotionCenterParameter mp = parameters.get(MotionCenterParameter.class);
		final List<SplinePoint> points = getPoints();
		if (mp == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		// calculate internal directions and times
		calculateDirections(points);
		final double[] times = calculateTimes(points, getTranslationalVelocity(), getRotationalVelocity());

		final Frame origin = points.get(0).getPose().getReference();

		ExecutableCartesianMotionPlan ret = null;
		Transformation lastTrans = null;
		for (int i = 0; i < points.size() - 1; i++) {
			final SplinePoint from = points.get(i), to = points.get(i + 1);
			double duration = times[i + 1] - times[i];
			Transformation tFrom = origin.asPose().getTransformationTo(from.getPose(),
					World.getCommandedTopology().withoutDynamic());
			final Transformation tTo = lastTrans = origin.asPose().getTransformationTo(to.getPose(),
					World.getCommandedTopology().withoutDynamic());
			Vector vFrom = tFrom.getRotation().apply(from.getDirection().scale(from.getTranslationVelocity()));
			Vector rFrom = tFrom.getRotation().apply(from.getRotation().scale(from.getRotationVelocity() / duration));
			Vector vTo = tTo.getRotation().apply(to.getDirection().scale(to.getTranslationVelocity()));
			Vector rTo = tTo.getRotation().apply(to.getRotation().scale(to.getRotationVelocity() / duration));
			ExecutableCartesianMotionPlan bezierFragment = new CartesianBezierPlan(origin, tFrom,
					new Twist(vFrom, rFrom), tTo, new Twist(vTo, rTo), times[i], duration);

			if (ret == null) {
				ret = bezierFragment;
			} else {
				CartesianConditionalPlan conditional = new CartesianConditionalPlan(bezierFragment, ret, times[i],
						times[i + 1] - times[i]);
				ret = conditional;
			}
		}

		CartesianConditionalPlan conditional = new CartesianConditionalPlan(ret,
				new CartesianConstantPositionPlan(origin, lastTrans), 0, times[times.length - 1]);
		plans.put(this, new CartesianBezierSplinePlan(conditional, times));

	}

	/**
	 * Calculates the directions at the control points (based on previous and next
	 * control point)
	 *
	 */
	private void calculateDirections(final List<SplinePoint> points) throws RoboticsException {
		// fill velocity and direction for start and end point
		if (points.get(0).getTranslationVelocity() == 0 || points.get(0).getDirection() == null) {
			points.get(0).setDirection(new Vector());
			points.get(0).setRotation(new Vector());
			points.get(0).setTranslationVelocity(0);
			points.get(0).setRotationVelocity(0);
		}

		if (points.get(points.size() - 1).getTranslationVelocity() == 0
				|| points.get(points.size() - 1).getDirection() == null) {
			points.get(points.size() - 1).setDirection(new Vector());
			points.get(points.size() - 1).setRotation(new Vector());
			points.get(points.size() - 1).setTranslationVelocity(0);
			points.get(points.size() - 1).setRotationVelocity(0);
		}

		// set internal directions (equal time instants)
		for (int i = 1; i < points.size() - 1; i++) {
			final Transformation next = points.get(i).getPose()
					.getCommandedTransformationTo(points.get(i + 1).getPose());
			final Transformation prev = points.get(i).getPose()
					.getCommandedTransformationTo(points.get(i - 1).getPose());
			Transformation diff = points.get(i - 1).getPose().getCommandedTransformationTo(points.get(i + 1).getPose());
			if (points.get(i).getDirection() == null) {
				points.get(i).setDirection(next.getTranslation().add(prev.getTranslation().invert()).normalize());
				points.get(i).setTranslationVelocity(transVel);
			}
			if (points.get(i).getRotation() == null) {
				Vector localAxis = diff.getRotation().getAxis();
				points.get(i).setRotation(prev.getRotation().apply(localAxis));
				points.get(i).setRotationVelocity(rotVel * diff.getRotation().getAngle());
			}
		}
	}

	/**
	 * Calculates the times when control points are reached
	 *
	 * @param rotVel
	 *
	 * @return array of milliseconds when all the control points are reached
	 */
	private double[] calculateTimes(final List<SplinePoint> points, final double transVel, final double rotVel)
			throws RoboticsException {
		final double[] ret = new double[points.size()];
		int nr = 0;
		double time = 0;
		ret[nr++] = time;
		// calculate durations
		final Pose origin = points.get(0).getPose();
		for (int i = 0; i < points.size() - 1; i++) {
			final SplinePoint from = points.get(i), to = points.get(i + 1);
			final Transformation tFrom = origin.getTransformationTo(from.getPose(),
					World.getCommandedTopology().withoutDynamic());
			final Transformation tTo = origin.getTransformationTo(to.getPose(),
					World.getCommandedTopology().withoutDynamic());
			if (tFrom == null) {
				throw new RoboticsException("Spline switches between different dynamic frames.");
			}
			final Vector p0 = tFrom.getTranslation(), p3 = tTo.getTranslation();
			double duration;
			final Vector dp = p3.add(p0.invert());
			final double angle = Math.abs(tFrom.invert().multiply(tTo).getRotation().getAngle());
			if (from.getTranslationVelocity() == 0 && to.getTranslationVelocity() == 0) {
				// use overall velocity in center
				duration = Math.max(dp.getLength() / transVel, angle / rotVel) * 3 / 2;
			} else {
				// center velocity is 90% of maximum velocity
				final Vector t0 = tFrom.getRotation().apply(from.getDirection());
				final Vector t3 = tTo.getRotation().apply(to.getDirection());
				final Vector st = t3.scale(to.getTranslationVelocity()).add(t0.scale(from.getTranslationVelocity()));
				final double destVel = Math.max(from.getTranslationVelocity(), to.getTranslationVelocity()) * 0.9;
				final double a = (4 * destVel * destVel) - st.dot(st) / 4;
				final double b = 3 * dp.dot(st);
				final double c = -9 * dp.dot(dp);
				final double lambda = (-b + Math.sqrt(b * b - 4 * a * c)) / 2 / a;
				duration = Math.max(lambda, angle * 3 / 2 / rotVel);
			}
			time += duration;
			ret[nr++] = time;
		}
		return ret;
	}

}
