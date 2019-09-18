/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.controller.JointImpedanceController;
import org.roboticsapi.framework.multijoint.controller.JointPositionController;
import org.roboticsapi.systemtest.DeviceInterfaceTest;
import org.roboticsapi.systemtest.DeviceInterfaceTestMethod;

public class JointControllerInterfaceTests implements DeviceInterfaceTest<JointControllerInterface> {

	@Override
	public Class<JointControllerInterface> getDeviceInterfaceType() {
		return JointControllerInterface.class;
	}

	@DeviceInterfaceTestMethod
	public void testSwitchToJointPositionControllerExecutesWithoutError(JointControllerInterface jci)
			throws RoboticsException {
		jci.switchJointController(new JointPositionController()).execute();
	}

	@DeviceInterfaceTestMethod
	public void testSwitchToJointImpedanceControllerExecutesWithoutError(JointControllerInterface jci)
			throws RoboticsException {
		jci.switchJointController(
				new JointImpedanceController(((MultiJointDevice) jci.getDevice()).getJointCount(), 1, 1, 0)).execute();
	}

	@DeviceInterfaceTestMethod
	public void testSwitchJointControllersMultipleTimesExecutesWithoutError(JointControllerInterface jci)
			throws RoboticsException {
		testSwitchToJointImpedanceControllerExecutesWithoutError(jci);
		testSwitchToJointPositionControllerExecutesWithoutError(jci);
		testSwitchToJointImpedanceControllerExecutesWithoutError(jci);
		testSwitchToJointPositionControllerExecutesWithoutError(jci);
	}

}
