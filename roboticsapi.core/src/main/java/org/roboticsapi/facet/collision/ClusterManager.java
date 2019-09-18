/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Property;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.util.FrameGraph;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphAdapter;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphListener;
import org.roboticsapi.core.world.util.RoboticsEntityGraph;
import org.roboticsapi.core.world.util.RoboticsEntityGraph.RoboticsEntityGraphListener;
import org.roboticsapi.facet.collision.properties.CollisionFriendsProperty;
import org.roboticsapi.facet.collision.properties.CollisionShapeProperty;
import org.roboticsapi.facet.collision.shapes.Shape;

public class ClusterManager {

	/**
	 * Listener for entity clusters.
	 */
	public static interface ClusterListener {

		void onUpdating();

		void onAdded(PhysicalObject entity, Shape[] hulls, Set<PhysicalObject> cluster);

		void onRemoved(PhysicalObject entity, Set<PhysicalObject> cluster);

		void onFound(Set<PhysicalObject> friends);

		void onUpdated();
	}

	private final FrameGraph frameGraph;
	private final RoboticsEntityGraph entityGraph;

	private final List<Cluster> clusters = new ArrayList<Cluster>();
	private final List<ClusterListener> listeners = new ArrayList<ClusterListener>();
	private final Map<Frame, Set<PhysicalObject>> open = new HashMap<Frame, Set<PhysicalObject>>();

	private final FrameGraphListener frameListener = new FrameGraphAdapter() {

		public void onRelationAdded(Relation relation) {
			Frame knownFrame = relation.getFrom();
			Frame newFrame = relation.getTo();
			Cluster cluster = getCluster(knownFrame);

			if (cluster == null) {
				knownFrame = relation.getTo();
				newFrame = relation.getFrom();
				cluster = getCluster(knownFrame);
			}

			if (relation instanceof StaticConnection) {
				cluster.add(newFrame);
			} else {
				cluster = makeCluster(newFrame);
			}

			Set<PhysicalObject> set = open.remove(newFrame);

			if (set != null) {
				for (PhysicalObject object : set) {
					cluster.add(object);

					Set<CollisionShapeProperty> properties = object.getProperties(CollisionShapeProperty.class);

					for (CollisionShapeProperty hullProperty : properties) {
						Shape[] hulls = hullProperty.shapes;
						notifyOnEntityAdded(object, hulls, cluster);
					}
				}
			}
		}

		public void onFrameRemoved(Frame frame) {
			Cluster cluster = getCluster(frame);

			if (cluster != null) {
				cluster.remove(frame);

				if (cluster.isEmpty()) {
					clusters.remove(cluster);
				}
			}
		}

	};

	private final RoboticsEntityGraphListener entityListener = new RoboticsEntityGraphListener() {

		public void onEntitiesUpdated() {
			notifyOnUpdated();
		}

		public void onEntitiesUpdating() {
			notifyOnUpdating();
		}

		@Override
		public void onPropertyAdded(RoboticsEntity entity, Property property) {
			if (entity instanceof PhysicalObject) {
				if (property instanceof CollisionShapeProperty) {
					PhysicalObject object = (PhysicalObject) entity;
					CollisionShapeProperty hullProperty = (CollisionShapeProperty) property;
					Shape[] hulls = hullProperty.shapes;

					Cluster cluster = getCluster(object);

					if (cluster != null) {
						notifyOnEntityAdded(object, hulls, cluster);
					}
				}
			}

			if (property instanceof CollisionFriendsProperty) {
				CollisionFriendsProperty fProperty = (CollisionFriendsProperty) property;
				notifyOnFound(fProperty.getFriends());
			}
		}

		@Override
		public void onEntityRemoved(RoboticsEntity entity) {
			if (entity instanceof PhysicalObject) {
				PhysicalObject object = (PhysicalObject) entity;
				Cluster cluster = getCluster(object);

				if (cluster != null) {
					cluster.remove(object);

					if (cluster.isEmpty()) {
						clusters.remove(cluster);
					}
					notifyOnEntityRemoved(object, cluster);
				}
			}
		}

		@Override
		public void onEntityAdded(RoboticsEntity entity) {
			if (entity instanceof PhysicalObject) {
				PhysicalObject object = (PhysicalObject) entity;
				Frame base = object.getBase();

				Cluster cluster = getCluster(base);

				if (cluster == null) {
					Set<PhysicalObject> set = open.get(base);

					if (set == null) {
						set = new HashSet<PhysicalObject>();
						open.put(base, set);
					}
					set.add(object);
					return;
				}
				cluster.add(object);
			}
		}

	};

	public ClusterManager(RoboticsEntityGraph g, ClusterListener... listeners) {
		this.entityGraph = g;
		this.frameGraph = g.getFrameGraph();
		makeCluster(frameGraph.getRoot());

		for (ClusterListener l : listeners) {
			addClusterListener(l);
		}
		frameGraph.addFrameGraphListener(frameListener);
		entityGraph.addEntityGraphListener(entityListener);
	}

	protected void finalize() throws Throwable {
		frameGraph.removeFrameGraphListener(frameListener);
		entityGraph.removeEntityGraphListener(entityListener);
	};

	public boolean hasCluster(Frame f) {
		return getCluster(f) != null;
	}

	public Cluster getCluster(Frame f) {
		for (Cluster cluster : this.clusters) {
			if (cluster.contains(f)) {
				return cluster;
			}
		}
		return null;
	}

	public boolean hasCluster(PhysicalObject o) {
		return getCluster(o) != null;
	}

	public Cluster getCluster(PhysicalObject o) {
		for (Cluster cluster : this.clusters) {
			if (cluster.contains(o)) {
				return cluster;
			}
		}
		return null;
	}

	private Cluster makeCluster(Frame f) {
		Cluster cluster = new Cluster();
		cluster.add(f);
		clusters.add(cluster);

		return cluster;
	}

	public void addClusterListener(ClusterListener l) {
		this.listeners.add(l);
	}

	public void removeClusterListener(ClusterListener l) {
		this.listeners.remove(l);
	}

	private void notifyOnUpdating() {
		for (ClusterListener l : new ArrayList<ClusterListener>(this.listeners)) {
			l.onUpdating();
		}
	}

	private void notifyOnEntityAdded(PhysicalObject entity, Shape[] hulls, Cluster cluster) {
		Set<PhysicalObject> entities = cluster.entities();

		for (ClusterListener l : new ArrayList<ClusterListener>(this.listeners)) {
			l.onAdded(entity, hulls, entities);
		}
	}

	private void notifyOnEntityRemoved(PhysicalObject entity, Cluster cluster) {
		Set<PhysicalObject> entities = cluster.entities();

		for (ClusterListener l : new ArrayList<ClusterListener>(this.listeners)) {
			l.onRemoved(entity, entities);
		}
	}

	private void notifyOnFound(Set<PhysicalObject> friends) {
		for (ClusterListener l : new ArrayList<ClusterListener>(this.listeners)) {
			l.onFound(friends);
		}
	}

	private void notifyOnUpdated() {
		for (ClusterListener l : new ArrayList<ClusterListener>(this.listeners)) {
			l.onUpdated();
		}
	}

}
