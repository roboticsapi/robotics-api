/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiVisualizationClientAccepterImpl extends UnicastRemoteObject
		implements RmiVisualizationClientAccepter, Serializable {

	private static final long serialVersionUID = 1L;
	private OnFound onfound;

	public RmiVisualizationClientAccepterImpl(OnFound onfound) throws RemoteException {
		this.onfound = onfound;
	}

	@Override
	public void registerRemoteVisualizationClient(RmiVisualizationClient factory) throws RemoteException {
		onfound.onRemoteVisualizationClientRegistered(factory);
	}

	public interface OnFound {
		public void onRemoteVisualizationClientRegistered(RmiVisualizationClient factory) throws RemoteException;
	}
}
