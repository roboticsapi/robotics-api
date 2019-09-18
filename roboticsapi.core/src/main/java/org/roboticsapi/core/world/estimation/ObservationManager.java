/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.observation.Observation;
import org.roboticsapi.core.world.util.FrameGraph;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphListener;

public class ObservationManager {

	private final FrameGraph graph;
	private final List<ObservationListener> listeners = new ArrayList<ObservationListener>();
	private final FrameTopology topology;

	private final List<Observation> observations = new ArrayList<Observation>();
	private final List<LogicalRelation> logicalRelations = new ArrayList<LogicalRelation>();

	private final FrameGraphListener graphListener = new FrameGraphListener() {

		@Override
		public void onRelationRemoved(Relation object) {
			if (object instanceof LogicalRelation) {
				if (!logicalRelations.contains(object)) {
					return;
				}
				LogicalRelation relation = (LogicalRelation) object;
				logicalRelations.remove(relation);
				if (getGeometric(relation) == null) {
					for (ObservationListener listener : listeners) {
						listener.logicalRelationRemoved(relation);
					}
				}
			} else if (object instanceof GeometricRelation) {
				for (LogicalRelation relation : logicalRelations) {
					if (getGeometric(relation) == object) {
						for (ObservationListener listener : listeners) {
							listener.logicalRelationAdded(relation);
						}
					}
				}
			} else if (object instanceof Observation && observations.contains(object)) {
				observations.remove(object);
				for (ObservationListener listener : listeners) {
					listener.observationRemoved((Observation) object);
				}
			}
		}

		@Override
		public void onRelationAdded(Relation object) {
			if (logicalRelations.contains(object) || observations.contains(object)) {
				return;
			}
			if (object instanceof LogicalRelation) {
				LogicalRelation relation = (LogicalRelation) object;
				logicalRelations.add(relation);
				if (getGeometric(relation) != null) {
					for (ObservationListener listener : listeners) {
						listener.logicalRelationAdded(relation);
					}
				}
			} else if (object instanceof GeometricRelation) {
				for (LogicalRelation relation : logicalRelations) {
					if (getGeometric(relation) == object) {
						for (ObservationListener listener : listeners) {
							listener.logicalRelationResolved(relation);
						}
					}
				}
			} else if (object instanceof Observation) {
				observations.add((Observation) object);
				for (ObservationListener listener : listeners) {
					listener.observationAdded((Observation) object);
				}
			}
		}

		@Override
		public void onFrameRemoved(Frame frame) {
		}

		@Override
		public void onFrameGraphUpdating() {
		}

		@Override
		public void onFrameGraphUpdated() {
		}

		@Override
		public void onFrameAdded(Frame frame) {
		}
	};

	public ObservationManager(Frame root, FrameTopology topology) {
		this.topology = topology;
		graph = new FrameGraph(root, topology);
	}

	public void addListener(ObservationListener listener) {
		listeners.add(listener);
		for (Observation observation : getObservations()) {
			listener.observationAdded(observation);
		}
		for (LogicalRelation relation : getUnresolvedLogicalRelations()) {
			listener.logicalRelationAdded(relation);
		}
		if (listeners.size() == 1) {
			graph.addFrameGraphListener(graphListener);
		}
	}

	public void removeListener(ObservationListener listener) {
		List<Observation> obs = getObservations();
		List<LogicalRelation> rel = getUnresolvedLogicalRelations();
		listeners.remove(listener);
		for (Observation observation : obs) {
			listener.observationRemoved(observation);
		}
		for (LogicalRelation relation : rel) {
			listener.logicalRelationRemoved(relation);
		}
		if (listeners.isEmpty()) {
			logicalRelations.clear();
			observations.clear();
			graph.removeFrameGraphListener(graphListener);
		}
	}

	private GeometricRelation getGeometric(LogicalRelation relation) {
		for (Relation object : relation.getFrom().getRelations(topology)) {
			if (object instanceof GeometricRelation) {
				if (object.getFrom() == relation.getFrom() && object.getTo() == relation.getTo()) {
					return (GeometricRelation) object;
				}
				if (object.getFrom() == relation.getTo() && object.getTo() == relation.getFrom()) {
					return (GeometricRelation) object;
				}
			}
		}
		return null;
	}

	interface ObservationListener {
		void observationAdded(Observation o);

		void observationRemoved(Observation o);

		void logicalRelationAdded(LogicalRelation r);

		void logicalRelationRemoved(LogicalRelation r);

		void logicalRelationResolved(LogicalRelation r);
	}

	public List<Observation> getObservations() {
		if (listeners.isEmpty()) {
			throw new IllegalStateException("ObservationManager is disabled when no listeners are registered");
		}

		return observations;
	}

	public List<LogicalRelation> getUnresolvedLogicalRelations() {
		if (listeners.isEmpty()) {
			throw new IllegalStateException("ObservationManager is disabled when no listeners are registered");
		}
		List<LogicalRelation> ret = new ArrayList<LogicalRelation>();
		for (LogicalRelation relation : logicalRelations) {
			if (getGeometric(relation) == null) {
				ret.add(relation);
			}
		}
		return ret;
	}

	public List<LogicalRelation> getResolvedLogicalRelations() {
		if (listeners.isEmpty()) {
			throw new IllegalStateException("ObservationManager is disabled when no listeners are registered");
		}
		List<LogicalRelation> ret = new ArrayList<LogicalRelation>();
		for (LogicalRelation relation : logicalRelations) {
			if (getGeometric(relation) != null) {
				ret.add(relation);
			}
		}
		return ret;
	}

}
