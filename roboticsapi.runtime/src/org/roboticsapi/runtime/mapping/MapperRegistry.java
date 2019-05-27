/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.CommandMapper;
import org.roboticsapi.runtime.mapping.parts.CommandMappingPorts;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.EventEffectMapper;
import org.roboticsapi.runtime.mapping.parts.EventHandlerMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.parts.TransactionMapper;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.CommandMapperResult;
import org.roboticsapi.runtime.mapping.result.EventHandlerMapperResult;
import org.roboticsapi.runtime.mapping.result.MapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.TransactionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class MapperRegistry {

	// ActuatorRuntimeAdapter mappers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends ActuatorDriver>, Map<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>>>>> actuatorDriverMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends ActuatorDriver>, Map<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>>>>>();

	public <R extends RoboticsRuntime, AD extends ActuatorDriver, AR extends ActionResult> void registerActuatorDriverMapper(
			Class<? extends R> runtime, Class<? extends AD> actuatorDriver, Class<? extends AR> actionResult,
			ActuatorDriverMapper<R, AD, AR> mapper) {
		if (!actuatorDriverMappers.containsKey(runtime)) {
			actuatorDriverMappers.put(runtime,
					new HashMap<Class<? extends ActuatorDriver>, Map<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>>>>());
		}
		if (!actuatorDriverMappers.get(runtime).containsKey(actuatorDriver)) {
			actuatorDriverMappers.get(runtime).put(actuatorDriver,
					new HashMap<Class<? extends ActionResult>, List<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>>>());
		}
		if (!actuatorDriverMappers.get(runtime).get(actuatorDriver).containsKey(actionResult)) {
			actuatorDriverMappers.get(runtime).get(actuatorDriver).put(actionResult,
					new ArrayList<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>>());
		}
		actuatorDriverMappers.get(runtime).get(actuatorDriver).get(actionResult).add(mapper);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ActuatorDriverMapperResult mapActuatorDriver(RoboticsRuntime runtime, ActuatorDriver actuatorDriver,
			ActionResult actionResult, DeviceParameterBag parameters, DataflowOutPort cancel, DataflowOutPort override)
			throws MappingException {

		List<ActuatorDriverMapper<? extends RoboticsRuntime, ? extends ActuatorDriver, ? extends ActionResult>> mappers = flatten(
				collectAll(collectAll(collect(actuatorDriverMappers, runtime), actuatorDriver), actionResult));

		for (ActuatorDriverMapper mapper : mappers) {
			try {
				ActuatorDriverMapperResult ret = mapper.map(runtime, actuatorDriver, actionResult, parameters,
						new DeviceMappingPorts(cancel, override));
				if (ret != null) {
					return ret;
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable actuator driver mapper found for "
				+ actuatorDriver.getClass().getSimpleName() + " and " + actionResult.getClass().getSimpleName());
	}

	// action mappers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends Action>, List<ActionMapper<? extends RoboticsRuntime, ? extends Action>>>> actionMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends Action>, List<ActionMapper<? extends RoboticsRuntime, ? extends Action>>>>();

	public <R extends RoboticsRuntime, A extends Action> void registerActionMapper(Class<? extends R> runtime,
			Class<? extends A> action, ActionMapper<R, A> mapper) {
		if (!actionMappers.containsKey(runtime)) {
			actionMappers.put(runtime,
					new HashMap<Class<? extends Action>, List<ActionMapper<? extends RoboticsRuntime, ? extends Action>>>());
		}
		if (!actionMappers.get(runtime).containsKey(action)) {
			actionMappers.get(runtime).put(action,
					new ArrayList<ActionMapper<? extends RoboticsRuntime, ? extends Action>>());
		}
		actionMappers.get(runtime).get(action).add(mapper);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ActionMapperResult mapAction(RoboticsRuntime runtime, Action action, DeviceParameterBag parameters,
			DataflowOutPort cancel, DataflowOutPort override, Map<PlannedAction<?>, Plan> actionPlans)
			throws MappingException {

		List<ActionMapper<? extends RoboticsRuntime, ? extends Action>> mappers = flatten(
				collectAll(collect(actionMappers, runtime), action));

		for (ActionMapper mapper : mappers) {
			try {
				ActionMapperResult ret = mapper.map(runtime, action, parameters,
						new ActionMappingContext(cancel, override, actionPlans));
				if (ret != null) {
					return ret;
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable action mapper found for " + action.getClass().getSimpleName());
	}

	// sensor mappers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends Sensor<?>>, List<SensorMapper<? extends RoboticsRuntime, ?, ? extends Sensor<?>>>>> sensorMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends Sensor<?>>, List<SensorMapper<? extends RoboticsRuntime, ?, ? extends Sensor<?>>>>>();

	public <R extends RoboticsRuntime, T, S extends Sensor<T>> void registerSensorMapper(Class<? extends R> runtime,
			Class<? extends S> sensor, SensorMapper<R, T, S> mapper) {
		if (!sensorMappers.containsKey(runtime)) {
			sensorMappers.put(runtime,
					new HashMap<Class<? extends Sensor<?>>, List<SensorMapper<? extends RoboticsRuntime, ?, ? extends Sensor<?>>>>());
		}
		if (!sensorMappers.get(runtime).containsKey(sensor)) {
			sensorMappers.get(runtime).put(sensor,
					new ArrayList<SensorMapper<? extends RoboticsRuntime, ?, ? extends Sensor<?>>>());
		}
		sensorMappers.get(runtime).get(sensor).add(mapper);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> SensorMapperResult<T> mapSensor(RoboticsRuntime runtime, Sensor<T> sensor, NetFragment fragment,
			SensorMappingContext context) throws MappingException {
		if (context == null) {
			context = new SensorMappingContext();
		}

		if (context.getSensorResult(sensor) != null) {
			return context.getSensorResult(sensor);
		}
		List<SensorMapper<? extends RoboticsRuntime, ?, ? extends Sensor<?>>> mappers = flatten(
				collectAll(collect(sensorMappers, runtime), sensor));

		for (SensorMapper mapper : mappers) {
			SensorMapperResult<T> ret = mapper.map(runtime, sensor, new SensorMappingPorts(), context);
			if (ret != null) {
				if (fragment != null) {
					fragment.add(ret.getNetFragment());
				}
				context.addSensorResult(sensor, ret);
				return ret;
			}
		}

		throw new MappingException("No suitable sensor mapper found for " + sensor.getClass().getSimpleName());
	}

	// command mappers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends Command>, List<CommandMapper<? extends RoboticsRuntime, ? extends Command>>>> commandMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends Command>, List<CommandMapper<? extends RoboticsRuntime, ? extends Command>>>>();

	public <R extends RoboticsRuntime, C extends Command> void registerCommandMapper(Class<? extends R> runtime,
			Class<? extends C> command, CommandMapper<R, C> mapper) {
		if (!commandMappers.containsKey(runtime)) {
			commandMappers.put(runtime,
					new HashMap<Class<? extends Command>, List<CommandMapper<? extends RoboticsRuntime, ? extends Command>>>());
		}
		if (!commandMappers.get(runtime).containsKey(command)) {
			commandMappers.get(runtime).put(command,
					new ArrayList<CommandMapper<? extends RoboticsRuntime, ? extends Command>>());
		}
		commandMappers.get(runtime).get(command).add(mapper);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CommandMapperResult mapCommand(RoboticsRuntime runtime, Command command, DataflowOutPort start,
			DataflowOutPort stop, DataflowOutPort cancel, DataflowOutPort override) throws MappingException {

		List<CommandMapper<? extends RoboticsRuntime, ? extends Command>> mappers = flatten(
				collectAll(collect(commandMappers, runtime), command));

		for (CommandMapper mapper : mappers) {
			try {
				CommandMapperResult ret = mapper.map(runtime, command,
						new CommandMappingPorts(start, stop, cancel, override));
				if (ret != null) {
					return ret;
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable command mapper found for " + command.getClass().getSimpleName());
	}

	// transaction mappers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends Command>, List<TransactionMapper<? extends RoboticsRuntime, ? extends Command>>>> transactionMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends Command>, List<TransactionMapper<? extends RoboticsRuntime, ? extends Command>>>>();

	public <R extends RoboticsRuntime, C extends Command> void registerTransactionMapper(Class<? extends R> runtime,
			Class<? extends C> command, TransactionMapper<R, C> mapper) {
		if (!transactionMappers.containsKey(runtime)) {
			transactionMappers.put(runtime,
					new HashMap<Class<? extends Command>, List<TransactionMapper<? extends RoboticsRuntime, ? extends Command>>>());
		}
		if (!transactionMappers.get(runtime).containsKey(command)) {
			transactionMappers.get(runtime).put(command,
					new ArrayList<TransactionMapper<? extends RoboticsRuntime, ? extends Command>>());
		}
		transactionMappers.get(runtime).get(command).add(mapper);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TransactionMapperResult mapTransaction(RoboticsRuntime runtime, Command command) throws MappingException {

		List<TransactionMapper<? extends RoboticsRuntime, ? extends Command>> mappers = flatten(
				collectAll(collect(transactionMappers, runtime), command));

		for (TransactionMapper mapper : mappers) {
			try {
				TransactionMapperResult ret = mapper.map(runtime, command);
				if (ret != null) {
					return ret;
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable transaction mapper found for " + command.getClass().getSimpleName());
	}

	// event handlers
	Map<Class<? extends RoboticsRuntime>, Map<Class<? extends EventEffect>, List<EventEffectMapper<? extends RoboticsRuntime, ? extends EventEffect>>>> eventEffectMappers = new HashMap<Class<? extends RoboticsRuntime>, Map<Class<? extends EventEffect>, List<EventEffectMapper<? extends RoboticsRuntime, ? extends EventEffect>>>>();

	public <R extends RoboticsRuntime, E extends EventEffect> void registerEventEffectMapper(Class<? extends R> runtime,
			Class<? extends E> eventEffect, EventEffectMapper<R, E> mapper) {
		if (!eventEffectMappers.containsKey(runtime)) {
			eventEffectMappers.put(runtime,
					new HashMap<Class<? extends EventEffect>, List<EventEffectMapper<? extends RoboticsRuntime, ? extends EventEffect>>>());
		}
		if (!eventEffectMappers.get(runtime).containsKey(eventEffect)) {
			eventEffectMappers.get(runtime).put(eventEffect,
					new ArrayList<EventEffectMapper<? extends RoboticsRuntime, ? extends EventEffect>>());
		}
		eventEffectMappers.get(runtime).get(eventEffect).add(mapper);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MapperResult mapEventHandler(RoboticsRuntime runtime, DataflowOutPort event, EventEffect handler,
			CommandMapperResult context) throws MappingException {

		List<EventEffectMapper<? extends RoboticsRuntime, ? extends EventEffect>> mappers = flatten(
				collectAll(collect(eventEffectMappers, runtime), handler));

		for (EventEffectMapper mapper : mappers) {
			try {
				EventHandlerMapperResult ret = mapper.map(runtime, event, handler, context,
						new EventHandlerMappingPorts());
				if (ret != null) {
					return ret;
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}
		}

		throw new MappingException("No suitable event effect mapper found for " + handler.getClass().getSimpleName());
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
