/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.roboticsapi.core.RoboticsObject.RelationshipListener;
import org.roboticsapi.core.util.HashCodeUtil;

public interface RelationshipView<T extends Relationship> {

	public static class PreferringRelationshipView<T extends Relationship> extends DerivedRelationshipView<T> {
		private final Class<? extends T> dislikedType;
		private final Class<? extends T> preferredType;

		private PreferringRelationshipView(RelationshipView<T> innerView, Class<? extends T> dislikedType,
				Class<? extends T> preferredType) {
			super(innerView);
			this.dislikedType = dislikedType;
			this.preferredType = preferredType;
		}

		@Override
		public List<T> getRelationships(RoboticsObject o) {
			List<T> ret = new ArrayList<T>();
			List<T> disliked = new ArrayList<T>();
			for (T elem : super.getRelationships(o)) {
				if (dislikedType.isAssignableFrom(elem.getClass())) {
					disliked.add(elem);
				} else {
					ret.add(elem);
				}
			}
			ret.addAll(disliked);
			return ret;
		}

		class PreferringRelationshipListener<RR extends Relationship> implements RelationshipListener<RR> {
			private final RelationshipListener<RR> inner;
			private final List<RR> disliked = new ArrayList<>();

			public PreferringRelationshipListener(RelationshipListener<RR> inner) {
				this.inner = inner;
			}

			@Override
			public boolean equals(Object obj) {
				return obj instanceof PreferringRelationshipListener
						&& PreferringRelationshipListener.class.cast(obj).inner.equals(inner);
			}

			@Override
			public int hashCode() {
				return inner.hashCode();
			}

			@Override
			public void onAdded(RR r) {
				if (preferredType.isAssignableFrom(r.getClass())) {
					if (!disliked.isEmpty()) {
						disliked.forEach(inner::onRemoved);
						inner.onAdded(r);
						disliked.forEach(inner::onAdded);
						return;
					}
				} else if (dislikedType.isAssignableFrom(r.getClass())) {
					disliked.add(r);
				}
				inner.onAdded(r);
			}

			@Override
			public void onRemoved(RR r) {
				if (dislikedType.isAssignableFrom(r.getClass())) {
					disliked.remove(r);
				}
				inner.onRemoved(r);
			}

		}

		@Override
		public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
			innerView.addRelationshipListener(o, new PreferringRelationshipListener<T>(listener));
		}

