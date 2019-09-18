/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.facet.runtime.rpi.ConfiguredRcc;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.feature.runtime.realtimercc.websocket.SoftRobotWebsocketControlCore;

public class RealtimeRcc extends AbstractRoboticsObject implements ConfiguredRcc {

	private final Dependency<SoftRobotWebsocketControlCore> controlCore;
	private final Dependency<String> uri;

	public RealtimeRcc() {
		controlCore = createDependency("controlCore", () -> {
			try {
				return new SoftRobotWebsocketControlCore(getUri());
			} catch (RpiException e) {
				throw new IllegalArgumentException(e);
			}
		});
		uri = createDependency("uri", "http://127.0.0.1:8080");
	}

	public RealtimeRcc(String uri) {
		this();
		setUri(uri);
	}

	@Override
	protected void beforeUninitialization() {
		controlCore.get().shutdown();
		super.beforeUninitialization();
	}

	public String getUri() {
		return uri.get();
	}

	@ConfigurationProperty
	public void setUri(String uri) {
		this.uri.set(uri);
	}

	@Override
	public ControlCore getControlCore() {
		return controlCore.get();
	}

}
