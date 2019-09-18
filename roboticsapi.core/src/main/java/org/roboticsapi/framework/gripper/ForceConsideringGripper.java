/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.framework.gripper.parameters.ForceParameter;

/**
 * This interface represents a gripper with force skills.
 * <p>
 * This interface can be implemented by any gripper which can cope with force
 * data - in particular as device parameter for gripping activities (cf.
 * {@link ForceParameter}).
 *
 * @see ForceParameter
 */
public interface ForceConsideringGripper extends Gripper {

	/**
	 * Returns the gripper's min. permitted force in [N].
	 *
	 * @return the min. permitted force in [N].
	 */
	public double getMinimumForce();

	/**
	 * Returns the gripper's max. permitted force in [N].
	 *
	 * @return the max. permitted force in [N].
	 */
	public double getMaximumForce();

}
