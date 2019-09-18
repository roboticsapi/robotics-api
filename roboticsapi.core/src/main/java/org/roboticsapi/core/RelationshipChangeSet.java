/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.roboticsapi.core.RelationshipView.FilteredRelationshipListener;
import org.roboticsapi.core.RoboticsObject.RelationshipListener;

public abstract class RelationshipChangeSet {

	public abstract static class DerivedRelationshipChangeSet extends RelationshipChangeSet {
		private final RelationshipChangeSet innerChangeSet;

		public DerivedRelationshipChangeSet(RelationshipChangeSet innerChangeSet) {
			this.innerChangeSet = innerChangeSet;
		}

		@Override
		public List<Relationship> getRelationships(RoboticsObject o) {
			return innerChangeSet.getRelationships(o);
		}

		@Override
		public void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
			innerChangeSet.addRelationshipListener(o, listener);
		}

		@Override
		public void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
			innerChangeSet.removeRelationshipListener(o, listener);
		}

		@Override
		public void apply() {
			innerChangeSet.apply();
		}
	}

	public static RelationshipChangeSet getUnmodified() {
		return new RelationshipChangeSet() {
			@Override
			public List<Relationship> getRelationships(RoboticsObject o) {
				return o.getRelationships(Relationship.class);
			}

			@Override
			public void apply() {
				// do nothing
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				o.addRelationshipListener(Relationship.class, listener);
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				o.removeRelationshipListener(Relationship.class, listener);
			}

		};
	}

	public RelationshipChangeSet() {
		super();
	}

	public abstract List<Relationship> getRelationships(RoboticsObject o);

	public <U extends Relationship> List<U> getRelationships(RoboticsObject o, Class<U> type) {
		List<U> ret = new ArrayList<U>();
		for (Relationship relationship : getRelationships(o)) {
			if (type.isAssignableFrom(relationship.getClass())) {
				ret.add(type.cast(relationship));
			}
		}
		return ret;
	}

	public abstract void apply();

	public abstract void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener);

	public abstract void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener);

	public RelationshipChangeSet adding(final Relationship... relationships) {
		for (Relationship relationship : relationships) {
			if (!relationship.canEstablish(this)) {
				throw new IllegalArgumentException("Relationship " + relationship + " cannot be established.");
			}
		}

		final List<Relationship> list = Arrays.asList(relationships);

		return new DerivedRelationshipChangeSet(this) {
			boolean applied = false;
			private final Predicate<Relationship> predicate = r -> applied || !list.contains(r);

			@Override
			public List<Relationship> getRelationships(RoboticsObject o) {
				List<Relationship> ret = super.getRelationships(o);

				// if this change set was already applied,
				// return the underlying list only
				if (applied) {
					return ret;
				}

				return Stream.concat(ret.stream(), list.stream().filter(r -> (r.getFrom() == o || r.getTo() == o)))
						.collect(Collectors.toList());
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				super.addRelationshipListener(o, new FilteredRelationshipListener<Relationship>(listener, predicate));

				if (!applied) {
					list.stream().filter(r -> (r.getFrom() == o || r.getTo() == o)).forEach(listener::onAdded);
				}
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				super.removeRelationshipListener(o,
						new FilteredRelationshipListener<Relationship>(listener, predicate));

				if (!applied) {
					list.stream().filter(r -> (r.getFrom() == o || r.getTo() == o)).forEach(listener::onRemoved);
				}
			}

			@Override
			public void apply() {
				if (applied) {
					return;
				}
				super.apply();
				list.stream().forEach(Relationship::establish);
				applied = true;
			}
		};
	}

	public RelationshipChangeSet removing(final Relationship... relationships) {
		for (Relationship relationship : relationships) {
			if (!relationship.canRemove(this)) {
				throw new IllegalArgumentException("Relationship " + relationship + " cannot be removed.");
			}
		}

		final List<Relationship> list = Arrays.asList(relationships);

		return new DerivedRelationshipChangeSet(this) {
			boolean applied = false;

			@Override
			public List<Relationship> getRelationships(RoboticsObject o) {
				List<Relationship> ret = super.getRelationships(o);

				// if this change set was already applied,
				// return the underlying list only
				if (applied) {
					return ret;
				}
				return ret.stream().filter(r -> !list.contains(r)).collect(Collectors.toList());
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				super.addRelationshipListener(o,
						new FilteredRelationshipListener<Relationship>(listener, r -> applied || !list.contains(r)));
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				super.removeRelationshipListener(o,
						new FilteredRelationshipListener<Relationship>(listener, r -> applied || !list.contains(r)));
			}

			@Override
			public void apply() {
				if (applied) {
					return;
				}
				super.apply();
				for (Relationship relationship : relationships) {
					relationship.remove();
				}
				applied = true;
			}
		};
	}

}