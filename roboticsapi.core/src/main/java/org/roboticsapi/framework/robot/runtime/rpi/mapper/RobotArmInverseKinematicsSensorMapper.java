/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleArrayFragment;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmInverseKinematicsRealtimeDoubleArray;
import org.roboticsapi.framework.robot.runtime.rpi.primitives.InvKin;

public class RobotArmInverseKinematicsSensorMapper
		extends TypedRealtimeValueFragmentFactory<Double[], RobotArmInverseKinematicsRealtimeDoubleArray> {

	public RobotArmInverseKinematicsSensorMapper() {
		super(RobotArmInverseKinematicsRealtimeDoubleArray.class);
	}

	@Override
	protected RealtimeValueFragment<Double[]> createFragment(RobotArmInverseKinematicsRealtimeDoubleArray value)
			throws MappingException, RpiException {

		RealtimeValueFragment<Double[]> ret = new RealtimeDoubleArrayFragment(value);
		final InvKin kin = ret.add(new InvKin(value.getDriver().getRpiDeviceName()));
		ret.addDependency(value.getTransformation(), "inFrame", kin.getInFrame());
		ret.addDependency(RealtimeDoubleArray.createFromComponents(value.getHintJoints()), "inHintJoints",
				kin.getInHintJoints());
		ret.defineResult(kin.getOutJoints());
		return ret;
	}
}
