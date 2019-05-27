/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.commandfilter;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.TransactionCommand;

public abstract class TransactionCommandFilter implements CommandFilter {
	private TransactionCommand current = null;

	@Override
	public final boolean filter(Command command) {
		if (command instanceof TransactionCommand) {
			TransactionCommand trans = (TransactionCommand) command;
			if (filterTransactionCommand(trans)) {
				current = trans;
				return true;
			}
		}

		current = null;
		return false;

	}

	protected abstract boolean filterTransactionCommand(TransactionCommand command);

	protected void filterChildren(CommandFilter filter) {
		if (current != null) {
			filterChildrenInternal(filter, current);
		}
	}

	private void filterChildrenInternal(CommandFilter filter, TransactionCommand command) {
		for (Command cmd : command.getCommandsInTransaction()) {
			if (filter.filter(cmd)) {
				filter.process(cmd);
			}
		}
	}

	protected void filterDescendants(CommandFilter filter) {
		if (current != null) {
			filterDescendantsInternal(filter, current);
		}
	}

	private void filterDescendantsInternal(CommandFilter filter, TransactionCommand command) {
		filterChildrenInternal(filter, command);

		for (Command cmd : command.getCommandsInTransaction()) {
			if (cmd instanceof TransactionCommand) {
				filterDescendantsInternal(filter, (TransactionCommand) cmd);
			}
		}
	}

	@Override
	public final void process(Command command) {
		if (command instanceof TransactionCommand) {
			processTransactionCommand((TransactionCommand) command);
		}
	}

	protected abstract void processTransactionCommand(TransactionCommand command);
}