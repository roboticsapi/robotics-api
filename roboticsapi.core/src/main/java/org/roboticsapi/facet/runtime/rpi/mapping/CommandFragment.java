/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.Observer;
import org.roboticsapi.core.PersistContext.PersistedRealtimeValueFactory;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.Assignment;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.FragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.ReadBoolFromNet;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Cancel;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Clock;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeNet;
import org.roboticsapi.facet.runtime.rpi.mapping.DependentFragment.Dependency;
import org.roboticsapi.facet.runtime.rpi.mapping.DependentFragment.PortDependency;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.mapping.core.IdentityRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.core.mapper.RealtimeTupleMapper;

public class CommandFragment extends RuntimeFragment {
	private Command command = null;
	private FragmentOutPort outTerminate = null;
	private RealtimeDouble time;
	private RealtimeBoolean cancel;
	private Map<RealtimeValue<?>, RealtimeValueFragment<?>> realtimeValues = new HashMap<RealtimeValue<?>, RealtimeValueFragment<?>>();
	private List<RealtimeValueAliasFactory> realtimeValueAliasFactories = new ArrayList<RealtimeValueAliasFactory>();
	private List<RealtimeValueFragmentFactory> realtimeValueFragmentFactories = new ArrayList<RealtimeValueFragmentFactory>();

	public CommandFragment(Command command) throws RoboticsException {
		this.command = command;
		this.cancel = new IdentityRealtimeBoolean("<<Cancel>>");
		RealtimeValueFragment<Boolean> cancelFragment = add(
				new RealtimeBooleanFragment(this.cancel, new Cancel().getOutCancel()));

		realtimeValues.put(this.cancel, cancelFragment);

		this.time = new IdentityRealtimeDouble("<<Time>>", command.getRuntime());
		realtimeValues.put(this.time, new RealtimeDoubleFragment(this.time, new Clock().getOutValue()));

		realtimeValueFragmentFactories.add(new RealtimeTupleMapper(this));
	}

	public void addRealtimeValueFragmentFactory(RealtimeValueFragmentFactory factory) {
		realtimeValueFragmentFactories.add(factory);
	}

	public FragmentOutPort getOutTerminate() {
		return outTerminate;
	}

	public void setTermination(RealtimeValue<Boolean> terminate) throws MappingException {
		RealtimeValueFragment<Boolean> fragment = getOrCreateRealtimeValueFragment(terminate);
		this.outTerminate = provideOutPort(fragment.getValuePort(), "outTerminate");
	}

	public void addConsumerFragment(RealtimeValueConsumerFragment fragment) throws MappingException {
		addRealtimeValueSource(fragment);
		addWithDependencies(fragment);
	}

	public void addWithDependencies(DependentFragment fragment) throws MappingException {
		if (!getPrimitives().contains(fragment)) {
			add(fragment);
			for (Dependency<?> dep : fragment.getDependencies()) {
				RealtimeValueFragment<?> depFragment = getOrCreateRealtimeValueFragment(dep.getValue());
				addWithDependencies(depFragment);
				if (dep.getTimePort() != null) {
					if (depFragment.getTimePort() != null) {
						connect(depFragment.getTimePort(), dep.getTimePort());
					} else {
						TimeNet now = add(new TimeNet());
						connect(now.getOutValue(), dep.getTimePort());
					}
				}
				connect(depFragment.getValuePort(), dep.getValuePort());
			}

			for (PortDependency dep : fragment.getPortDependencies()) {
				OutPort source = findOutPort(dep.getSource());
				connect(source, dep.getInPort());
			}
		}
	}

	private OutPort findOutPort(OutPort port) {
		if (getPrimitives().contains(port.getPrimitive())) {
			return port;
		}

		for (Primitive prim : getPrimitives()) {
			if (prim instanceof Fragment) {
				OutPort ret = findOutPort(port, (Fragment) prim);
				if (ret != null) {
					return ret;
				}
			}
		}

		return null;
	}

