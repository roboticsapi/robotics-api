/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.framework.cartesianmotion.action.CartesianErrorCorrection;
import org.roboticsapi.framework.cartesianmotion.action.FollowCartesianGoal;
import org.roboticsapi.framework.cartesianmotion.action.FollowCartesianVelocity;
import org.roboticsapi.framework.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.framework.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.framework.cartesianmotion.action.LIN;
import org.roboticsapi.framework.cartesianmotion.action.LINFromMotion;
import org.roboticsapi.framework.cartesianmotion.action.LimitedJerkLIN;
import org.roboticsapi.framework.cartesianmotion.action.MoveToCartesianGoal;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianCommandedPositionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianCommandedVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianErrorCorrectionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianMeasuredPositionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianMeasuredVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.ExecutablePathMotionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.FollowCartesianGoalMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.FollowCartesianVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.HoldCartesianPositionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.HoldCartesianVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.MoveToCartesianGoalMapper;

public class CartesianMotionRuntimeExtension extends RpiExtension {

	@Override
	protected void registerMappers(MapperRegistry mr) {
		mr.registerRealtimeValueMapper(new CartesianCommandedPositionMapper());
		mr.registerRealtimeValueMapper(new CartesianMeasuredPositionMapper());
		mr.registerRealtimeValueMapper(new CartesianCommandedVelocityMapper());
		mr.registerRealtimeValueMapper(new CartesianMeasuredVelocityMapper());

		mr.registerActionMapper(CartesianBezierSpline.class, new ExecutablePathMotionMapper<CartesianBezierSpline>());
		mr.registerActionMapper(LIN.class, new ExecutablePathMotionMapper<LIN>());
		mr.registerActionMapper(LimitedJerkLIN.class, new ExecutablePathMotionMapper<LimitedJerkLIN>());
		mr.registerActionMapper(LINFromMotion.class, new ExecutablePathMotionMapper<LINFromMotion>());
		mr.registerActionMapper(CartesianErrorCorrection.class, new CartesianErrorCorrectionMapper());
		mr.registerActionMapper(HoldCartesianPosition.class, new HoldCartesianPositionMapper());
		mr.registerActionMapper(FollowCartesianGoal.class, new FollowCartesianGoalMapper());
		mr.registerActionMapper(MoveToCartesianGoal.class, new MoveToCartesianGoalMapper());
		mr.registerActionMapper(HoldCartesianVelocity.class, new HoldCartesianVelocityMapper());
		mr.registerActionMapper(FollowCartesianVelocity.class, new FollowCartesianVelocityMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {

	}

}
