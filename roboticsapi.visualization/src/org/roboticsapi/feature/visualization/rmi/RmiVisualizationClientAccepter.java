/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiVisualizationClientAccepter extends Remote {

	public static final String RMI_NAME = "org.roboticsapi.feature.visualization.rmi";
	public static final String RMI_SERVER_TYPE = "rmi";

	public void registerRemoteVisualizationClient(RmiVisualizationClient factory) throws RemoteException;

}