	private FragmentOutPort findOutPort(OutPort port, Fragment context) {
		if (context.getPrimitives().contains(port.getPrimitive())) {
			return context.provideOutPort(port, port.getName() + "_" + port.getPrimitiveName());
		}

		for (Primitive prim : context.getPrimitives()) {
			if (prim instanceof Fragment) {
				FragmentOutPort ret = findOutPort(port, (Fragment) prim);
				if (ret != null) {
					return context.provideOutPort(ret, ret.getName() + "_" + ret.getPrimitiveName());
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> RealtimeValueFragment<T> getOrCreateRealtimeValueFragment(RealtimeValue<T> value)
			throws MappingException {
		value = value.getForScope(command);
		if (value.getRuntime() != null && value.getRuntime() != command.getRuntime())
			throw new MappingException("Realtime value cannot be used in this context.");
		if (realtimeValues.containsKey(value)) {
			if (realtimeValues.get(value) == null) {
				throw new MappingException("Cycle in realtime value dependencies");
			}
			return (RealtimeValueFragment<T>) realtimeValues.get(value);
		}

		for (RealtimeValueAliasFactory factory : realtimeValueAliasFactories) {
			RealtimeValue<T> alias = factory.createRealtimeValueAlias(value);
			if (alias != null) {
				if (alias.contains(value))
					throw new MappingException("Cycle in realtime value alias - check " + factory);
				return getOrCreateRealtimeValueFragment(alias);
			}
		}

		for (RealtimeValueFragmentFactory producer : realtimeValueFragmentFactories) {
			RealtimeValueFragment<T> ret = (RealtimeValueFragment<T>) producer.createRealtimeValueFragment(value);
			if (ret != null) {
				realtimeValues.put(value, null);
				addWithDependencies(ret);
				realtimeValues.put(value, ret);
				return ret;
			}
		}
		throw new MappingException(
				"Could not create fragment - neither RealtimeValueAliasFactory nor RealtimeValueFragmentFactory available for "
						+ value + " [" + value.getClass() + "]");
	}

	public RealtimeDouble getTime() {
		return time;
	}

	public RealtimeBoolean getCancel() {
		return cancel;
	}

	private int resultNr = 0;
	private Map<CommandResult, NetcommValue> resultMap = new HashMap<CommandResult, NetcommValue>();

	public void addCommandResult(List<RealtimeBoolean> resultConditions, CommandResult result) throws MappingException {
		RealtimeBoolean cond = orState(resultConditions);

		RealtimeValueFragment<Boolean> valueFragment = getOrCreateRealtimeValueFragment(cond);
		ReadBoolFromNet read = add(new ReadBoolFromNet("r" + resultNr++));
		connect(valueFragment.getValuePort(), read.getInValue());
		resultMap.put(result, read.getNetcomm());
	}

	private RealtimeBoolean orState(List<RealtimeBoolean> resultConditions) {
		RealtimeBoolean cond = RealtimeBoolean.FALSE;
		for (RealtimeBoolean state : resultConditions)
			cond = cond.or(state);
		return cond;
	}

	public void setTermination(List<RealtimeBoolean> completion) throws MappingException {
		setTermination(orState(completion));
	}

	public <T> void addObserver(Observer<T> observer) throws MappingException {
		addConsumerFragment(getOrCreateRealtimeValueFragment(observer.getSensor())
				.createObserverFragment(observer.getCondition(), observer.getListener()));
	}

	public void addEventHandler(final EventHandler handler) throws MappingException {
		addObserver(handler.getState().createObserver(new RealtimeValueListener<Boolean>() {
			@Override
			public void onValueChanged(Boolean newValue) {
				if (newValue) {
					if (handler.getEffect().isAsync()) {
						handler.getContext().getCommandHandle().startThread(handler.getEffect().getRunnable());
					} else {
						handler.getEffect().getRunnable().run();
					}
				}
			}
		}, null, false));
	}

	public Map<CommandResult, NetcommValue> getResultMap() {
		return resultMap;
	}

	public void addRealtimeValueSource(RealtimeValueSource source) {
		realtimeValueAliasFactories.addAll(source.getRealtimeValueAliasFactories());
		realtimeValueFragmentFactories.addAll(source.getRealtimeValueFragmentFactories());
	}

	public void addRealtimeValueAliasFactory(RealtimeValueAliasFactory factory) {
		realtimeValueAliasFactories.add(factory);
	}

	public <T> void addAssignment(Assignment<T> assignment) throws MappingException {
		final RealtimeValueFragment<T> valueFragment = getOrCreateRealtimeValueFragment(assignment.getSource());
		final InterNetcommFragment netcomm = valueFragment.createInterNetcommFragment(assignment.getCondition());
		addConsumerFragment(netcomm);
		assignment.getTarget().setFactory(new PersistedRealtimeValueFactory<T>() {
			@Override
			public RealtimeValue<T> createRealtimeValue(Command command) {
				return valueFragment.createInterNetcommValue(command, netcomm.getKey());
			}
		});
	}
}
