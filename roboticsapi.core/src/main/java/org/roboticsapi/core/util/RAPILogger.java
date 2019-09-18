/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RAPILogger {
	static class FilterRecord {
		public String namespace;
		public Level level;
	}

	private static Level defaultLoggingLevel = null;
	private static List<FilterRecord> records = new ArrayList<FilterRecord>();
	private static boolean useNamespaceLogging = false;

	/**
	 * Sets the logging level of Java's default ConsoleHandler. Messages with the
	 * given level or higher will be printed to the console (if the loggers log
	 * them).
	 *
	 * @param level the level required for a message to be printed to console
	 */
	public static void setConsoleOutputLevel(Level level) {
		Handler[] handlers = getLogger("").getHandlers();

		for (Handler h : handlers) {
			if (h instanceof ConsoleHandler) {
				h.setLevel(level);
			}
		}
	}

	/**
	 * Sets the default logging level for all loggers created by {@link RAPILogger}.
	 *
	 * @param level the default level of a newly created logger
	 */
	public static void setDefaultLoggingLevel(Level level) {
		defaultLoggingLevel = level;
	}

	/**
	 * Configures {@link RAPILogger} to create loggers that log messages with
	 * varying detail for different namespaces. See also description of
	 * {@link #setLoggingLevel(String, Level)}.
	 *
	 * This can greatly decrease logging performance!
	 *
	 * @param namespaceLogging whether namespace-based logging should be used
	 */
	public static void setUseNamespaceLogging(boolean namespaceLogging) {
		useNamespaceLogging = namespaceLogging;
	}

	/**
	 * Sets the logging level for all newly created loggers in a given namespace.
	 * Requires namespace filtering to be enabled (see
	 * {@link #setUseNamespaceLogging(boolean)}).
	 *
	 * @param namespace the namespace for which to set log level
	 * @param level     the minimum level of messages to be logged
	 */
	public static void setLoggingLevel(String namespace, Level level) {
		FilterRecord record = new FilterRecord();
		record.namespace = namespace;
		record.level = level;
		records.add(record);
	}

	private static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		if (defaultLoggingLevel != null) {
			logger.setLevel(defaultLoggingLevel);
		}

		if (useNamespaceLogging) {
			for (FilterRecord r : records) {
				if (name.contains(r.namespace)) {
					logger.setLevel(r.level);
				}
			}
			// logger.setFilter(filter);
		}
		return logger;
	}

	public static void logException(Object obj, Exception e) {
		getLogger(obj).log(Level.WARNING, e.getMessage(), e);
	}

	public static void logException(Class<?> clazz, Exception e) {
		getLogger(clazz).log(Level.WARNING, e.getMessage(), e);
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(Object obj) {
		return getLogger(obj.getClass());
	}

}
