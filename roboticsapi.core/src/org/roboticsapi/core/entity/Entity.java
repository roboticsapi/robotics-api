/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

import java.util.Set;

import org.roboticsapi.core.exception.EntityException;

/**
 * An <code>Entity</code> is an interface for defining some aspect of the real
 * world. An entity can be (uniquely) identified and is defined by a set of
 * properties. It can be either a physical object or an abstract concept.
 *
 * Entities can have an (hierarchical) containment relationship to other
 * entities.
 */
public interface Entity {

	/**
	 * Retrieves the entity's parent entity.
	 *
	 * @return the entity's parent entity or <code>null</code>.
	 */
	public ComposedEntity getParent();

	/**
	 * Sets the entity's new parent entity, if possible.
	 *
	 * @param parent the entity's parent entity or <code>null</code> to remove a
	 *               parent.
	 * @throws EntityException if the current parent is not <code>null</code> (and
	 *                         the given new parent is not <code>null</code> either)
	 *                         or if the new parent dose not allow .
	 *
	 * @see ComposedEntity#addChild(Entity)
	 * @see ComposedEntity#removeChild(Entity)
	 */
	public void setParent(ComposedEntity parent) throws EntityException;

	/**
	 * Adds an {@link EntityListener}.
	 *
	 * @param l the listener.
	 */
	public void addEntityListener(EntityListener l);

	/**
	 * Removes an {@link EntityListener}.
	 *
	 * @param l the listener.
	 */
	public void removeEntityListener(EntityListener l);

	/**
	 * Adds a property for this <code>Entity</code>.
	 *
	 * @param property property for this device
	 */
	public abstract void addProperty(Property property);

	/**
	 * Gets the properties of this <code>Entity</code>.
	 *
	 * @return a set with all properties
	 */
	public abstract Set<Property> getProperties();

	/**
	 * Get all properties of the given type.
	 *
	 * @param type The property type
	 * @return a set with all found properties of the given type.
	 */
	public abstract <T extends Property> Set<T> getProperties(Class<T> type);

	/**
	 * Returns whether this Entitity has properies of the given type.
	 *
	 * @param type The property type
	 * @return true, if at least one property of the given type was found
	 */
	public <T extends Property> boolean hasProperty(Class<T> type);

	/**
	 * Removes a property, if this Entity has this property.
	 *
	 * @param property The property to remove
	 */
	public void removeProperty(Property property);

	/**
	 * Removes all properties of the given type, if this Entity has such.
	 *
	 * @param type The type of properties to remove
	 */
	public <T extends Property> void removeProperties(Class<T> type);

	/**
	 * Adds an {@link PropertyListener}.
	 *
	 * @param l the listener.
	 */
	public void addPropertyListener(PropertyListener l);

	/**
	 * Removes an {@link PropertyListener}.
	 *
	 * @param l the listener.
	 */
	public void removePropertyListener(PropertyListener l);

}