/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;

public class PlacementUtils {

	public static Placement place(Frame frame, Frame reference)
			throws InitializationException, TransformationException {
		Transformation transformation = reference.getTransformationTo(frame);
		return place(frame, reference, transformation);
	}

	public static Placement place(Frame frame, Frame reference, Transformation t) throws InitializationException {
		Placement p = new Placement(t);
		reference.addRelation(p, frame);

		return p;
	}

	public static Placement place(PhysicalObject object, Frame reference)
			throws InitializationException, TransformationException {
		return place(object.getBase(), reference);
	}

	public static Placement place(PhysicalObject object, Frame reference, Transformation t)
			throws InitializationException {
		return place(object.getBase(), reference, t);
	}

	public static boolean unplace(Frame frame) {
		return unplace(frame, new Placement[0]);
	}

	public static boolean unplace(Frame frame, Placement... forbiddenPlacements) {
		return unplace(frame, new ArrayList<Connection>(), Arrays.asList(forbiddenPlacements));
	}

	public static boolean unplace(PhysicalObject object) {
		return unplace(object.getBase());
	}

	public static boolean unplace(PhysicalObject object, Placement... forbiddenPlacements) {
		return unplace(object.getBase(), forbiddenPlacements);
	}

	protected static boolean unplace(Frame frame, List<Connection> done, List<Placement> forbidden) {
		boolean ret = true;
		List<Connection> open = new ArrayList<Connection>();
		List<Relation> relations = frame.getRelations();

		for (Relation relation : new ArrayList<Relation>(relations)) {
			if (relation instanceof Placement) {
				if (forbidden.contains(relation)) {
					continue;
				}

				// TODO Handle temporary placements
				try {
					relation.getFrom().removeRelation(relation);
				} catch (InitializationException e) {
					ret = false;
				}
			} else if (relation instanceof Connection) {
				open.add((Connection) relation);
			}
		}

		for (Connection connection : open) {
			if (done.contains(connection)) {
				continue;
			}

			done.add(connection);
			Frame other = connection.getOther(frame);
			ret &= unplace(other, done, forbidden);
		}
		return ret;
	}

}
