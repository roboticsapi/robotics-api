/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.startup.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.feature.startup.configuration.util.MultipleIllegalConfigurationsException;

public final class DefaultRapi extends Rapi {

	private DefaultRapi(String name) {
		super(name);
	}

	private void loadExtensionsOverJspi() {
		List<Extension> extensions = new ArrayList<>();
		List<ExtensionHandler<?>> extensionHandlers = new ArrayList<>();
		for (Extension extension : ServiceLoader.load(Extension.class)) {
			extensions.add(extension);
			if (extension instanceof ExtensionHandler) {
				extensionHandlers.add((ExtensionHandler<?>) extension);
			}
		}
		extensionHandlers.forEach(h -> registerExtensionHandler(h));
		extensions.forEach(e -> registerExtension(e));
	}

	@Override
	protected void beforeDestroy() {
	}

	public static DefaultRapi createNewEmpty() {
		return createNewEmpty(null);
	}

	public static DefaultRapi createNewEmpty(String name) {
		DefaultRapi rapi = new DefaultRapi(name);
		try {
			rapi.loadExtensionsOverJspi();
		} catch (RuntimeException e) {
			rapi.destroy();
			throw e;
		}
		return rapi;
	}

	public static DefaultRapi createWithConfigFile(String configFile) throws MultipleIllegalConfigurationsException {
		return createWithConfigFile(null, configFile);
	}

	public static DefaultRapi createWithConfigFile(String name, String configFile)
			throws MultipleIllegalConfigurationsException {
		return createWithConfigFile(name, new File(configFile));
	}

	public static DefaultRapi createWithConfigFile(File configFile) throws MultipleIllegalConfigurationsException {
		return createWithConfigFile(null, configFile);
	}

	public static DefaultRapi createWithConfigFile(String name, File configFile)
			throws MultipleIllegalConfigurationsException {
		DefaultRapi rapi = createNewEmpty(name);
		try {
			rapi.loadConfigFile(configFile);
		} catch (RuntimeException e) {
			rapi.destroy();
			throw e;
		} catch (MultipleIllegalConfigurationsException e) {
			rapi.destroy();
			throw e;
		}
		return rapi;
	}

}
