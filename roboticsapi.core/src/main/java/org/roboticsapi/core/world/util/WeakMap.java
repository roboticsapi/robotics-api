/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeakMap<V, K> {

	final Map<WeakObjectReference, V> entities = new HashMap<>();
	final ReferenceQueue<K> queue = new ReferenceQueue<>();

	public List<V> cleanup() {
		List<V> objects = new ArrayList<V>();
		WeakObjectReference frameRef;

		while ((frameRef = (WeakObjectReference) queue.poll()) != null) {
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

	public boolean containsKey(K key) {
		return entities.containsKey(new WeakObjectReference(key, queue));
	}

	public boolean containsValue(Object value) {
		return entities.containsValue(value);
	}

	public V get(K key) {
		return entities.get(new WeakObjectReference(key, queue));
	}

	public boolean isEmpty() {
		return entities.isEmpty();
	}

	public V put(K key, V value) {
		return entities.put(new WeakObjectReference(key, queue), value);
	}

	public V remove(K key) {
		return entities.remove(new WeakObjectReference(key, queue));
	}

	public int size() {
		return entities.size();
	}

	public Collection<V> values() {
		return entities.values();
	}

	class WeakObjectReference extends WeakReference<K> {

		int hash;

		public WeakObjectReference(K f, ReferenceQueue<K> q) {
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
			if (!(o instanceof WeakMap.WeakObjectReference)) {
				return false;
			}

			K t = this.get();
			Object u = ((WeakMap<V, K>.WeakObjectReference) o).get();

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
