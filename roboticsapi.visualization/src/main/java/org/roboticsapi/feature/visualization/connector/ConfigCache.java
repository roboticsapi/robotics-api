/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.feature.visualization.RapiInfo;

public class ConfigCache {

	private final long maxAge;
	private long currentTimestamp;
	private final List<RapiInfo> cache = new ArrayList<>();

	public ConfigCache(RapiInfo[] infos, long maxAge) {
		this.maxAge = maxAge;
		this.currentTimestamp = System.currentTimeMillis();
		if (infos == null) {
			return;
		}
		for (RapiInfo info : infos) {
			cache.add(info.withNewAge(Math.max(0, info.age)));
		}
	}

	public synchronized RapiInfo[] getCurrent() {
		long timestamp = System.currentTimeMillis();
		updateTimestampsAndClearOld(timestamp);
		return cache.toArray(new RapiInfo[cache.size()]);
	}

	private void updateTimestampsAndClearOld(long timestamp) {
		List<RapiInfo> _c = new ArrayList<>(cache);
		cache.clear();
		long age = timestamp - currentTimestamp;
		currentTimestamp = timestamp;
		for (RapiInfo entry : _c) {
			long newAge = entry.age + age;
			if (newAge < maxAge)
				cache.add(entry.withNewAge(newAge));
		}
	}
}