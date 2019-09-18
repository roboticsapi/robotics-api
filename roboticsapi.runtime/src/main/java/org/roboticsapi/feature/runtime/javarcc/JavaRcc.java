/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc;

import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.ConfiguredRcc;

public class JavaRcc extends AbstractRoboticsObject implements ConfiguredRcc, JavaRccExtensionPoint {
	private final Dependency<JavaControlCore> controlCore;

	public JavaRcc() {
		controlCore = createDependency("controlCore", () -> new JavaControlCore(getName()));
	}

	@Override
	protected void beforeUninitialization() {
		controlCore.get().shutdown();
		super.beforeUninitialization();
	}

	@Override
	public JavaControlCore getControlCore() {
		return controlCore.get();
	}

	@Override
	public void registerPrimitive(String name, Class<? extends JPrimitive> primitive) {
		controlCore.get().registerPrimitive(name, primitive);
	}

	@Override
	public void registerDevice(String type, DeviceFactory factory) {
		controlCore.get().registerDevice(type, factory);
	}

	@Override
	public void registerInterface(String name, InterfaceExtractor extractor) {
		controlCore.get().registerInterface(name, extractor);
	}
}
