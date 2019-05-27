/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.netoptimizer.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime.CommandMappingHook;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.netoptimizer.NetOptimizer;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.Primitive;
import org.roboticsapi.runtime.rpi.RPIException;

public class NetOptimizerExtension implements RoboticsObjectListener, CommandMappingHook {

	private static boolean STATS = false;

	// private static final boolean STATS = true;

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof AbstractMapperRuntime) {
			((AbstractMapperRuntime) object).addCommandMappingHook(this);
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		if (object instanceof AbstractMapperRuntime) {
			((AbstractMapperRuntime) object).removeCommandMappingHook(this);
		}
	}

	@Override
	public void netFragmentHook(NetFragment netFragment) {
	}

	@Override
	public void netHook(Fragment net) {
		try {
			int prev = countPrimitives(net);
			NetOptimizer.optimize(net);

			if (!STATS) {
				return;
			}

			HashMap<String, Integer> hist = new HashMap<String, Integer>();

			for (Primitive p : listPrimitives(net)) {
				if (hist.containsKey(p.getType())) {
					hist.put(p.getType(), hist.get(p.getType()) + 1);
				} else {
					hist.put(p.getType(), 1);
				}
			}

			List<Map.Entry<String, Integer>> histList = new ArrayList<Map.Entry<String, Integer>>();

			for (Map.Entry<String, Integer> key : hist.entrySet()) {
				histList.add(key);
			}

			Collections.sort(histList, new Comparator<Map.Entry<String, Integer>>() {

				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o2.getValue() - o1.getValue();
				}

			});

			StringBuffer buffer = new StringBuffer();

			for (Map.Entry<String, Integer> e : histList) {
				buffer.append(e.getValue()).append("\t").append(e.getKey()).append("\n");
			}

			System.out.println("NetOptimizer: " + prev + " -> " + countPrimitives(net) + "\n" + buffer.toString());
		} catch (RPIException e) {
			e.printStackTrace();
		}
	}

	public List<Primitive> listPrimitives(Fragment net) {
		List<Primitive> ret = new ArrayList<Primitive>();
		for (Primitive prim : net.getPrimitives()) {
			ret.add(prim);
			if (prim instanceof Fragment) {
				ret.addAll(listPrimitives((Fragment) prim));
			}
		}
		return ret;
	}

	public int countPrimitives(Fragment net) {
		int count = 0;
		for (Primitive prim : net.getPrimitives()) {
			if (prim instanceof Fragment) {
				count += countPrimitives((Fragment) prim);
			} else {
				count++;
			}
		}
		return count;
	}
}
