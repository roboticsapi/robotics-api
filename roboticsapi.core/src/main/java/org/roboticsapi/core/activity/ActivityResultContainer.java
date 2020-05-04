/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;

public class ActivityResultContainer implements ActivityResults {

	private List<ActivityResult> results = new ArrayList<>();
	private List<Consumer<ActivityResult>> consumers = new ArrayList<>();
	private boolean completed = false;

	public ActivityResultContainer() {
	}

	public ActivityResultContainer(ActivityResult... results) {
		for (ActivityResult result : results)
			addResult(result);
		addResult(null);
	}

	public synchronized void provide(RoboticsConsumer<ActivityResult> consumer, Consumer<RoboticsException> error) {
		provide(r -> {
			try {
				consumer.accept(r);
			} catch (RoboticsException e) {
				error.accept(e);
			}
		});
	}

	public synchronized void provide(Consumer<ActivityResult> consumer) {
		consumers.add(consumer);
		for (ActivityResult result : results)
			consumer.accept(result);
		if (completed)
			consumer.accept(null);
	}

	public synchronized void addResult(ActivityResult result) {
		if (completed)
			throw new IllegalStateException();
		if (result == null)
			completed = true;
		results.add(result);
		for (Consumer<ActivityResult> consumer : consumers)
			consumer.accept(result);
	}

	public ActivityResults cross(ActivityResults other) {
		List<ActivityResult> results1 = new ArrayList<>(), results2 = new ArrayList<>();
		ActivityResultContainer ret = new ActivityResultContainer();
		this.provide(r1 -> {
			results1.add(r1);
			if (r1 == null) {
				if (results2.contains(null))
					ret.addResult(null);
				return;
			}
			for (ActivityResult r2 : results2)
				if (r2 != null)
					ret.addResult(r1.and(r2));
		});
		other.provide(r2 -> {
			results2.add(r2);
			if (r2 == null) {
				if (results1.contains(null))
					ret.addResult(null);
				return;
			}
			for (ActivityResult r1 : results1)
				if (r1 != null)
					ret.addResult(r1.and(r2));
		});
		return ret;
	}

	public ActivityResults union(ActivityResults other) {
		List<ActivityResult> results1 = new ArrayList<>(), results2 = new ArrayList<>();
		ActivityResultContainer ret = new ActivityResultContainer();
		this.provide(r1 -> {
			results1.add(r1);
			if (r1 == null && !results2.contains(null))
				return;
			else
				ret.addResult(r1);
		});
		other.provide(r2 -> {
			results2.add(r2);
			if (r2 == null && !results1.contains(null))
				return;
			else
				ret.addResult(r2);
		});
		return ret;
	}

	public ActivityResults without(ActivityResult result) {
		ActivityResultContainer ret = new ActivityResultContainer();
		this.provide(r -> {
			if (r != result)
				ret.addResult(r);
		});
		return ret;
	}

	@Override
	public ActivityResults withMetadataFor(Set<Device> devices) {
		ActivityResultContainer ret = new ActivityResultContainer();
		this.provide(r -> ret.addResult(r.withMetadataFor(devices)));
		return ret;
	}

}
