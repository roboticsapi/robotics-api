/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.estimation.ObservationManager.ObservationListener;
import org.roboticsapi.core.world.observation.FramePoseObservation;
import org.roboticsapi.core.world.observation.Observation;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.EstimatedPosition;

public abstract class AbstractEstimator extends AbstractRoboticsObject implements ObservationListener {
	private FrameTopology topology;
	private ObservationManager observationManager = null;
	private final Dependency<RoboticsRuntime> runtime;
	private final Dependency<Frame> root;
	private final List<Estimation> estimations = new ArrayList<Estimation>();

	public AbstractEstimator() {
		runtime = createDependency("runtime");
		root = createDependency("root");
	}

	protected FrameTopology getTopology() {
		return topology;
	}

	private List<Estimation> getEstimationsForRelation(Relation r) {
		List<Estimation> ret = new ArrayList<Estimation>();
		for (Estimation e : estimations) {
			if (e.getResolvedLogicalRelations().contains(r)) {
				ret.add(e);
			}
		}
		return ret;
	}

	private List<Estimation> getEstimationsForObservation(Observation o) {
		List<Estimation> ret = new ArrayList<Estimation>();
		for (Estimation e : estimations) {
			if (e.getUsedObservation() == o) {
				ret.add(e);
			}
		}
		return ret;
	}

	private List<Estimation> getDependingEstimations(Relation r) {
		List<Estimation> ret = new ArrayList<Estimation>();
		for (Estimation e : estimations) {
			if (e.getUsedGeometricRelations().contains(r)) {
				ret.add(e);
			}
		}
		return ret;
	}

	protected class Estimation extends EstimatedPosition {
		public final List<Relation> relations;
		public final List<Relation> dependencies;
		public final Observation observation;
		private final RealtimeTwist twist;
		private final RealtimeTransformation transformation;
		private RealtimeTwist twistPersisted;
		private RealtimeTransformation transformationPersisted;
		private PersistContext<Transformation> pc;

		public Estimation(Frame from, Frame to, RealtimeTransformation transformation, RealtimeTwist twist,
				Observation o, List<Relation> relations, List<Relation> dependencies) {
			super(from, to, transformation, twist);
			this.transformation = transformation;
			this.twist = twist;
			this.observation = o;
			this.relations = relations;
			this.dependencies = dependencies;
		}

		@Override
		public void establish() {
			try {
				Command pc = runtime.get().createWaitCommand("Estimation from " + getFrom() + " to " + getTo());
				PersistContext<Transformation> pcTrans = new PersistContext<Transformation>(pc);
				PersistContext<Twist> pcTwist = new PersistContext<Twist>(pc);
				if (twist != null) {
					pc.assign(twist, pcTwist, transformation.isNull().not());
				}
				pc.assign(transformation, pcTrans, transformation.isNull().not());
				pc.start();
				this.pc = pcTrans;
				transformationPersisted = (RealtimeTransformation) pcTrans.getValue();
				if (twist != null) {
					twistPersisted = (RealtimeTwist) pcTwist.getValue();
				} else {
					twistPersisted = RealtimeTwist.createFromConstant(new Twist());
				}
			} catch (RoboticsException e) {
				transformationPersisted = super.getTransformation();
				twistPersisted = super.getTwist();
				RAPILogger.logException(AbstractEstimator.class, e);
			}
			estimations.add(this);

			RAPILogger.getLogger(this).info("Established " + toString());

			super.establish();
		}

		@Override
		public void remove() {
			super.remove();

			estimations.remove(this);
			try {
				if (pc != null) {
					pc.unpersist();
				}
				pc = null;
			} catch (CommandException e) {
				e.printStackTrace();
			}

		}

		@Override
		protected RealtimeTransformation getTransformation() {
			return transformationPersisted;
		}

		@Override
		protected RealtimeTwist getTwist() {
			return twistPersisted;
		}

		@Override
		public String toString() {
			return "Estimation from " + getFrom() + " to " + getTo() + " through " + observation;
		}

		/**
		 * Retrieves the {@link GeometricRelation}s that are used to compute this
		 * {@link Estimation}'s position and twist
		 *
		 * @return used {@link GeometricRelation}
		 */
		public List<Relation> getUsedGeometricRelations() {
			return dependencies;
		}

		/**
		 * Retrieves the {@link LogicalRelation} describing the relationship estimated
		 * here
		 *
		 * @return {@link LogicalRelation} resolved by this {@link Estimation}
		 */
		public List<Relation> getResolvedLogicalRelations() {
			return relations;
		}

		/**
		 * Retrieves the {@link Observation} this estimation is based on
		 *
		 * @return the used {@link Observation}
		 */
		public Observation getUsedObservation() {
			return observation;
		}

	}

	public RoboticsRuntime getRuntime() {
		return runtime.get();
	}

	@ConfigurationProperty
	public void setRuntime(RoboticsRuntime runtime) {
		this.runtime.set(runtime);
	}

