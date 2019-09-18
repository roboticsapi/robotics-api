/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.controller;

/**
 * Cartesian Impedance Setting offers stiffness, damping and additional torque
 */
public class CartesianImpedanceSettings {

	private final double stiffness;
	private final double damping;
	private final double addTorque;
	private final double maxTorque;

	/**
	 * Constructor for Impedance setting of one joint
	 *
	 * @param stiffness stiffness value in Nm/rad
	 * @param damping   damping value in 0..1 range
	 * @param addTorque additional torque in Nm
	 */
	public CartesianImpedanceSettings(double stiffness, double damping, double addTorque) {
		this(stiffness, damping, addTorque, 0d);

	}

	/**
	 * onstructor for Impedance setting of one joint
	 *
	 * @param stiffness stiffness value in Nm/rad
	 * @param damping   damping value in 0..1 range
	 * @param addTorque additional torque in Nm
	 * @param maxTorque maximum torque in Nm (<=0 means infinite)
	 */
	public CartesianImpedanceSettings(double stiffness, double damping, double addTorque, double maxTorque) {
		this.stiffness = stiffness;
		this.damping = damping;
		this.addTorque = addTorque;
		this.maxTorque = maxTorque;
	}

	/**
	 * Get Stiffness
	 *
	 * @return stiffness in Nm/rad
	 */
	public double getStiffness() {
		return stiffness;
	}

	/**
	 * Get Damping
	 *
	 * @return damping in 0..1 range
	 */
	public double getDamping() {
		return damping;
	}

	/**
	 * Get additional torque
	 *
	 * @return additional torque in Nm
	 */
	public double getAddTorque() {
		return addTorque;
	}

	/**
	 * Gets the maximum torque.
	 *
	 * @return the maximum torque in Nm
	 */
	public double getMaxTorque() {
		return maxTorque;
	}

}
