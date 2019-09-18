/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public interface RoboticsObjectView<T extends RoboticsObject> {

	public static class SpecializedRoboticsObjectView<T extends RoboticsObject, U extends T>
			implements RoboticsObjectView<U> {
		private final Class<U> type;
		private final RoboticsObjectView<T> innerView;

		private SpecializedRoboticsObjectView(Class<U> type, RoboticsObjectView<T> innerView) {
			this.type = type;
			this.innerView = innerView;
		}

		@Override
		public Set<U> getObjects() {
			return innerView.getObjects(type);
		}

		@Override
		public Class<U> getObjectType() {
			return type;
		}
	}

	public static class DerivedRoboticsObjectView<T extends RoboticsObject> implements RoboticsObjectView<T> {
		private final RoboticsObjectView<T> innerView;

		public DerivedRoboticsObjectView(RoboticsObjectView<T> innerView) {
			this.innerView = innerView;
		}

		@Override
		public Set<T> getObjects() {
			return innerView.getObjects();
		}

		@Override
		public Class<T> getObjectType() {
			return innerView.getObjectType();
		}
	}

	public Set<T> getObjects();

	public Class<T> getObjectType();

	public default <U extends T> Set<U> getObjects(Class<U> type) {
		Set<U> ret = new HashSet<U>();

		for (T object : getObjects()) {
			if (type.isAssignableFrom(object.getClass())) {
				ret.add(type.cast(object));
			}
		}
		return ret;
	}

	public default <U extends T> RoboticsObjectView<U> specializingInObjects(final Class<U> type) {
		return new SpecializedRoboticsObjectView<T, U>(type, this);
	}

	@SuppressWarnings("unchecked")
	public default RoboticsObjectView<T> hidingObjects(final T... objects) {
		return new DerivedRoboticsObjectView<T>(this) {
			@Override
			public Set<T> getObjects() {
				Set<T> ret = new HashSet<>(super.getObjects());
				for (T o : objects) {
					ret.remove(o);
				}
				return ret;
			}
		};
	}

	/**
	 * Hides all {@link RoboticsObject}s the given filter applies to.
	 *
	 * @param filter decides which {@link RoboticsObject}s to hide
	 * @return {@link RoboticsObjectView} without the hidden relations
	 */
	public default RoboticsObjectView<T> hidingObjects(final Predicate<T> filter) {
		return new DerivedRoboticsObjectView<T>(this) {
			@Override
			public Set<T> getObjects() {
				Set<T> ret = new HashSet<>(super.getObjects());
				for (Iterator<T> iterator = ret.iterator(); iterator.hasNext();) {
					T r = iterator.next();
					if (filter.appliesTo(r)) {
						iterator.remove();
					}
				}
				return ret;
			}
		};
	}

}
