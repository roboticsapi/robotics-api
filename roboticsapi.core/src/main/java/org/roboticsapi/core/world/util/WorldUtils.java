/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.util.RoboticsEntityUtils;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.World;

public class WorldUtils {

	public static <T extends RoboticsEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type) {
		return getConnectedEntities(frame, type, new Frame[0]);
	}

	public static <T extends RoboticsEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			Frame... forbiddenEntities) {
		return getConnectedEntities(frame, type, true, forbiddenEntities);
	}

	public static <T extends RoboticsEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			boolean withDynamic) {
		return getConnectedEntities(frame, type, withDynamic, new Frame[0]);
	}

	public static <T extends RoboticsEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			boolean withDynamic, RoboticsEntity... forbiddenEntities) {
		Set<T> ret = new HashSet<T>();
		List<RoboticsEntity> forbidden = Arrays.asList(forbiddenEntities);

		// get all connected frames and relations
		Set<RoboticsEntity> connected = getConnectedFrames(frame, withDynamic, forbiddenEntities);

		for (RoboticsEntity entity : connected) {
			Set<T> ancestors = RoboticsEntityUtils.getAncestors(entity, type);

			for (T t : ancestors) {
				if (!forbidden.contains(t)) {
					ret.add(t);
				}
			}
		}
		return ret;
	}

	public static Set<RoboticsEntity> getConnectedFrames(Frame frame) {
		return getConnectedFrames(frame, true);
	}

	public static Set<RoboticsEntity> getConnectedFrames(Frame frame, boolean withDynamic) {
		return getConnectedFrames(frame, withDynamic, new Frame[0]);
	}

	public static Set<RoboticsEntity> getConnectedFrames(Frame frame, boolean withDynamic,
			RoboticsEntity... forbiddenEntities) {
		FrameTopology topology = World.getCommandedTopology();
		if (!withDynamic) {
			topology = topology.withoutDynamic();
		}
		List<Frame> forbiddenFrames = new ArrayList<Frame>();
		for (RoboticsEntity e : forbiddenEntities) {
			if (e instanceof Frame) {
				forbiddenFrames.add((Frame) e);
			}
		}

		topology = topology.withoutFrames(forbiddenFrames);
		return getConnectedFrames(frame, topology);

	}

	public static Set<RoboticsEntity> getConnectedFrames(Frame frame, FrameTopology topology) {
		List<Frame> todoList = new LinkedList<Frame>();
		Set<RoboticsEntity> ret = new HashSet<RoboticsEntity>();

		todoList.add(frame);
		ret.add(frame); // add to prevent frame from being added later

		while (!todoList.isEmpty()) {
			for (Frame f : new LinkedList<Frame>(todoList)) {
				todoList.remove(f);

				for (Relation r : topology.getRelations(f)) {
					Frame other = r.getOther(f);

					// check if frame is not forbidden AND
					// check if we know this frame already
					if (!ret.contains(other)) {
						ret.add(other);
						todoList.add(other);
					}
				}
			}
		}
		ret.remove(frame);
		return ret;
	}

	public static Set<RoboticsEntity> getEntities(Frame frame) {
		return RoboticsEntityUtils.getAncestors(frame);
	}

	public static <T extends RoboticsEntity> Set<T> getEntities(Frame frame, Class<T> type) {
		return RoboticsEntityUtils.getAncestors(frame, type);
	}

	/**
	 * Retrieves the frames directly belonging to the given {@link RoboticsEntity}.
	 *
	 * @param entity the entity
	 * @return set of frames
	 */
	public static Set<Frame> getOwnedFrames(RoboticsEntity entity) {
		return RoboticsEntityUtils.getChildren(entity, Frame.class);
	}

	/**
	 * Retrieves the frames belonging to the given {@link RoboticsEntity} or to its
	 * children.
	 *
	 * @param entity the entity
	 * @return set of frames
	 */
	public static Set<Frame> getFrames(RoboticsEntity entity) {
		return RoboticsEntityUtils.getDescendants(entity, Frame.class);
	}

}
