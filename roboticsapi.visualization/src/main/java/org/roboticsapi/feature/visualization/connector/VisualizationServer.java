/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector;

public interface VisualizationServer {

	public static final int DEFAULT_TIMEOUT = 500;

	public String getType();

	public int getPort();

	public String getRapiInstanceName();

	public void shutdown();

}
