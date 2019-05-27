/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.entity.Entity;

public class WeakEntityMap<V> {

	final Map<WeakEntityReference, V> entities = new HashMap<WeakEntityReference, V>();
	final ReferenceQueue<Entity> queue = new ReferenceQueue<Entity>();

	public List<V> cleanup() {
		List<V> objects = new ArrayList<V>();
		WeakEntityReference frameRef;

		while ((frameRef = (WeakEntityReference) queue.poll()) != null) {
			V object = entities.remove(frameRef);
			if (object != null) {
				objects.add(object);
			}
		}
		return objects;
	}

	public void clear() {
		entities.clear();
	}

	public boolean containsKey(Object key) {
		if (key instanceof Entity) {
			Entity frame = (Entity) key;
			return entities.containsKey(new WeakEntityReference(frame, queue));
		}
		return false;
	}

	public boolean containsValue(Object value) {
		return entities.containsValue(value);
	}

	public V get(Object key) {
		if (key instanceof Entity) {
			Entity frame = (Entity) key;
			return entities.get(new WeakEntityReference(frame, queue));
		}
		return null;
	}

	public boolean isEmpty() {
		return entities.isEmpty();
	}

	public V put(Entity key, V value) {
		return entities.put(new WeakEntityReference(key, queue), value);
	}

	public V remove(Object key) {
		if (key instanceof Entity) {
			Entity frame = (Entity) key;
			return entities.remove(new WeakEntityReference(frame, queue));
		}
		return null;
	}

	public int size() {
		return entities.size();
	}

	public Collection<V> values() {
		return entities.values();
	}

	static class WeakEntityReference extends WeakReference<Entity> {

		int hash;

		public WeakEntityReference(Entity f, ReferenceQueue<Entity> q) {
			super(f, q);
			hash = f.hashCode();
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof WeakEntityReference)) {
				return false;
			}

			Entity t = this.get();
			Entity u = ((WeakEntityReference) o).get();

			if ((t == null) || (u == null)) {
				return false;
			}
			if (t == u) {
				return true;
			}
			return t.equals(u);
		}
	}

}
