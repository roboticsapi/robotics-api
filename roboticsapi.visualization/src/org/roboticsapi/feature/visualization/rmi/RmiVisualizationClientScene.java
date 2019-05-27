/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.roboticsapi.feature.visualization.VisualizationClientScene;

public interface RmiVisualizationClientScene extends VisualizationClientScene, Remote {

	@Override
	String getName() throws RemoteException;

	@Override
	boolean hasModel(String modelName) throws RemoteException;

	@Override
	void uploadModel(String modelName, byte[] modelData, Map<String, byte[]> auxFiles) throws RemoteException;

	@Override
	int addModel(String name, int frame, String modelName, double x, double y, double z, double a, double b, double c)
			throws RemoteException;

	@Override
	int addBox(String name, int parentID, double sizeX, double sizeY, double sizeZ, double x, double y, double z,
			double a, double b, double c) throws RemoteException;

	@Override
	int addSphere(String name, int parentID, double radius, double x, double y, double z, double a, double b, double c)
			throws RemoteException;

	@Override
	int addCylinder(String name, int parentID, double radius, double height, double x, double y, double z, double a,
			double b, double c) throws RemoteException;

	@Override
	int addCapsule(String name, int parentID, double radius, double height, double x, double y, double z, double a,
			double b, double c) throws RemoteException;

	@Override
	int addFrame(String name) throws RemoteException;

	@Override
	void removeFrame(int nodeID) throws RemoteException;

	@Override
	void addRelation(int from, int to, double x, double y, double z, double a, double b, double c)
			throws RemoteException;

	@Override
	void removeRelation(int from, int to) throws RemoteException;

	@Override
	void updateTransformation(int from, int to, double x, double y, double z, double a, double b, double c)
			throws RemoteException;

	@Override
	boolean isValid() throws RemoteException;

	@Override
	void highlight() throws RemoteException;

	@Override
	double getTransparency(int id) throws RemoteException;

	@Override
	double[] getColor(int id) throws RemoteException;

	@Override
	void setTransparency(int id, double value) throws RemoteException;

	@Override
	void setColor(int id, double r, double g, double b) throws RemoteException;

	@Override
	void setScale(int id, double sx, double sy, double sz) throws RemoteException;

}
