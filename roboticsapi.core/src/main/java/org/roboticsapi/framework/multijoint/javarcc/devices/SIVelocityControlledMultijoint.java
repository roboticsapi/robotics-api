/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.devices;

import java.rmi.RemoteException;

import org.roboticsapi.facet.javarcc.simulation.SimulationItem;

public interface SIVelocityControlledMultijoint extends SimulationItem {

	double getMeasuredJointPosition(int joint) throws RemoteException;

	double getMeasuredJointVelocity(int joint) throws RemoteException;

	void setJointVelocity(int joint, double velocity) throws RemoteException;

}
