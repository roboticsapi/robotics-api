/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.simulation;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.roboticsapi.core.util.RAPILogger;

public class SimulationHelper {

	private static Map<String, AbstractSimulationItem> simulationItems = new HashMap<String, AbstractSimulationItem>();
	private static List<SimulationItem> diedSimulationItems = new ArrayList<>();

	public interface Supplier<T extends SimulationItem> {
		T get() throws RemoteException;
	}

	public interface SimulationCall {
		public void run() throws RemoteException;
	}

	public interface SimulationFunction<T> {
		public T run() throws RemoteException;
	}

	public static void callSimulationItem(SimulationItem item, SimulationCall task) {
		try {
			if (item != null && !diedSimulationItems.contains(item))
				task.run();
		} catch (RemoteException e) {
			diedSimulationItems.add(item);
			RAPILogger.getLogger(SimulationHelper.class).log(Level.WARNING,
					"Failed to communicate with simulation item", e);
		}
	}

	public static <T> T callSimulationItem(SimulationItem item, SimulationFunction<T> task, T defaultValue) {
		try {
			if (item != null && !diedSimulationItems.contains(item))
				return task.run();
		} catch (RemoteException e) {
			diedSimulationItems.add(item);
			RAPILogger.getLogger(SimulationHelper.class).log(Level.WARNING,
					"Failed to communicate with simulation item", e);
		}
		return defaultValue;
	}

	public static <T extends SimulationItem> T getSimulationItem(Class<T> type, String identifier) {
		Object ret = null;
		if (identifier != null && identifier.startsWith("rmi://")) {
			try {
				URI url = new URI(identifier);
				ret = LocateRegistry.getRegistry(url.getHost(), url.getPort()).lookup(url.getPath());
			} catch (Exception e) {
				RAPILogger.getLogger(SimulationHelper.class).warning("Simulation item " + identifier + " of type "
						+ type.getName() + " is not available: " + e.getMessage());
			}
		} else {
			ret = simulationItems.get(identifier);
		}

		if (ret == null) {
			RAPILogger.getLogger(SimulationHelper.class).warning("Simulation item " + identifier + " of type "
					+ type.getName() + " is not available: Not registered");
		} else if (!type.isAssignableFrom(ret.getClass())) {
			RAPILogger.getLogger(SimulationHelper.class).warning("Simulation item " + identifier + " of type "
					+ type.getName() + " is not available: Incorrect type");
		} else {
			return type.cast(ret);
		}
		return null;
	}

	public static <T extends AbstractSimulationItem> boolean registerSimuationItem(String identifier,
			Supplier<T> supplier) {
		if (identifier == null)
			return false;

		try {
			T object = supplier.get();
			simulationItems.put(identifier, object);

			if (identifier.startsWith("rmi://")) {
				try {
					if (object instanceof Remote) {
						URI url = new URI(identifier);
						try {
							Registry reg = LocateRegistry.getRegistry(url.getHost(), url.getPort());
							reg.bind(url.getPath(), object);
						} catch (RemoteException e) {
							Registry reg = LocateRegistry.createRegistry(url.getPort());
							reg.bind(url.getPath(), object);
						}
					} else {
						RAPILogger.getLogger(SimulationHelper.class).warning("Simulation item " + identifier
								+ " could not be registered: Not a valid remote object.");
						return false;
					}
				} catch (Exception e) {
					RAPILogger.getLogger(SimulationHelper.class)
							.warning("Simulation item " + identifier + " could not be registered: " + e.getMessage());
					return false;
				}
			}

			return true;
		} catch (RemoteException e) {
			RAPILogger.getLogger(SimulationHelper.class)
					.warning("Simulation item " + identifier + " could not be registered: " + e.getMessage());
		}
		return false;
	}

	public static void unregisterSimulationItem(String identifier) {
		if (identifier == null)
			return;

		try {
			AbstractSimulationItem item = simulationItems.remove(identifier);
			if (item != null)
				item.destroy();
			if (identifier.startsWith("rmi://")) {
				URI url = new URI(identifier);
				Registry registry = LocateRegistry.getRegistry(url.getHost(), url.getPort());
				registry.unbind(url.getPath());
			}
		} catch (RemoteException | URISyntaxException | NotBoundException e) {
			RAPILogger.logException(SimulationHelper.class, e);
		}
	}

}
