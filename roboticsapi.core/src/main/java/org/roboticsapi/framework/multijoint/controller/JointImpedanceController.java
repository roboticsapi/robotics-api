/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.controller;

/**
 * Joint Impedance Controller for multi-joint devices
 */
public class JointImpedanceController implements JointController {

	JointImpedanceSettings[] impedanceSettings;

	/**
	 * Constructor for Joint Impedance Controller which will set all joints to equal
	 * parameters
	 *
	 * @param jointCount joints to be controlled
	 * @param stiffness  stiffness in Nm/rad to be set to every joint
	 * @param damping    damping in 0..1 range to be set to every joint
	 * @param addTorque  additional torque in Nm to be set to every joint
	 */
	public JointImpedanceController(int jointCount, double stiffness, double damping, double addTorque) {
		this(jointCount, stiffness, damping, addTorque, 0);
	}

	/**
	 * Constructor for Joint Impedance Controller which will set all joints to equal
	 * parameters
	 *
	 * @param jointCount joints to be controlled
	 * @param stiffness  stiffness in Nm/rad to be set to every joint
	 * @param damping    damping in 0..1 range to be set to every joint
	 * @param addTorque  additional torque in Nm to be set to every joint
	 */
	public JointImpedanceController(int jointCount, double stiffness, double damping, double addTorque,
			double maxTorque) {
		impedanceSettings = new JointImpedanceSettings[jointCount];
		JointImpedanceSettings setting = new JointImpedanceSettings(stiffness, damping, addTorque, maxTorque);
		for (int i = 0; i < impedanceSettings.length; i++) {
			impedanceSettings[i] = setting;
		}
	}

	/**
	 * Constructor for Joint Impedance Controller which sets up different joints for
	 * each given parameter setting
	 *
	 * @param impedanceSettings Parameter setting with stiffness, damping and
	 *                          additional torque
	 */
	public JointImpedanceController(JointImpedanceSettings... impedanceSettings) {
		this.impedanceSettings = new JointImpedanceSettings[impedanceSettings.length];
		for (int i = 0; i < impedanceSettings.length; i++) {
			this.impedanceSettings[i] = impedanceSettings[i];
		}
	}

	/**
	 * Get Number of Joints
	 *
	 * @return joint count
	 */
	public int getJointCount() {
		return impedanceSettings.length;
	}

	/**
	 * Get impedance Setting for given joint number
	 *
	 * @param joint joint index
	 * @return impedance setting for given joint number
	 */
	public JointImpedanceSettings getImpedanceSettings(int joint) {
		return impedanceSettings[joint];
	}

}
