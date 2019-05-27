/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.extension;

import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.cartesianmotion.action.CartesianJogging;
import org.roboticsapi.cartesianmotion.action.FollowCartesianGoal;
import org.roboticsapi.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.cartesianmotion.action.HybridMoveAction;
import org.roboticsapi.cartesianmotion.action.LIN;
import org.roboticsapi.cartesianmotion.action.LINFromMotion;
import org.roboticsapi.cartesianmotion.action.LimitedJerkLIN;
import org.roboticsapi.cartesianmotion.action.MoveToCartesianGoal;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianBezierSplineMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianErrorCorrectionMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianJoggingMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.ExecutablePathMotionMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.FollowCartesianGoalMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.HoldCartesianPositionMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.HoldCartesianVelocityMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.HybridMoveMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.MoveToCartesianGoalMapper;
import org.roboticsapi.runtime.mapping.MapperRegistry;

public class SoftRobotCartesianMotionExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject runtime) {
		if (runtime instanceof SoftRobotRuntime) {
			onRuntimeAvailable((SoftRobotRuntime) runtime);
		}
	}

	@Override
	public void onUnavailable(RoboticsObject runtime) {
		if (runtime instanceof SoftRobotRuntime) {
			onRuntimeUnavailable((SoftRobotRuntime) runtime);
		}
	}

	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {
		MapperRegistry mapperregistry = runtime.getMapperRegistry();

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, CartesianBezierSpline.class,
				new CartesianBezierSplineMapper());
		// mapperregistry.registerActionMapper(SoftRobotRuntime.class,
		// LIN.class,
		// new LINMapper());
		// mapperregistry.registerActionMapper(SoftRobotRuntime.class,
		// LINFromMotion.class, new LINFromMotionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, LIN.class, new ExecutablePathMotionMapper<LIN>());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, LimitedJerkLIN.class,
				new ExecutablePathMotionMapper<LimitedJerkLIN>());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, LINFromMotion.class,
				new ExecutablePathMotionMapper<LINFromMotion>());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, CartesianErrorCorrection.class,
				new CartesianErrorCorrectionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, HoldCartesianPosition.class,
				new HoldCartesianPositionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, CartesianJogging.class,
				new CartesianJoggingMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, FollowCartesianGoal.class,
				new FollowCartesianGoalMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, MoveToCartesianGoal.class,
				new MoveToCartesianGoalMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, HoldCartesianVelocity.class,
				new HoldCartesianVelocityMapper());

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, HybridMoveAction.class, new HybridMoveMapper());
	}

	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
		// TODO remove mappers???
	}
}
