/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public class MapperRegistry implements RealtimeValueSource {

	// realtime values
	private List<RealtimeValueFragmentFactory> ff = new ArrayList<RealtimeValueFragmentFactory>();
	private List<RealtimeValueAliasFactory> af = new ArrayList<RealtimeValueAliasFactory>();

	public List<RealtimeValueFragmentFactory> getRealtimeValueFragmentFactories() {
		return ff;
	}

	public List<RealtimeValueAliasFactory> getRealtimeValueAliasFactories() {
		return af;
	}

	public void registerRealtimeValueMapper(RealtimeValueFragmentFactory factory) {
		ff.add(factory);
	}

	public void registerRealtimeValueMapper(RealtimeValueAliasFactory factory) {
		af.add(factory);
	}

	// action mappers
	private Map<Class<? extends Action>, List<ActionMapper<? extends Action>>> am = new HashMap<Class<? extends Action>, List<ActionMapper<? extends Action>>>();

	public <A extends Action> void registerActionMapper(Class<? extends A> action, ActionMapper<A> mapper) {
		if (!am.containsKey(action)) {
			am.put(action, new ArrayList<ActionMapper<? extends Action>>());
		}
		am.get(action).add(mapper);
	}

	public ActionResult mapAction(Action action, DeviceParameterBag parameters, RealtimeBoolean cancel,
			RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> actionPlans)
			throws MappingException {
		return mapAction(action.getClass(), action, parameters, cancel, override, time, actionPlans);

	}

	@SuppressWarnings("unchecked")
	private <A extends Action> ActionResult mapAction(Class<A> aClass, Action action, DeviceParameterBag parameters,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time,
			Map<PlannedAction<?>, Plan> actionPlans) throws MappingException {

		List<ActionMapper<? extends Action>> mappers = flatten(collect(am, action));

		for (ActionMapper<? extends Action> mapper : mappers) {
			ActionMapper<? super A> typedMapper = (ActionMapper<? super A>) mapper;
			try {
				ActionResult ret = typedMapper.map((A) action, parameters, this, cancel, override, time, actionPlans);
				if (ret != null) {
					return ret;
				}
			} catch (RpiException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable action mapper found for " + action.getClass().getSimpleName());
	}

	// driver mappers
	Map<Class<? extends ActuatorDriver>, Map<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends ActuatorDriver, ? extends ActionResult>>>> dm = new HashMap<Class<? extends ActuatorDriver>, Map<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends ActuatorDriver, ? extends ActionResult>>>>();

	public <AD extends ActuatorDriver, AR extends ActionResult> void registerActuatorDriverMapper(
			Class<? extends AD> actuatorDriver, Class<? extends AR> actionResult, ActuatorDriverMapper<AD, AR> mapper) {
		if (!dm.containsKey(actuatorDriver)) {
			dm.put(actuatorDriver,
					new HashMap<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends ActuatorDriver, ? extends ActionResult>>>());
		}
		if (!dm.get(actuatorDriver).containsKey(actionResult)) {
			dm.get(actuatorDriver).put(actionResult,
					new ArrayList<ActuatorDriverMapper<? extends ActuatorDriver, ? extends ActionResult>>());
		}
		dm.get(actuatorDriver).get(actionResult).add(mapper);
	}

	public RealtimeValueConsumerFragment mapDriver(ActuatorDriver actuatorDriver, ActionResult actionResult,
			DeviceParameterBag parameters, RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException {

		return mapDriver(actuatorDriver.getClass(), actionResult.getClass(), actuatorDriver, actionResult, parameters,
				cancel, override, time);
	}

	@SuppressWarnings("unchecked")
	private <AD extends ActuatorDriver, AR extends ActionResult> RealtimeValueConsumerFragment mapDriver(
			Class<AD> adClass, Class<AR> arClass, ActuatorDriver actuatorDriver, ActionResult actionResult,
			DeviceParameterBag parameters, RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time)
			throws MappingException {

		for (ActuatorDriverMapper<? extends ActuatorDriver, ? extends ActionResult> mapper : flatten(
				collectAll(collect(dm, actuatorDriver), actionResult))) {
			ActuatorDriverMapper<? super AD, ? super AR> typedMapper = ((ActuatorDriverMapper<? super AD, ? super AR>) mapper);
			try {
				RealtimeValueConsumerFragment ret = typedMapper.map((AD) actuatorDriver, (AR) actionResult, parameters,
						this, cancel, override, time);
				if (ret != null) {
					return ret;
				}
			} catch (RpiException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable actuator driver mapper found for "
				+ actuatorDriver.getClass().getSimpleName() + " and " + actionResult.getClass().getSimpleName());
	}

	// helper methods
	private <T> List<T> flatten(List<List<T>> lists) {
		List<T> ret = new ArrayList<T>();
		for (List<T> list : lists) {
			ret.addAll(list);
		}
		return ret;
	}

	private <T, U> List<U> collectAll(List<Map<Class<? extends T>, U>> maps, T key) {
		List<U> ret = new ArrayList<U>();
		for (Map<Class<? extends T>, U> map : maps) {
			ret.addAll(collect(map, key));
		}
		return ret;
	}

	private <T, U> List<U> collect(Map<Class<? extends T>, U> map, T key) {
		List<Map.Entry<Class<? extends T>, U>> entries = new ArrayList<Map.Entry<Class<? extends T>, U>>();
		for (Map.Entry<Class<? extends T>, U> entry : map.entrySet()) {
			if (entry.getKey().isAssignableFrom(key.getClass())) {
				entries.add(entry);
			}
		}
		Collections.sort(entries, new Comparator<Map.Entry<Class<? extends T>, U>>() {
			@Override
			public int compare(Entry<Class<? extends T>, U> o1, Entry<Class<? extends T>, U> o2) {
				Class<?> o1Type = o1.getKey();
				Class<?> o2Type = o2.getKey();
				if (o1Type == o2Type) {
					return 0;
				}
				if (o1Type.isAssignableFrom(o2Type)) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		List<U> ret = new ArrayList<U>();
		for (Map.Entry<Class<? extends T>, U> entry : entries) {
			ret.add(entry.getValue());
		}
		return ret;
	}

}
