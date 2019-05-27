/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.Arrays;
import java.util.Collection;

import org.roboticsapi.core.Predicate;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.TemporaryRelation;

/**
 * This class consists exclusively of static methods that return
 * {@link Predicate}s for entities.
 */
public class EntityPredicates {

	/**
	 * Private constructor
	 */
	private EntityPredicates() {

	}

	public static <T> Predicate<T> not(final Predicate<T> predicate) {
		return new Predicate<T>() {

			@Override
			public boolean appliesTo(T e) {
				return !predicate.appliesTo(e);
			}
		};
	}

	public static <T> Predicate<T> and(final Predicate<T>... predicates) {
		return new Predicate<T>() {

			@Override
			public boolean appliesTo(T e) {
				for (Predicate<T> f : predicates) {
					if (f != null && !f.appliesTo(e)) {
						return false;
					}
				}
				return true;
			}
		};
	}

	public static <T> Predicate<T> or(final Predicate<T>... predicates) {
		return new Predicate<T>() {

			@Override
			public boolean appliesTo(T e) {
				for (Predicate<T> f : predicates) {
					if (f != null && f.appliesTo(e)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	public static <T extends Entity> Predicate<Entity> contains(final T... elements) {
		return contains(Arrays.asList(elements));
	}

	/**
	 * Returns a new {@link Predicate} that tests if an element is contained in the
	 * given collection.
	 * 
	 * @param elements a collection with elements to test with
	 * @return the predicate
	 */
	public static <T extends Entity> Predicate<Entity> contains(final Collection<T> elements) {
		return new Predicate<Entity>() {

			@Override
			public boolean appliesTo(Entity d) {
				return elements.contains(d);
			}
		};
	}

	/**
	 * Returns a new {@link Predicate} that tests if an element is an instance of
	 * the given class.
	 * 
	 * @param type the class to test with
	 * @return the predicate
	 */
	public static <T> Predicate<Entity> isInstance(final Class<T> type) {
		return new Predicate<Entity>() {

			@Override
			public boolean appliesTo(Entity e) {
				return type.isInstance(e);
			}
		};
	}

	public static <T extends ComposedEntity> Predicate<Entity> belongsTo(final Class<T> type) {
		return new Predicate<Entity>() {

			@Override
			public boolean appliesTo(Entity e) {
				while (e != null) {
					e = e.getParent();

					if (type.isInstance(e)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	/**
	 * Returns a new {@link Predicate} that tests if an entity's parent is an
	 * instance of the given class. If the given type is <code>null</code>, the
	 * predicate evaluates to <code>true</code> if and only if the entity's parent
	 * is <code>null</code>.
	 * 
	 * @param type the class to test with
	 * @return the predicate
	 */
	public static <T extends Entity> Predicate<Entity> parentIsIstance(final Class<T> type) {
		return new Predicate<Entity>() {

			@Override
			public boolean appliesTo(Entity e) {
				ComposedEntity parent = e.getParent();

				if (parent == null) {
					if (type == null) {
						return true;
					}
					return false;
				}
				if (type != null) {
					return type.isInstance(parent);
				}
				return false;
			}
		};
	}

	public static <T extends Entity> Predicate<Entity> filterParent(final T... forbidden) {
		return filterParent(Arrays.asList(forbidden));
	}

	public static <T extends Entity> Predicate<Entity> filterParent(final Collection<T> forbiddenParent) {
		return new Predicate<Entity>() {

			@Override
			public boolean appliesTo(Entity e) {
				final ComposedEntity parent = e.getParent();
				return forbiddenParent.contains(parent);
			}
		};
	}

	public static Predicate<Entity> isConnection() {
		return isInstance(Connection.class);
	}

	public static Predicate<Entity> isDynamicConnection() {
		return isInstance(DynamicConnection.class);
	}

	public static Predicate<Entity> isPlacement() {
		return isInstance(Placement.class);
	}

	public static Predicate<Entity> isTemporaryRelation() {
		return isInstance(TemporaryRelation.class);
	}

}
