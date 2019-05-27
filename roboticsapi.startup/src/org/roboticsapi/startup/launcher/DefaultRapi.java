/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.launcher;

import java.io.File;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.startup.configuration.util.MultipleIllegalConfigurationsException;

/**
 */
public final class DefaultRapi extends Rapi {

	private DefaultRapi(String name) {
		super(name);
	}

	private void loadExtensionsOverJspi() {
		@SuppressWarnings("rawtypes")
		Iterator<ExtensionHandler> consumers = ServiceLoader.load(ExtensionHandler.class).iterator();
		while (consumers.hasNext()) {
			registerExtensionHandler((ExtensionHandler<?>) consumers.next());
		}

		Iterator<Extension> extensions = ServiceLoader.load(Extension.class).iterator();
		while (extensions.hasNext()) {
			Extension extension = extensions.next();
			registerExtension(extension);
		}
	}

	@Override
	protected void beforeDestroy() {
	}

	public static DefaultRapi createNewEmpty() {
		return createNew(null);
	}

	public static DefaultRapi createNew(String name) {
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
		DefaultRapi rapi = createNew(name);
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
