/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.extension.RoboticsObjectListener;

public class RoboticsObjectListenerHandler implements ExtensionHandler<RoboticsObjectListener> {

	private final List<RoboticsObjectListener> registeredListeners = new ArrayList<RoboticsObjectListener>();
	private final List<RoboticsObject> registeredObjects = new ArrayList<RoboticsObject>();

	protected RoboticsObjectListenerHandler() {
	}

	@Override
	public Class<RoboticsObjectListener> getType() {
		return RoboticsObjectListener.class;
	}

	protected synchronized void onAvailable(final RoboticsObject object) {
		registeredObjects.add(object);
		for (final RoboticsObjectListener l : registeredListeners) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					l.onAvailable(object);
				}
			});
		}
	}

	protected synchronized void onUnavailable(final RoboticsObject object) {
		registeredObjects.remove(object);
		for (final RoboticsObjectListener l : registeredListeners) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					l.onUnavailable(object);
				}
			});
		}
	}

	@Override
	public synchronized void addExtension(final RoboticsObjectListener l) {
		registeredListeners.add(l);
		for (final RoboticsObject ro : registeredObjects) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					l.onAvailable(ro);
				}
			});
		}
	}

	@Override
	public synchronized void removeExtension(final RoboticsObjectListener l) {
		registeredListeners.remove(l);
		for (final RoboticsObject ro : registeredObjects) {
			safelyExecute(new Runnable() {
				@Override
				public void run() {
					l.onUnavailable(ro);
				}
			});
		}
	}

	private void safelyExecute(Runnable r) {
		try {
			r.run();
		} catch (Throwable e) {
			RAPILogger.getLogger(this).log(Level.SEVERE, "Error while notifying robotics object listener", e);
		}
	}

}
