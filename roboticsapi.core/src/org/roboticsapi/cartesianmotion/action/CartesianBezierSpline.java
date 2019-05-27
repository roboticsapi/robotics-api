/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.cartesianmotion.plan.CartesianBezierPlan;
import org.roboticsapi.cartesianmotion.plan.CartesianBezierSplinePlan;
import org.roboticsapi.cartesianmotion.plan.CartesianConditionalPlan;
import org.roboticsapi.cartesianmotion.plan.CartesianConstantPositionPlan;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActionState;
import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

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

	public static class AtPoint extends ActionState {
		private final int point;

		public AtPoint(final int point) {
			this.point = point;
		}

		public int getPoint() {
			return point;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj) && point == ((AtPoint) obj).point;
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
	public CartesianBezierSpline(final double transVel, final double rotVel) {
		super(0);
		this.transVel = transVel;
		this.rotVel = rotVel;
	}

	/**
	 * Adds a new point to the spline
	 * 
	 * @param point spline point to drive to
	 */
	public void addPoint(final SplinePoint point) {
		points.add(point);
	}

	/**
	 * Adds a new point to the spline
	 * 
	 * @param frame frame to drive to
	 */
	public void addPoint(final Frame frame) {
		points.add(new SplinePoint(frame, transVel));
	}

	/**
	 * Retrieves the list of spline points
	 * 
	 * @return read-only list of spline points
	 */
	public List<SplinePoint> getPoints() {
		return Collections.unmodifiableList(points);
	}

	@Override
	public Frame getFrom() {
		SplinePoint firstPoint = getPoints().get(0);
		return firstPoint != null ? firstPoint.getFrame() : null;
	}

	@Override
	public Frame getTo() {
		List<SplinePoint> pts = getPoints();
		SplinePoint lastPoint = pts.get(pts.size() - 1);
		return lastPoint != null ? lastPoint.getFrame() : null;
	}

	/**
	 * Action event when a certain control point is reached
	 * 
	 * @param nr number of point
	 * @return Action event at the given point
	 */
	public ActionState getAtPoint(final int nr) {
		return new AtPoint(nr).setAction(this);
	}

	/**
	 * Action event when a certain control point is reached (static access)
	 * 
	 * @param nr number of point
	 * @return Action event at the given point
	 */
	public static ActionState atPoint(final int nr) {
		return new AtPoint(nr);
	}

	/**
	 * A point in a Bezier spline
	 */
	public static class SplinePoint {
		/** destination frame */
		Frame frame;

		/** direction to pass through frame */
		Vector direction;

		/** axis of rotation when passing through frame */
		Vector rotation;

		/** velocity at the point (m/s) */
		double velocity;

		public Frame getFrame() {
			return frame;
		}

		public Vector getDirection() {
			return direction;
		}

		public double getVelocity() {
			return velocity;
		}

		public void setDirection(final Vector direction) {
			this.direction = direction;
		}

		public void setVelocity(final double velocity) {
			this.velocity = velocity;
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
		public SplinePoint(final Frame frame, final Vector direction, final double velocity) {
			this.frame = frame;
			this.direction = (direction != null ? direction.normalize() : null);
			this.velocity = velocity;
		}

		/**
		 * Creates a spline point (velocity = 0)
		 * 
		 * @param frame point to stop at
		 */
		public SplinePoint(final Frame frame) {
			this(frame, null, 0);
		}

		/**
		 * Creates a spline point with a given velocity
		 * 
		 * @param frame    point
		 * @param velocity velocity (m/s)
		 */
		public SplinePoint(final Frame frame, final double velocity) {
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

		final Frame origin = points.get(0).getFrame();

		ExecutableCartesianMotionPlan ret = null;
		Transformation lastTrans = null;
		for (int i = 0; i < points.size() - 1; i++) {
			final SplinePoint from = points.get(i), to = points.get(i + 1);
			Transformation tFrom = origin.getTransformationTo(from.getFrame(), false);
			final Transformation tTo = lastTrans = origin.getTransformationTo(to.getFrame(), false);
			Vector vFrom = tFrom.getRotation().apply(from.getDirection().scale(from.getVelocity()));
			Vector rFrom = tFrom.getRotation().apply(from.getRotation().scale(from.getVelocity()));
			Vector vTo = tTo.getRotation().apply(to.getDirection().scale(to.getVelocity()));
			Vector rTo = tTo.getRotation().apply(to.getRotation().scale(to.getVelocity()));

			ExecutableCartesianMotionPlan bezierFragment = new CartesianBezierPlan(origin, tFrom,
					new Twist(vFrom, rFrom), tTo, new Twist(vTo, rTo), times[i], times[i + 1] - times[i]);

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
	 * @throws MappingException if start or end point have velocity, but no
	 *                          direction
	 */
	private void calculateDirections(final List<SplinePoint> points) throws RoboticsException {
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
			throws RoboticsException {
		final double[] ret = new double[points.size()];
		int nr = 0;
		double time = 0;
		ret[nr++] = time;
		// calculate durations
		final Frame origin = points.get(0).getFrame();
		for (int i = 0; i < points.size() - 1; i++) {
			final SplinePoint from = points.get(i), to = points.get(i + 1);
			final Transformation tFrom = origin.getTransformationTo(from.getFrame(), false);
			final Transformation tTo = origin.getTransformationTo(to.getFrame(), false);
			if (tFrom == null) {
				throw new RoboticsException("Spline switches between different dynamic frames.");
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
		}
		return ret;
	}

}
