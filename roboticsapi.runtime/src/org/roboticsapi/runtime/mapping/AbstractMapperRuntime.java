/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.AbstractRuntime;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.TransactionMapperResult;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class AbstractMapperRuntime extends AbstractRuntime {

	/**
	 * Interface for a hook executed during the mapping of a command
	 */
	public interface CommandMappingHook {
		/**
		 * Called for the net fragment created for a command.
		 * 
		 * @param netFragment net fragment for the command
		 * 
		 */
		void netFragmentHook(NetFragment netFragment);

		/**
		 * Called for the net created for a command.
		 * 
		 * @param net net for the command
		 */
		void netHook(Fragment net);
	}

	private final List<CommandMappingHook> mappingHooks = new Vector<CommandMappingHook>();

	public void addCommandMappingHook(CommandMappingHook hook) {
		mappingHooks.add(hook);
	}

	public void removeCommandMappingHook(CommandMappingHook hook) {
		mappingHooks.remove(hook);
	}

	public abstract MapperRegistry getMapperRegistry();

	@Override
	public CommandHandle loadCommand(Command command) throws RoboticsException {
		NetFragment intermediateNet;
		final Fragment net = new Fragment();
		net.setName("ROOT");
		try {
			TransactionMapperResult mapperResult = this.getMapperRegistry().mapTransaction(this, command);
			intermediateNet = mapperResult.getNetFragment();
			for (CommandMappingHook hook : mappingHooks) {
				hook.netFragmentHook(intermediateNet);
			}

			// build net
			intermediateNet.addToFragment(net);
			net.provideOutPort(mapperResult.getTerminationPort().getPorts().get(0), "outTerminate");
			net.provideOutPort(mapperResult.getFailurePort().getPorts().get(0), "outFail");

			if (!net.correctLinks()) {
				StringBuilder errmsg = new StringBuilder();
				errmsg.append("Unconnected Ports:");
				for (InPort port : net.getInPorts()) {
					errmsg.append(" ").append(port.getName());
				}
				throw new RPIException(errmsg.toString());
			}

			for (CommandMappingHook hook : mappingHooks) {
				hook.netHook(net);
			}

		} catch (final MappingException e) {
			throw new CommandException(e.getMessage(), e);
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}

		CommandHandle handle = loadNet(net, command.getName());
		command.setCommandHandle(handle);
		return handle;
	}

	protected abstract CommandHandle loadNet(Fragment fragment, String description) throws RoboticsException;

}
