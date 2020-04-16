/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

import java.rmi.Remote;
import java.util.Map;

/**
 * A remotely available and editable 3D scene.
 */
public interface VisualizationClientScene extends Remote {

	/**
	 * Returns the scene's name.
	 * 
	 * @return the scene's name
	 * 
	 * @throws Exception if an exception occurs.
	 */
	String getName() throws Exception;

	/**
	 * Remotely loads a new model node into the scene.
	 * 
	 * @param name      the node's name.
	 * @param frame     the parent node's id.
	 * @param modelName asset name of the model which will be looked up on server
	 *                  side
	 * @return the node's id.
	 */
	int addModel(String name, int frame, String modelName, double x, double y, double z, double a, double b, double c)
			throws Exception;

	int addBox(String name, int frame, double sizeX, double sizeY, double sizeZ, double x, double y, double z, double a,
			double b, double c) throws Exception;

	int addSphere(String name, int frame, double radius, double x, double y, double z, double a, double b, double c)
			throws Exception;

	int addCylinder(String name, int frame, double radius, double height, double x, double y, double z, double a,
			double b, double c) throws Exception;

	int addCapsule(String name, int frame, double radius, double height, double x, double y, double z, double a,
			double b, double c) throws Exception;

	/**
	 * Checks if a model exists on the server
	 * 
	 * @param modelName asset name of the model to check
	 * @return true if the model exists
	 */
	boolean hasModel(String modelName) throws Exception;

	/**
	 * Uploads a Collada model to the server
	 * 
	 * @param modelName asset name of the model
	 * @param modelData content of the DAE file
	 * @param auxFiles  additional files required for DAE file
	 */
	void uploadModel(String modelName, byte[] modelData, Map<String, byte[]> auxFiles) throws Exception;

	/**
	 * Retrieves the root frame node
	 * 
	 * @return the frame node id
	 * @throws Exception if an exception occurs.
	 */
	int getRootFrame() throws Exception;

	/**
	 * Remotely adds a new frame node
	 * 
	 * @param name the frame's name.
	 * @return the frame node id.
	 * @throws Exception if an exception occurs.
	 */
	int addFrame(String name) throws Exception;

	/**
	 * Removes the specified frame.
	 * 
	 * @param nodeID the id of the frame to delete
	 * @throws Exception if an exception occurs.
	 */
	void removeFrame(int nodeID) throws Exception;

	/**
	 * Adds a relation between two frames
	 * 
	 * @param from parent frame
	 * @param to   child frame
	 * @param x    the translation in X
	 * @param y    the translation in Y
	 * @param z    the translation in Z
	 * @param a    the rotation around Z
	 * @param b    the rotation around Y
	 * @param c    the rotation around X
	 * 
	 * @throws Exception if an exception occurs.
	 */
	void addRelation(int from, int to, double x, double y, double z, double a, double b, double c) throws Exception;

	/**
	 * Removes a relation between two frames
	 * 
	 * @param from parent frame
	 * @param to   child frame
	 * @throws Exception if an exception occurs.
	 */
	void removeRelation(int from, int to) throws Exception;

	/**
	 * Returns whether this scene is valid or not.
	 * 
	 * @return <code>true</code> if the scene is valid; <code>false</code>
	 *         otherwise.
	 * @throws Exception if an exception occurs.
	 */
	boolean isValid() throws Exception;

	/**
	 * Highlights this scene (and all scenes belonging to the same scene group).
	 * Hence, all other existing scenes are made invisible.
	 */
	void highlight() throws Exception;

	/**
	 * Updates the transformation of the specified node (relative to its parent
	 * node).
	 * 
	 * @param from the id of the parent frame
	 * @param to   the id of the child frame
	 * @param x    the translation in X
	 * @param y    the translation in Y
	 * @param z    the translation in Z
	 * @param a    the rotation around Z
	 * @param b    the rotation around Y
	 * @param c    the rotation around X
	 * @throws Exception if an exception occurs.
	 */
	void updateTransformation(int from, int to, double x, double y, double z, double a, double b, double c)
			throws Exception;

	public double getTransparency(int id) throws Exception;

	public double[] getColor(int id) throws Exception;

	public void setTransparency(int id, double value) throws Exception;

	public void setColor(int id, double r, double g, double b) throws Exception;

	public void setScale(int id, double sx, double sy, double sz) throws Exception;

}
