/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.roboticsapi.core.entity.Entity;

public class WeakEntitySet<E extends Entity> extends AbstractSet<E> {

	private final Set<WeakEntry> set = new LinkedHashSet<WeakEntry>();
	private final ReferenceQueue<Object> queue = new ReferenceQueue<Object>();

	@Override
	public boolean add(E e) {
		expungeStaleEntries();
		return set.add(new WeakEntry(e));
	}

	@Override
	public void clear() {
		while (queue.poll() != null) {
		}
		set.clear();
		while (queue.poll() != null) {
		}
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<WeakEntry> iterator = set.iterator();

		return new Iterator<E>() {
			Object next;

			@Override
			public boolean hasNext() {
				try {
					while ((next = iterator.next().get()) == null) {
					}
					return true;
				} catch (NoSuchElementException e) {
					next = null;
					return false;
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public E next() {
				if (next == null) {
					throw new NoSuchElementException();
				}
				return (E) next;
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		expungeStaleEntries();
		return set.remove(new WeakEntry(o));
	}

	@Override
	public int size() {
		expungeStaleEntries();
		return set.size();
	}

	@SuppressWarnings("unchecked")
	private void expungeStaleEntries() {
		WeakEntry e;
		while ((e = (WeakEntry) queue.poll()) != null) {
			set.remove(e);
		}
	}

	private class WeakEntry extends WeakReference<Object> {

		private final int hash;

		public WeakEntry(Object o) {
			super(o, queue);
			hash = o.hashCode();
		}

		@Override
		@SuppressWarnings({ "rawtypes" })
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof WeakEntitySet.WeakEntry) {
				WeakEntitySet.WeakEntry e = (WeakEntitySet.WeakEntry) o;

				if (hash == e.hash) {
					Object o1 = get();
					Object o2 = e.get();

					return (o1 == o2 || (o1 != null && o1.equals(o2)));
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}

	}

}
