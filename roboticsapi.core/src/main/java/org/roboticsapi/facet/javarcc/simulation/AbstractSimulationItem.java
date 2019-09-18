/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.simulation;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.roboticsapi.core.util.RAPILogger;

public abstract class AbstractSimulationItem extends UnicastRemoteObject implements SimulationItem {
	private static final long serialVersionUID = -302346311740699602L;

	protected AbstractSimulationItem() throws RemoteException {
		super();
	}

	public void destroy() {
		try {
			unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			RAPILogger.logException(this, e);
		}
	}
}