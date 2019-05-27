/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.sensor.TransformationSensor;

/**
 * The connection described and controlled by a joint
 */
public abstract class JointConnection extends DynamicConnection {

	protected final JointDriver jointDriver;
	protected final Joint joint;

	protected JointConnection(Joint joint, JointDriver jointDriver) {
		super();
		this.joint = joint;
		this.jointDriver = jointDriver;
	}

	public Joint getJoint() {
		return joint;
	}

	/**
	 * Sensor computing the transformation for a given joint position
	 *
	 * @param position input position
	 * @return sensor for the transformation
	 */
	public abstract TransformationSensor getTransformationSensor(DoubleSensor position);

}