	public Frame getRoot() {
		return root.get();
	}

	@ConfigurationProperty
	public void setRoot(Frame root) {
		this.root.set(root);
	}

	@Override
	protected void afterInitialization() {
		topology = World.getMeasuredTopology().forRuntime(runtime.get());
		observationManager = new ObservationManager(root.get(), topology);
		observationManager.addListener(this);
		super.afterInitialization();
	}

	@Override
	protected void beforeUninitialization() {
		topology = null;
		observationManager.removeListener(this);
		observationManager = null;
		for (Estimation e : new ArrayList<Estimation>(estimations)) {
			e.remove();
		}
		super.beforeUninitialization();
	}

	@Override
	public void observationAdded(Observation o) {
		if (topology == null) {
			return;
		}
		boolean needsRebuild = false;
		for (LogicalRelation r : new ArrayList<LogicalRelation>(observationManager.getUnresolvedLogicalRelations())) {
			needsRebuild |= removePartials(resolveRelationAndObservation(r, o));
		}
		if (needsRebuild) {
			rebuildEstimations();
		}
	}

	@Override
	public void observationRemoved(Observation o) {
		if (topology == null) {
			return;
		}

		List<Estimation> estimations = getEstimationsForObservation(o);
		if (!estimations.isEmpty()) {
			for (Estimation est : new ArrayList<Estimation>(estimations)) {
				est.remove();
			}
			rebuildEstimations();
		}
	}

	@Override
	public void logicalRelationAdded(LogicalRelation r) {
		if (topology == null) {
			return;
		}
		for (Observation o : new ArrayList<Observation>(observationManager.getObservations())) {
			resolveRelationAndObservation(r, o);
		}
	}

	@Override
	public void logicalRelationResolved(LogicalRelation r) {
		if (topology == null) {
			return;
		}
		boolean removed = false;
		for (Estimation e : new ArrayList<Estimation>(estimations)) {
			if (e.getResolvedLogicalRelations().size() > 1) {
				Relation first = e.getResolvedLogicalRelations().get(0);
				Relation last = e.getResolvedLogicalRelations().get(e.getResolvedLogicalRelations().size() - 1);
				if (r == first || r == last) {
					RAPILogger.getLogger(this).info("Removing estimation " + e);
					e.remove();
					removed = true;
				}
			}
		}
		if (removed) {
			rebuildEstimations();
		}
	}

	@Override
	public void logicalRelationRemoved(LogicalRelation r) {
		if (topology == null) {
			return;
		}
		boolean removed = false;
		for (Estimation rr : getEstimationsForRelation(r)) {
			rr.remove();
			removed = true;
		}
		for (Estimation rr : getDependingEstimations(r)) {
			rr.remove();
			removed = true;
		}
		if (removed) {
			rebuildEstimations();
		}
	}

	private Estimation resolveRelationAndObservation(LogicalRelation r, Observation o) {
		Estimation ret = resolveObservation(o, r.getFrom(), r.getTo(), Arrays.asList(r));
		if (ret != null) {
			return ret;
		}
		ret = resolveObservation(o, r.getTo(), r.getFrom(), Arrays.asList(r));
		return ret;
	}

