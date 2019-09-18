/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.geometricobject.collision.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.facet.collision.properties.CollisionShapeProperty;
import org.roboticsapi.facet.collision.shapes.BoxShape;
import org.roboticsapi.framework.geometricobject.Box;

public class GeometricObjectCollisionExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof Box) {
			Box box = (Box) object;
			box.addProperty(new CollisionShapeProperty("box", new BoxShape(Transformation.IDENTITY,
					box.getXExtent() / 2, box.getYExtent() / 2, box.getZExtent() / 2)));
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {

	}

}
