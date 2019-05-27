/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.util.EntityUtils;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.TemporaryRelation;

public class WorldUtils {

	public static <A extends Actuator> List<A> getKinematicChain(Frame start, Frame end, Class<A> type) {
		List<Relation> relations = start.getRelationsTo(end);

		if (relations == null) {
			return null;
		}
		List<A> result = new ArrayList<A>();
		Frame current = start;
		addAncestor(current, type, result);

		for (Relation relation : relations) {
			addAncestor(relation, type, result);

			current = relation.getOther(current);
			addAncestor(current, type, result);
		}

		return result;
	}

	private static <A extends Actuator> void addAncestor(Entity entity, Class<A> type, List<A> result) {
		A ancestor = EntityUtils.getAncestor(entity, type);

		if (ancestor != null && !result.contains(ancestor)) {
			result.add(ancestor);
		}
	}

	public static <T extends ComposedEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type) {
		return getConnectedEntities(frame, type, new Entity[0]);
	}

	public static <T extends ComposedEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			Entity... forbiddenEntities) {
		return getConnectedEntities(frame, type, true, true, forbiddenEntities);
	}

	public static <T extends ComposedEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			boolean withDynamic, boolean withTemporary) {
		return getConnectedEntities(frame, type, withDynamic, withTemporary, new Entity[0]);
	}

	public static <T extends ComposedEntity> Set<T> getConnectedEntities(Frame frame, Class<T> type,
			boolean withDynamic, boolean withTemporary, Entity... forbiddenEntities) {
		Set<T> ret = new HashSet<T>();
		List<Entity> forbidden = Arrays.asList(forbiddenEntities);

		// get all connected frames and relations
		Set<Entity> connected = getConnectedFramesAndRelations(frame, withDynamic, withTemporary, forbiddenEntities);

		for (Entity entity : connected) {
			Set<T> ancestors = EntityUtils.getAncestors(entity, type);

			for (T t : ancestors) {
				if (!forbidden.contains(t)) {
					ret.add(t);
				}
			}
		}
		return ret;
	}

	public static Set<Entity> getConnectedFramesAndRelations(Frame frame) {
		return getConnectedFramesAndRelations(frame, true, true);
	}

	public static Set<Entity> getConnectedFramesAndRelations(Frame frame, boolean withDynamic, boolean withTemporary) {
		return getConnectedFramesAndRelations(frame, withDynamic, withTemporary, new Entity[0]);
	}

	public static Set<Entity> getConnectedFramesAndRelations(Frame frame, boolean withDynamic, boolean withTemporary,
			Entity... forbiddenFramesAndRelations) {
		List<Frame> todoList = new LinkedList<Frame>();
		Set<Entity> ret = new HashSet<Entity>();
		List<Entity> forbidden = Arrays.asList(forbiddenFramesAndRelations);

		// check if frame is already forbidden
		if (forbidden.contains(frame)) {
			return ret;
		}
		todoList.add(frame);
		ret.add(frame); // add to prevent frame from being added later

		while (!todoList.isEmpty()) {
			for (Frame f : new LinkedList<Frame>(todoList)) {
				todoList.remove(f);

				for (Relation r : f.getRelations()) {
					// check for dynamic connection
					if (r instanceof DynamicConnection && !withDynamic) {
						continue;
					}

					// check for temporary placement
					if (r instanceof TemporaryRelation && !withTemporary) {
						continue;
					}

					// check if relation is not forbidden
					if (!forbidden.contains(r)) {
						ret.add(r);

						Frame other = r.getOther(f);

						// check if frame is not forbidden AND
						// check if we know this frame already
						if (!forbidden.contains(other) && !ret.contains(other)) {
							ret.add(other);
							todoList.add(other);
						}
					}
				}
			}
		}
		ret.remove(frame);

		return ret;

	}

	public static Set<ComposedEntity> getEntities(Frame frame) {
		return EntityUtils.getAncestors(frame);
	}

	public static <T extends ComposedEntity> Set<T> getEntities(Frame frame, Class<T> type) {
		return EntityUtils.getAncestors(frame, type);
	}

	public static Set<ComposedEntity> getEntities(Relation relation) {
		return EntityUtils.getAncestors(relation);
	}

	public static <T extends ComposedEntity> Set<T> getEntities(Relation relation, Class<T> type) {
		return EntityUtils.getAncestors(relation, type);
	}

	/**
	 * Retrieves the frames directly belonging to the given {@link ComposedEntity}.
	 * 
	 * @param entity the entity
	 * @return set of frames
	 */
	public static Set<Frame> getOwnedFrames(ComposedEntity entity) {
		return EntityUtils.getChildren(entity, Frame.class);
	}

	/**
	 * Retrieves the frames belonging to the given {@link ComposedEntity} or to its
	 * children.
	 * 
	 * @param entity the entity
	 * @return set of frames
	 */
	public static Set<Frame> getFrames(ComposedEntity entity) {
		return EntityUtils.getDescendants(entity, Frame.class);
	}

	/**
	 * Retrieves the relations directly belonging to the given
	 * {@link ComposedEntity}.
	 * 
	 * @param entity the entity
	 * @return set of relations
	 */
	public static Set<Relation> getOwnedRelations(ComposedEntity entity) {
		return EntityUtils.getChildren(entity, Relation.class);
	}

	/**
	 * Retrieves the relations belonging to the given {@link ComposedEntity} or to
	 * its children.
	 * 
	 * @param entity the entity
	 * @return set of relations
	 */
	public static Set<Relation> getRelations(ComposedEntity entity) {
		return EntityUtils.getDescendants(entity, Relation.class);
	}

}
