/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmForwardKinematicsRealtimeTransformation;
import org.roboticsapi.framework.robot.runtime.rpi.primitives.Kin;

public class RobotArmForwardKinematicsSensorMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, RobotArmForwardKinematicsRealtimeTransformation> {

	public RobotArmForwardKinematicsSensorMapper() {
		super(RobotArmForwardKinematicsRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(
			RobotArmForwardKinematicsRealtimeTransformation value) throws MappingException, RpiException {

		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value);
		final Kin kin = ret.add(new Kin(value.getDriver().getRpiDeviceName()));
		ret.addDependency(RealtimeDoubleArray.createFromComponents(value.getJointValues()), "inJoints",
				kin.getInJoints());
		ret.defineResult(kin.getOutFrame());
		return ret;
	}
}
