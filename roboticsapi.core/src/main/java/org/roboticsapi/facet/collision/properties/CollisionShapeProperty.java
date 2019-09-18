/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.properties;

import org.roboticsapi.core.Property;
import org.roboticsapi.facet.collision.shapes.Shape;

/**
 * {@link Property} for defining collision shapes
 */
public class CollisionShapeProperty implements Property {

	public final Shape[] shapes;
	public final String name;

	public CollisionShapeProperty(String name, Shape... shapes) {
		this.name = name;
		this.shapes = shapes;
	}
}