	private Estimation resolveObservation(Observation o, Frame from, Frame to, List<Relation> rs) {
		if (from.getRealtimeTransformationTo(to, topology) != null) {
			return null;
		}
		// we do not need two estimations that talk about the same observation
		// and uncertain relation
		List<Estimation> ro = getEstimationsForObservation(o);
		if (!ro.isEmpty()) {
			for (Relation r : rs) {
				for (Estimation rr : getEstimationsForRelation(r)) {
					if (ro.contains(rr)) {
						return null;
					}
				}
			}
		}

		// don't resolve a path that is already resolved
		for (Relation r : rs) {
			for (Estimation e : getEstimationsForRelation(r)) {
				if (e.getFrom() == from && e.getTo() == to) {
					return null;
				}
				if (e.getFrom() == to && e.getTo() == from) {
					return null;
				}
			}
		}

		// now we try to resolve the path between from and to (which contains
		// the relations rs) using the given observation

		// currently we can only handle all kinds of FrameObservations
		if (o instanceof FramePoseObservation) {
			// so we see the observed frame at the given transformation
			FramePoseObservation fpo = (FramePoseObservation) o;
			Frame observedFrame = fpo.getTo();
			RealtimePose pose = fpo.getPose();
			if (pose == null) {
				return null;
			}

			List<Relation> globallyForbidden = new ArrayList<Relation>(rs);
			// we don't want to use any estimations created from this
			// observation
			if (ro != null) {
				for (Estimation e : ro) {
					globallyForbidden.add(e);
				}
			}

			for (Estimation e : estimations) {
				if (e.getResolvedLogicalRelations().containsAll(rs)) {
					globallyForbidden.add(e);
				}
			}

			List<Relation> forbidden = new ArrayList<Relation>(globallyForbidden);
			// check for a path from our relation start to the observed frame
			List<Relation> toToObservation = findPath(to, observedFrame, forbidden);

			if (toToObservation != null) {
				forbidden = new ArrayList<Relation>(globallyForbidden);
				forbidden.addAll(toToObservation);

				// now find a path from our relation end to the reference of the
				// observation
				List<Relation> fromToReference = findPath(from, pose.getReference(), forbidden);
				if (fromToReference != null) {

					// if there is a path, we try to find out if any other
					// uncertainty exists on the path
					forbidden = new ArrayList<Relation>(globallyForbidden);
					Frame cur = to;
					for (Relation r : toToObservation) {
						forbidden.add(r);
						cur = r.getOther(cur);
						if (isUncertain(r)) {
							// if so, we extend our handled relation path behind
							// this relation
							return resolveObservation(o, from, cur, forbidden);
						}
					}

					forbidden = new ArrayList<Relation>(globallyForbidden);
					cur = from;
					for (Relation r : fromToReference) {
						forbidden.add(r);
						cur = r.getOther(cur);
						if (isUncertain(r)) {
							// and continue behind
							return resolveObservation(o, cur, to, forbidden);
						}
					}

					// now we know that the path between from and to can be
					// observed through our observation, without any further
					// uncertainty
					try {
						List<Relation> deps = new ArrayList<Relation>();
						deps.addAll(toToObservation);
						deps.addAll(fromToReference);
						Estimation ret = buildEstimation(fpo, from, to, rs, deps);
						if (ret != null) {
							ret.establish();
						}
						return ret;
					} catch (TransformationException e) {
						e.printStackTrace();
					}
				}

			}
		}
		return null;
	}

	private boolean isUncertain(Relation relation) {
		if (relation instanceof LogicalRelation) {
			for (Relation object : relation.getFrom().getRelations(topology)) {
				if (object instanceof GeometricRelation) {
					if (object.getFrom() == relation.getFrom() && object.getTo() == relation.getTo()) {
						return false;
					}
					if (object.getFrom() == relation.getTo() && object.getTo() == relation.getFrom()) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	private boolean isPathWith(final Frame from, final Frame to, final Relation with,
			final List<Relation> allowedUncertain, final RoboticsRuntime runtime) {
		FrameTopology withAllowedUncertain = topology.withRelationFilter(new java.util.function.Predicate<Relation>() {
			@Override
			public boolean test(Relation d) {
				if (allowedUncertain.contains(d)) {
					return true;
				}
				if (isUncertain(d)) {
					return false;
				}
				return true;
			}
		});
		final List<Relation> fromToFrom = from.getRelationsTo(with.getFrom(), withAllowedUncertain);
		if (fromToFrom == null) {
			return false;
		}
		Frame next;
		if (fromToFrom.contains(with)) {
			next = with.getFrom();
		} else {
			fromToFrom.add(with);
			next = with.getTo();
		}
		List<Relation> nextToTo = next.getRelationsTo(to, withAllowedUncertain.withoutRelations(fromToFrom));
		if (nextToTo != null) {
			return true;
		}
		return false;
	}

	private void rebuildEstimations() {
		boolean needsRebuild = true;
		while (needsRebuild) {
			needsRebuild = false;
			for (LogicalRelation r : new ArrayList<LogicalRelation>(
					observationManager.getUnresolvedLogicalRelations())) {
				List<Estimation> est = getEstimationsForRelation(r);
				if (est.isEmpty()) {
					for (Observation o : new ArrayList<Observation>(observationManager.getObservations())) {
						needsRebuild |= removePartials(resolveRelationAndObservation(r, o));
					}
				}
			}
		}

	}

	private boolean removePartials(Estimation e) {
		if (e == null) {
			return false;
		}
		boolean ret = false;
		for (Estimation oe : new ArrayList<Estimation>(estimations)) {
			if (e == oe) {
				continue;
			}
			if (oe.getResolvedLogicalRelations().containsAll(e.getResolvedLogicalRelations())) {
				oe.remove();
				ret = true;
			}
		}
		return ret;
	}

	protected abstract Estimation buildEstimation(FramePoseObservation observation, Frame from, Frame to,
			List<Relation> relations, List<Relation> deps) throws TransformationException;

	private List<Relation> findPath(Frame from, Frame to, List<Relation> forbidden) {
		List<Relation> ret = from.getRelationsTo(to, topology.withoutRelations(forbidden));
		if (ret == null) {
			return ret;
		}
		for (Relation r : ret) {
			List<Relation> nf = new ArrayList<Relation>(forbidden);
			if (isUncertain(r)) {
				nf.add(r);
				List<Relation> nret = findPath(from, to, nf);
				if (nret != null) {
					return nret;
				}
				nf.remove(r);
			}
		}
		return ret;
	}

}
