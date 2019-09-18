/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AndRealtimeBoolean extends RealtimeBoolean {

	private final List<RealtimeBoolean> values;

	AndRealtimeBoolean(Collection<RealtimeBoolean> values) {
		super(values.toArray(new RealtimeBoolean[values.size()]));

		if (values.isEmpty()) {
			throw new IllegalArgumentException("AndRealtimeBoolean requires at least one value!");
		}
		this.values = new ArrayList<>(values);
	}

	AndRealtimeBoolean(RealtimeBoolean... values) {
		this(Arrays.asList(values));
	}

	@Override
	public RealtimeBoolean and(RealtimeBoolean other) {
		if (CONSTANT_FOLDING && other.isConstant()) {
			return other.and(this);
		}
		Set<RealtimeBoolean> values = new HashSet<>();
		values.addAll(getInnerValues());

		if (other instanceof AndRealtimeBoolean) {
			values.addAll(((AndRealtimeBoolean) other).getInnerValues());
		} else {
			values.add(other);
		}
		return new AndRealtimeBoolean(values.stream()
				.sorted(Comparator.comparing(RealtimeBoolean::hashCode).thenComparing(System::identityHashCode))
				.collect(Collectors.toList()));
	}

	@Override
	public Boolean calculateCheapValue() {
		boolean value = true;

		for (RealtimeBoolean s : getInnerValues()) {
			Boolean cheapValue = s.getCheapValue();
			if (cheapValue == null) {
				return null;
			}
			value &= cheapValue;
		}

		return value;
	}

	public List<RealtimeBoolean> getInnerValues() {
		return values;
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(values);
	}

	@Override
	public String toString() {
		String ret = "";
		for (RealtimeBoolean v : values) {
			ret += v + " & ";
		}
		if (!values.isEmpty()) {
			ret = ret.substring(0, ret.length() - 3);
		}
		return "(" + ret + ")";
	}

}
