/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.launcher.internal;

import java.util.HashMap;

import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameListener;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.extension.RoboticsObjectListener;

public class FrameListenerConsumer implements ExtensionHandler<FrameListener> {

	private final RoboticsContext context;
	private final HashMap<FrameListener, FrameListenerAdapter> adapters = new HashMap<FrameListener, FrameListenerConsumer.FrameListenerAdapter>();

	public FrameListenerConsumer(RoboticsContext context) {
		this.context = context;
	}

	@Override
	public void addExtension(FrameListener e) {
		FrameListenerAdapter adapter = new FrameListenerAdapter(e);
		adapters.put(e, adapter);
		context.addExtension(adapter);
	}

	@Override
	public void removeExtension(FrameListener e) {
		if (adapters.containsKey(e)) {
			context.removeExtension(adapters.get(e));
		}
	}

	private static class FrameListenerAdapter implements RoboticsObjectListener {

		FrameListener listener;

		public FrameListenerAdapter(FrameListener listener) {
			super();
			this.listener = listener;
		}

		@Override
		public void onAvailable(RoboticsObject object) {
			if (object instanceof Frame) {
				listener.onFrameAvailable((Frame) object);
			}
		}

		@Override
		public void onUnavailable(RoboticsObject object) {
			if (object instanceof Frame) {
				listener.onFrameUnavailable((Frame) object);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FrameListenerAdapter) {
				FrameListenerAdapter adapter = (FrameListenerAdapter) obj;

				if (listener == null) {
					return adapter.listener == null;
				}
				return listener.equals(adapter.listener);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return HashCodeUtil.hash(HashCodeUtil.SEED, listener);
		}

	}

	@Override
	public Class<FrameListener> getType() {
		return FrameListener.class;
	}

}