		@Override
		public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
			innerView.removeRelationshipListener(o, new PreferringRelationshipListener<T>(listener));
		}
	}

	/**
	 * Specialized {@link RelationshipListener} which filters {@link Relationship}s
	 * according to a given {@link java.util.function.Predicate}.
	 *
	 * @param <T> Type of {@link Relationship}
	 */
	public static class FilteredRelationshipListener<T extends Relationship> implements RelationshipListener<T> {

		private final RelationshipListener<T> innerListener;
		private final java.util.function.Predicate<T> predicate;

		/**
		 * Constructor
		 *
		 * @param innerListener The inner listener.
		 * @param predicate     Filter predicate. If the predicate evaluates to
		 *                      <code>true</code>, the according {@link Relationship}
		 *                      will be forwarded to the inner listener.
		 */
		public FilteredRelationshipListener(RelationshipListener<T> innerListener,
				java.util.function.Predicate<T> predicate) {
			this.innerListener = innerListener;
			this.predicate = predicate;
		}

		@Override
		public void onAdded(T relationship) {
			if (predicate.test(relationship)) {
				innerListener.onAdded(relationship);
			}
		}

		@Override
		public void onRemoved(T relationship) {
			if (predicate.test(relationship)) {
				innerListener.onRemoved(relationship);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof FilteredRelationshipView.FilteredRelationshipListener) {
				return innerListener
						.equals(((FilteredRelationshipView.FilteredRelationshipListener<?>) o).innerListener);
			}
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, getClass());
			return HashCodeUtil.hash(hashCode, innerListener.hashCode());
		}
	}

	public static class SpecializedRelationshipView<T extends Relationship, U extends T>
			implements RelationshipView<U> {
		private final Class<U> type;
		private final RelationshipView<T> innerView;

		private SpecializedRelationshipView(Class<U> type, RelationshipView<T> innerView) {
			this.type = type;
			this.innerView = innerView;
		}

		@Override
		public List<U> getRelationships(RoboticsObject o) {
			return innerView.getRelationships(o, type);
		}

		@Override
		public Class<U> getRelationshipType() {
			return type;
		}

		@Override
		public void addRelationshipListener(RoboticsObject o, RelationshipListener<U> listener) {
			innerView.addRelationshipListener(o, new SpecializedRelationshipListener(listener));
		}

		@Override
		public void removeRelationshipListener(RoboticsObject o, RelationshipListener<U> listener) {
			innerView.removeRelationshipListener(o, new SpecializedRelationshipListener(listener));
		}

		private class SpecializedRelationshipListener implements RelationshipListener<T> {

			private final RelationshipListener<U> innerListener;

			public SpecializedRelationshipListener(RelationshipListener<U> innerListener) {
				this.innerListener = innerListener;
			}

			@Override
			public void onAdded(T relationship) {
				if (getRelationshipType().isAssignableFrom(relationship.getClass())) {
					innerListener.onAdded(getRelationshipType().cast(relationship));
				}
			}

			@Override
			public void onRemoved(T relationship) {
				if (getRelationshipType().isAssignableFrom(relationship.getClass())) {
					innerListener.onRemoved(getRelationshipType().cast(relationship));
				}
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof SpecializedRelationshipView.SpecializedRelationshipListener) {
					return innerListener.equals(
							((SpecializedRelationshipView<?, ?>.SpecializedRelationshipListener) o).innerListener);
				}
				return super.equals(o);
			}

			@Override
			public int hashCode() {
				int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, getClass());
				return HashCodeUtil.hash(hashCode, innerListener.hashCode());
			}
		}
	}

	public static abstract class DerivedRelationshipView<T extends Relationship> implements RelationshipView<T> {

		protected final RelationshipView<T> innerView;

		public DerivedRelationshipView(RelationshipView<T> innerView) {
			this.innerView = innerView;
		}

		@Override
		public List<T> getRelationships(RoboticsObject o) {
			return innerView.getRelationships(o);
		}

		@Override
		public Class<T> getRelationshipType() {
			return innerView.getRelationshipType();
		}

	}

	public static class FilteredRelationshipView<T extends Relationship> extends DerivedRelationshipView<T> {

		private final java.util.function.Predicate<T> predicate;

		public FilteredRelationshipView(RelationshipView<T> innerView, java.util.function.Predicate<T> predicate) {
			super(innerView);
			this.predicate = predicate;
		}

		@Override
		public List<T> getRelationships(RoboticsObject o) {
			List<T> relationships = super.getRelationships(o);

			return relationships.stream().filter(predicate).collect(Collectors.toList());
		}

		@Override
		public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
			innerView.addRelationshipListener(o, new FilteredRelationshipListener<T>(listener, this.predicate));
		}

		@Override
		public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
			innerView.removeRelationshipListener(o, new FilteredRelationshipListener<T>(listener, this.predicate));
		}

	}

	public static RelationshipView<Relationship> getUnmodified() {
		return get(RelationshipChangeSet.getUnmodified());
	}

	public static <T extends Relationship> RelationshipView<T> getUnmodified(Class<T> type) {
		return get(RelationshipChangeSet.getUnmodified()).specializingInRelationships(type);
	}

	public static RelationshipView<Relationship> get(final RelationshipChangeSet changeset) {
		return new RelationshipView<Relationship>() {
			@Override
			public List<Relationship> getRelationships(RoboticsObject o) {
				return changeset.getRelationships(o);
			}

			@Override
			public Class<Relationship> getRelationshipType() {
				return Relationship.class;
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				changeset.addRelationshipListener(o, listener);
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<Relationship> listener) {
				changeset.removeRelationshipListener(o, listener);
			}
		};

	}

	/**
	 * Returns a {@link List} with {@link Relationship}s of a given
	 * {@link RoboticsObject} that are available in the underlying
	 * {@link RelationshipView}.
	 *
	 * @param o The {@link RoboticsObject} to query
	 * @return a {@link List} with available {@link Relationship}s
	 */
	public List<T> getRelationships(RoboticsObject o);

	/**
	 * Returns the type of shown {@link Relationship}s.
	 *
	 * @return the corresponding {@link Class} object.
	 */
	public Class<T> getRelationshipType();

	public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener);

	public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener);

	public default <U extends T> List<U> getRelationships(RoboticsObject o, Class<U> type) {
		List<U> ret = new ArrayList<U>();
		for (T relationship : getRelationships(o)) {
			if (type.isAssignableFrom(relationship.getClass())) {
				ret.add(type.cast(relationship));
			}
		}
		return ret;
	}

	public default <U extends T> RelationshipView<U> specializingInRelationships(final Class<U> type) {
		return new SpecializedRelationshipView<T, U>(type, this);
	}

	@SuppressWarnings("unchecked")
	public default RelationshipView<T> hidingRelationships(final T... relationships) {
		final List<T> list = Arrays.asList(relationships);
		return new FilteredRelationshipView<T>(this, r -> !list.contains(r));
		// @Override
		// public List<T> getRelationships(RoboticsObject o) {
		// List<T> ret = RelationshipView.this.getRelationships(o);
		// for (T relationship : relationships) {
		// ret.remove(relationship);
		// }
		// return ret;
		// }
	}

	/**
	 * Hides all {@link Relationship}s the given filter predicate applies to
	 *
	 * @param filter decides which Relationships to hide
	 * @return {@link RelationshipView} without the hidden relations.
	 */
	public default RelationshipView<T> hidingRelationships(final java.util.function.Predicate<T> filter) {
		return new FilteredRelationshipView<T>(this, r -> !filter.test(r));
	}

	public default RelationshipView<T> preferringRelationships(final Class<? extends T> preferredType,
			Class<? extends T> dislikedType) {
		return new PreferringRelationshipView<T>(this, dislikedType, preferredType);
	}

	public default RelationshipView<T> addingRelationships(List<T> syntheticRelations) {
		return new DerivedRelationshipView<T>(this) {
			@Override
			public List<T> getRelationships(RoboticsObject o) {
				List<T> ret = new ArrayList<>(super.getRelationships(o));
				for (T synthetic : syntheticRelations) {
					if (synthetic.getFrom() == o || synthetic.getTo() == o) {
						ret.add(synthetic);
					}
				}
				return ret;
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
				innerView.addRelationshipListener(o, listener);
				for (T synthetic : syntheticRelations) {
					if (synthetic.getFrom() == o || synthetic.getTo() == o) {
						listener.onAdded(synthetic);
					}
				}
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
				for (T synthetic : syntheticRelations) {
					if (synthetic.getFrom() == o || synthetic.getTo() == o) {
						listener.onRemoved(synthetic);
					}
				}
				innerView.removeRelationshipListener(o, listener);
			}
		};
	}

	/**
	 * Creates a lazy snapshot of the {@link RelationshipView}, i.e. a copy that
	 * will not change. The resulting {@link RelationshipView} is well-formed (each
	 * known {@link Relationship} will be provided for both the
	 * {@link Relationship#getFrom()} and the {@link Relationship#getTo()} object),
	 * but not necessarily temporally consistent (it may contain some
	 * {@link Relationship}s added later and not contain others that were added
	 * earlier).
	 *
	 * @return a lazy snapshot {@link RelationshipView}
	 */
	public default RelationshipView<T> lazySnapshot() {
		return new DerivedRelationshipView<T>(this) {
			private final Set<RoboticsObject> completedObjects = new HashSet<>();
			private final Map<RoboticsObject, List<T>> knownRelationships = new HashMap<>();

			@Override
			public List<T> getRelationships(RoboticsObject o) {
				if (!completedObjects.contains(o)) {
					if (!knownRelationships.containsKey(o)) {
						knownRelationships.put(o, new ArrayList<>());
					}
					List<T> ret = knownRelationships.get(o);

					List<T> additional = super.getRelationships(o);
					for (T relation : additional) {
						RoboticsObject other = relation.getOther(o);
						if (completedObjects.contains(other)) {
							if (!knownRelationships.get(other).contains(relation)) {
								continue;
							} else {
								ret.add(relation);
							}
						} else {
							if (!knownRelationships.containsKey(other)) {
								knownRelationships.put(other, new ArrayList<>());
							}
							knownRelationships.get(other).add(relation);
							ret.add(relation);
						}
					}
					completedObjects.add(o);
				}
				return knownRelationships.get(o);
			}

			@Override
			public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
				for (T relation : getRelationships(o)) {
					listener.onAdded(relation);
				}
			}

			@Override
			public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
				for (T relation : getRelationships(o)) {
					listener.onRemoved(relation);
				}
			}
		};
	}

}
