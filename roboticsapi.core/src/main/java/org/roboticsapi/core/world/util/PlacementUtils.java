/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.relation.Placement;
import org.roboticsapi.core.world.relation.StaticPosition;

public class PlacementUtils {

	public static Placement place(Frame frame, Frame reference)
			throws InitializationException, TransformationException {
		Transformation transformation = reference.getTransformationTo(frame);
		return place(frame, reference, transformation);
	}

	public static Placement place(Frame frame, Frame reference, Transformation t) throws InitializationException {
		Placement p = new Placement(reference, frame);
		p.establish();
		new StaticPosition(reference, frame, t).establish();
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
		return unplace(frame, new ArrayList<Relation>(), Arrays.asList(forbiddenPlacements));
	}

	public static boolean unplace(PhysicalObject object) {
		return unplace(object.getBase());
	}

	public static boolean unplace(PhysicalObject object, Placement... forbiddenPlacements) {
		return unplace(object.getBase(), forbiddenPlacements);
	}

	protected static boolean unplace(Frame frame, List<Relation> done, List<Placement> forbidden) {
		return unplace(frame, done, forbidden, World.getCommandedTopology());
	}

	protected static boolean unplace(Frame frame, List<Relation> done, List<Placement> forbidden,
			FrameTopology topology) {
		boolean ret = true;
		List<Relation> open = new ArrayList<Relation>();
		List<Relation> relations = topology.getRelations(frame);

		for (Relation relation : new ArrayList<Relation>(relations)) {
			if (relation instanceof LogicalRelation) {
				if (!((LogicalRelation) relation).isPersistent()) {
					if (forbidden.contains(relation)) {
						continue;
					}

					// TODO Handle temporary placements
					relation.remove();
				} else {
					open.add(relation);
				}
			}
		}

		for (Relation connection : open) {
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
