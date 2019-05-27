/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.roboticsapi.feature.visualization.VisualizationClient;
import org.roboticsapi.feature.visualization.VisualizationClientScene;

public interface RmiVisualizationClient extends VisualizationClient, Remote {

	@Override
	default public RmiVisualizationClientScene createScene() throws RemoteException {
		return createScene("");
	}

	@Override
	default public RmiVisualizationClientScene createScene(String name) throws RemoteException {
		return createScene(name, true);
	}

	@Override
	public RmiVisualizationClientScene createScene(String name, boolean allowSelection) throws RemoteException;

	@Override
	public boolean deleteScene(VisualizationClientScene scene) throws RemoteException;

}
