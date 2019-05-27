/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.cartesianmotion.activity.CartesianJoggingInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

/**
 * A {@link CartesianJoggingHandle} can be used to set velocities of an already
 * running cartesian jogging.
 */
public interface CartesianJoggingHandle {

	/**
	 * Sets the cartesian velocity of the jogged {@link Frame} to the given values.
	 * The values' directions are taken to be the axes of the guidance {@link Frame}
	 * (see {@link CartesianJoggingInterface}).
	 * 
	 * @param xspeed the translational speed along the x axis (in m/s)
	 * @param yspeed the translational speed along the y axis (in m/s)
	 * @param zspeed the translational speed along the z axis (in m/s)
	 * @param aspeed the rotational speed around the z axis (in rad/s)
	 * @param bspeed the rotational speed around the y axis (in rad/s)
	 * @param cspeed the rotational speed around the x axis (in rad/s)
	 * @throws JoggingException if an error occurred during jogging
	 */
	public void setCartesianVelocity(final double xspeed, final double yspeed, final double zspeed, final double aspeed,
			final double bspeed, final double cspeed) throws CartesianJoggingException;

	public void stopJogging() throws RoboticsException;
}
