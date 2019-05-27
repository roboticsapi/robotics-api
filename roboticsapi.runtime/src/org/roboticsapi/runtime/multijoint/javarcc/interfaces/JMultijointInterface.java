/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.javarcc.interfaces;

import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.world.types.RPIVector;

public interface JMultijointInterface extends JDevice {

	int getJointCount();

	int getJointError(int axis);

	double getMeasuredJointAcceleration(int axis);

	double getMeasuredJointVelocity(int axis);

	double getMeasuredJointPosition(int axis);

	double getCommandedJointAcceleration(int axis);

	double getCommandedJointVelocity(int axis);

	double getCommandedJointPosition(int axis);

	int checkJointPosition(int axis, double pos);

	void setJointPosition(int axis, double pos, Long time);

	void setToolCOM(RPIVector com, int axis);

	void setToolMOI(RPIVector moi, int axis);

	void setToolMass(double massVal, int axis);

	int getToolError(int axis);

	boolean getToolFinished(int axis);

}
