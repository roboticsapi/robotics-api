/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector.extension;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.World;
import org.roboticsapi.extension.ActivatableExtension;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.feature.visualization.connector.LookupServer;
import org.roboticsapi.feature.visualization.connector.RoboticsObjectVisualizationServer;

public abstract class VisualizationLookupServerExtension implements ActivatableExtension, RoboticsObjectListener {

	private final static int lookupPort = 4320;
	private LookupServer lookupServer = null;
	private World world = null;
	private RoboticsObjectVisualizationServer server = null;
	private List<RoboticsObject> objects = new ArrayList<>();

	public abstract RoboticsObjectVisualizationServer createServer(World world) throws Exception;

	@Override
	public void activate() {
		lookupServer = new LookupServer(lookupPort);
	}

	@Override
	public void deactivate() {
		lookupServer.close();
		lookupServer = null;
	}

	@Override
	public final void onAvailable(RoboticsObject object) {
		objects.add(object);
		if (object instanceof World) {
			world = (World) object;
			try {
				server = createServer(world);
				lookupServer.registerServer(server);
				for (RoboticsObject other : objects)
					server.addObject(other);
			} catch (Exception e) {
				RAPILogger.logException(getClass(), e);
			}
		} else if (server != null) {
			server.addObject(object);
		}
	}

	@Override
	public final void onUnavailable(RoboticsObject object) {
		if (object instanceof World) {
			if (server != null) {
				for (RoboticsObject other : objects)
					server.removeObject(other);
				lookupServer.unregisterServer(server);
				server.shutdown();
				server = null;
			}
		} else if (server != null) {
			server.removeObject(object);
		}
		objects.remove(object);
	}

}
