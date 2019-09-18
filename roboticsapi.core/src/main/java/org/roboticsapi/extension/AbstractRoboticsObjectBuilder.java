/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.RoboticsObject;

/**
 * A robotics builder which uses canonical names of classes as key. Furthermore,
 * new {@link RoboticsObject} classes are instantiated automatically by this
 * robotics builder.
 */
public abstract class AbstractRoboticsObjectBuilder implements RoboticsObjectBuilder {

	private final Map<String, Class<? extends RoboticsObject>> map;

	/**
	 * Creates a new robotics builder by telling all supported classes. All classes
	 * must implement the {@link RoboticsObject} interface.
	 */
	public AbstractRoboticsObjectBuilder(Class<?>... types) {
		map = new HashMap<String, Class<? extends RoboticsObject>>();

		if (types != null) {
			for (Class<?> type : types) {
				Class<? extends RoboticsObject> roType = checkType(type);
				map.put(type.getCanonicalName(), roType);
			}
		}
	}

	/**
	 * Method that inspects the given {@link Class} if it is a
	 * {@link RoboticsObject}.
	 *
	 * @param type the class to inspect
	 *
	 * @throws TypeNotSupportedException if the inspection fails
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends RoboticsObject> checkType(Class<?> type) {
		if (type.getCanonicalName() == null) {
			throw new TypeNotSupportedException(type,
					"A canonical name is not provided. This might be because it is a local or an anonymous class, for example.");
		}
		if (!RoboticsObject.class.isAssignableFrom(type)) {
			throw new TypeNotSupportedException(type, "Given class is not a robotics object.");
		}
		return (Class<? extends RoboticsObject>) type;
	}

	@Override
	public final RoboticsObject build(String name) {
		if (map.containsKey(name)) {
			return build(map.get(name));
		}
		throw new NoBuilderFoundException(name);
	}

	/**
	 * Creates a new instance of the specified {@link RoboticsObject} class.
	 *
	 * @param type the type of the instance to be created
	 * @return a new instance of the specified class
	 * @throws InstantiationFailedException if the {@link RoboticsObject} cannot be
	 *                                      instantiated.
	 */
	public final RoboticsObject build(Class<? extends RoboticsObject> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new InstantiationFailedException(type,
					"Given class is abstract or an interface; or it doesn't provide a nullary constructor.");
		} catch (IllegalAccessException e) {
			throw new InstantiationFailedException(type, "Constructor is not accessible.");
		}
	}

	@Override
	public final String[] getProvidedTypes() {
		Set<String> set = map.keySet();
		return set.toArray(new String[set.size()]);
	}

	@Override
	public final boolean canBuild(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}

		for (String s : getProvidedTypes()) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if a robotics object of the given type can be build by this
	 * robotics builder.
	 *
	 * @param type the type
	 * @return true if a robotics object can be build
	 */
	public final boolean canBuild(Class<? extends RoboticsObject> type) {
		if (type == null || type.getCanonicalName() == null) {
			return false;
		}
		return canBuild(type.getCanonicalName());
	}

	public final class NoBuilderFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private NoBuilderFoundException(String name) {
			super("Type '" + name + "' is not supported to be provided by a robotics builder");
		}
	}

	public final class TypeNotSupportedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private TypeNotSupportedException(Class<?> clazz, String reason) {
			super("Type '" + (clazz.getCanonicalName() != null ? clazz.getCanonicalName() : clazz.getName())
					+ "' is not supported to be provided by a robotics builder"
					+ (reason != null ? ". Reason: " + reason : ""));
		}
	}

	public final class InstantiationFailedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private InstantiationFailedException(Class<?> clazz, String reason) {
			super("Instantiation of class '"
					+ (clazz.getCanonicalName() != null ? clazz.getCanonicalName() : clazz.getName()) + "' failed"
					+ (reason != null ? ". Reason: " + reason : ""));
		}
	}

}
