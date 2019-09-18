/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper.runtime;

import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.relation.CommandedPosition;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.mapping.AbstractActuatorDriver;
import org.roboticsapi.framework.gripper.GripperDriver;
import org.roboticsapi.framework.gripper.ParallelGripper;

public abstract class AbstractParallelGripperDriver<T extends ParallelGripper> extends AbstractActuatorDriver<T>
		implements GripperDriver {

	private CommandedPosition gripperConnection1;
	private CommandedPosition gripperConnection2;

	@Override
	protected abstract boolean checkDeviceType(String deviceType);

	@Override
	protected abstract boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces);

	@Override
	protected void onPresent() {
		super.onPresent();
		removeGripperConnection();
		createGripperConnection();
	}

	private final void createGripperConnection() {
		gripperConnection1 = new CommandedPosition(getDevice().getBaseJawOrigin(0), getDevice().getBaseJaw(0).getBase(),
				RealtimeTransformation.createFromXYZABC(getBaseJawOpeningWidth().divide(2), RealtimeDouble.ZERO,
						RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO),
				null);
		gripperConnection1.establish();

		gripperConnection2 = new CommandedPosition(getDevice().getBaseJawOrigin(1), getDevice().getBaseJaw(1).getBase(),
				RealtimeTransformation.createFromXYZABC(getBaseJawOpeningWidth().divide(2), RealtimeDouble.ZERO,
						RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO),
				null);
		gripperConnection2.establish();
	}

	public abstract RealtimeDouble getBaseJawOpeningWidth();

	@Override
	protected void onAbsent() {
		removeGripperConnection();

		super.onAbsent();
	}

	private final void removeGripperConnection() {
		if (gripperConnection1 != null)
			gripperConnection1.remove();
		if (gripperConnection2 != null)
			gripperConnection2.remove();
		gripperConnection1 = null;
		gripperConnection2 = null;
	}

}
