/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.RealtimeValueListenerRegistration;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformationArray;
import org.roboticsapi.core.world.util.FrameGraph;
import org.roboticsapi.core.world.util.RoboticsEntityGraph;
import org.roboticsapi.facet.collision.ClusterManager.ClusterListener;
import org.roboticsapi.facet.collision.shapes.Shape;

public class CollisionObjectGraph extends Observable {

	public interface CollisionListener {

		void onCollisionAppeared(Collision<PhysicalObject> firstCollision);

		void onCollisionDisappeared(Collision<PhysicalObject> lastCollision);

	}

	private class TransformationUpdater implements RealtimeValueListener<Transformation> {

		private final PhysicalObject object;

		public TransformationUpdater(PhysicalObject object) {
			super();
			this.object = object;
		}

		@Override
		public void onValueChanged(Transformation newValue) {
			synchronized (world) {
				world.updateTransformation(object, newValue);
				setChanged();
				notifyObservers(object);
			}
		}
	}

	private final CollisionSpace<PhysicalObject> world;
	private final FrameGraph frameGraph;
	private final ClusterManager manager;

	private final Map<PhysicalObject, RealtimeValueListenerRegistration<Transformation>> regMap = new HashMap<PhysicalObject, RealtimeValueListenerRegistration<Transformation>>();
	private RealtimeTransformationArray sensor;
	private RealtimeValueListener<Transformation[]> updater;

	private final ClusterListener listener = new ClusterListener() {

		boolean needsUpdate = false;
		List<RealtimeValueListenerRegistration<?>> toAdd = new ArrayList<RealtimeValueListenerRegistration<?>>(),
				toRem = new ArrayList<RealtimeValueListenerRegistration<?>>();

		@Override
		public void onUpdating() {
			synchronized (world) {
				toAdd.clear();
				toRem.clear();
				needsUpdate = false;
			}
		}

		@Override
		public void onAdded(PhysicalObject entity, Shape[] hulls, Set<PhysicalObject> cluster) {
			synchronized (world) {
				try {
					RealtimeTransformation sensor = frameGraph.getRealtimeTransformationTo(entity.getBase());
					if (sensor == null)
						return;
					Transformation transformation = sensor.getCurrentValue();

					world.addKinematicObject(entity, transformation);

					for (PhysicalObject o : cluster) {
						if (o != entity) {
							world.ignoreCollisions(entity, o);
						}
					}
					TransformationUpdater updater = new TransformationUpdater(entity);

					RealtimeValueListenerRegistration<Transformation> reg = sensor.createListenerRegistration(updater);
					regMap.put(entity, reg);

					toAdd.add(reg);

					setChanged();
					notifyObservers(entity);
					needsUpdate = true;
				} catch (RealtimeValueReadException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onRemoved(PhysicalObject entity, Set<PhysicalObject> cluster) {
			synchronized (world) {
				world.removeCollisionObject(entity);

				for (PhysicalObject o : cluster) {
					world.unignoreCollisions(entity, o);
				}
				RealtimeValueListenerRegistration<Transformation> reg = regMap.remove(entity);

				if (reg != null) {
					toRem.add(reg);

					setChanged();
					notifyObservers(entity);
					needsUpdate = true;
				}
			}
		}

		@Override
		public void onUpdated() {
			synchronized (world) {
				if (needsUpdate) {
					try {
						RealtimeValue.removeListeners(toRem);
						RealtimeValue.addListeners(toAdd);
					} catch (RoboticsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onFound(Set<PhysicalObject> friends) {
			int size = friends.size();
			PhysicalObject[] array = friends.toArray(new PhysicalObject[size]);

			for (int i = 0; i < array.length - 1; i++) {
				PhysicalObject o1 = array[i];

				for (int j = i + 1; j < array.length; j++) {
					PhysicalObject o2 = array[j];
					world.ignoreCollisions(o1, o2);
				}
			}
		}
	};

	public CollisionObjectGraph(Frame root, CollisionSpace<PhysicalObject> world) {
		this(new FrameGraph(root), world);
	}

	public CollisionObjectGraph(FrameGraph frameGraph, CollisionSpace<PhysicalObject> world) {
		this(new RoboticsEntityGraph(frameGraph), world);
	}

	public CollisionObjectGraph(RoboticsEntityGraph entityGraph, CollisionSpace<PhysicalObject> world) {
		this.frameGraph = entityGraph.getFrameGraph();
		this.world = world;
		this.manager = new ClusterManager(entityGraph, listener);
	}

	protected void finalize() throws Throwable {
		manager.removeClusterListener(listener);
	}

	public Set<Collision<PhysicalObject>> checkCollisions() {
		synchronized (world) {
			return world.checkCollisions();
		}
	}

	// addListener, removeListener

	public Set<CollisionRange<PhysicalObject>> checkCollisions(WritableRealtimeDouble value, double start, double end) {
		return null;
	}

	public List<RayTestResult<PhysicalObject>> rayTest(Frame frame, Vector ray) {
		try {
			Transformation[] value = sensor.getCurrentValue();
			updater.onValueChanged(value);
			Transformation t = frameGraph.getTransformation(frame);

			return world.rayTest(t, ray);
		} catch (RealtimeValueReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// addListener, removeListener

}
