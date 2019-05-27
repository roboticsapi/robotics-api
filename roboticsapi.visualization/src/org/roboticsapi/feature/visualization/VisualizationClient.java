/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

/**
 * Factory for remotely creating a 3D scene using Java's RMI.
 */
public interface VisualizationClient {

	/**
	 * Creates a remote scene without name.
	 * 
	 * @return the created {@link VisualizationClientScene}.
	 * @throws Exception if an exception occurs.
	 */
	default VisualizationClientScene createScene() throws Exception {
		return createScene("");
	}

	/**
	 * Creates a remote scene.
	 * 
	 * @param name the scene's name.
	 * @return the created {@link VisualizationClientScene}.
	 * @throws Exception if an exception occurs.
	 */
	default VisualizationClientScene createScene(String name) throws Exception {
		return createScene(name, true);
	}

	/**
	 * Creates a remote scene.
	 * 
	 * @param name           the scene's name.
	 * @param allowSelection boolean flag indicating whether a selection of 3D
	 *                       objects is possible
	 * 
	 * @return the created {@link VisualizationClientScene}.
	 * @throws Exception if an exception occurs.
	 */
	VisualizationClientScene createScene(String name, boolean allowSelection) throws Exception;

	/**
	 * Deletes a remote scene.
	 * 
	 * @param scene the {@link VisualizationClientScene}.
	 * @return <code>true</code> if the scene was deleted, <code>false</code>
	 *         otherwise.
	 * @throws Exception if an exception occurs.
	 */
	boolean deleteScene(VisualizationClientScene scene) throws Exception;

}
