/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;

public interface RoboticsContext {

	public String getName();

	public <T extends RoboticsObject> List<T> getAll(Class<T> type);

	public void initialize(RoboticsObject object) throws InitializationException;

	public void uninitialize(RoboticsObject object) throws InitializationException;

	public boolean isInitialized(RoboticsObject object);

	public boolean hasExtension(Extension extension);

	public <U> void addExtension(Extension extension);

	public <T extends Extension> Set<T> getExtensions(Class<T> clazz);

	public void removeExtension(Extension extension);

	public <T extends Extension> void addExtensionHandler(ExtensionHandler<T> extensionHandler);

	public <T extends Extension> void removeExtensionHandler(ExtensionHandler<T> extensionHandler);

	public Logger getLogger();

	public void clearRoboticsObjects();

	public void destroy();

}
