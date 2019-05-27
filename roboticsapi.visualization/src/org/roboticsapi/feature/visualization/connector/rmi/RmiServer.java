/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector.rmi;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.feature.visualization.connector.RoboticsObjectVisualizationServer;
import org.roboticsapi.feature.visualization.connector.VisualizationGraph;
import org.roboticsapi.feature.visualization.rmi.RmiVisualizationClient;
import org.roboticsapi.feature.visualization.rmi.RmiVisualizationClientAccepter;
import org.roboticsapi.feature.visualization.rmi.RmiVisualizationClientAccepterImpl;
import org.roboticsapi.feature.visualization.rmi.RmiVisualizationClientScene;
import org.roboticsapi.world.World;

public final class RmiServer implements RoboticsObjectVisualizationServer {

	private int port = 2000;
	public boolean shutdown = false;
	private final World world;
	private final List<RoboticsObject> objects = new ArrayList<>();

	private final List<VisualizationGraph> visualizationGraphs = new LinkedList<>();
	private final Object visualizationGraphLock = new Object();

	private Registry registry;
	private RmiVisualizationClientAccepterImpl rmiAccepter;

	public RmiServer(World world) throws Exception {
		this.world = world;
		while (true) {
			try {
				registry = LocateRegistry.createRegistry(port);
				rmiAccepter = new RmiVisualizationClientAccepterImpl(this::onRemoteVisualizationAdded);
				registry.bind(RmiVisualizationClientAccepter.RMI_NAME, rmiAccepter);
				break;
			} catch (ExportException e) {
				port++; // TODO: Gültigen Portbereich eingrenzen?
			}
		}
	}

	@Override
	public String getRapiInstanceName() {
		return world.getName();
	}

	@Override
	public String getType() {
		return RmiVisualizationClientAccepterImpl.RMI_SERVER_TYPE;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void shutdown() {
		// RMI schließen
		try {
			registry.unbind(RmiVisualizationClientAccepter.RMI_NAME);
		} catch (RemoteException | NotBoundException e) {
			RAPILogger.getLogger().warning(getClass() + ": " + e);
		}
		try {
			UnicastRemoteObject.unexportObject(rmiAccepter, true);
		} catch (NoSuchObjectException e) {
			RAPILogger.getLogger().warning(getClass() + ": " + e);
		}

		// Thread beenden und aktuelle Verbindungsobjekte aufräumen
		shutdown = true;
		synchronized (visualizationGraphLock) {
			for (VisualizationGraph graph : visualizationGraphs) {
				graph.shutdown();
			}
		}
	}

	private void onRemoteVisualizationAdded(RmiVisualizationClient factory) throws RemoteException {
		synchronized (visualizationGraphLock) {
			RmiVisualizationClientScene scene = factory.createScene(world.getName());
			VisualizationGraph visualization = new VisualizationGraph(scene, world.getOrigin());
			for (RoboticsObject object : objects) {
				visualization.onAvailable(object);
			}
			visualizationGraphs.add(visualization);
		}
	}

	@Override
	public void addObject(RoboticsObject object) {
		synchronized (visualizationGraphLock) {
			objects.add(object);
		}
		for (VisualizationGraph graph : visualizationGraphs) {
			graph.onAvailable(object);
		}
	}

	@Override
	public void removeObject(RoboticsObject object) {
		for (VisualizationGraph graph : visualizationGraphs) {
			graph.onUnavailable(object);
		}
		synchronized (visualizationGraphLock) {
			objects.remove(object);
		}
	}

}
