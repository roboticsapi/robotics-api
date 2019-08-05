package org.roboticsapi.runtime.mockclass;

import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.AbstractRuntime;

public class MockAbstractRuntime extends AbstractRuntime {
	@Override
	public CommandHandle loadCommand(Command command) throws RoboticsException {
		return new MockCommandHandleRAPILoggerImpl(command);
	}

	@Override
	public double getOverride() {
		return 0;
	}

	@Override
	public void setOverride(double newOverride) {
	}

	@Override
	public void addExtension(String extensionId) {
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		return null;
	}

	@Override
	public Command createWaitCommand(String name) {
		return new MockWaitCommand(name, this);
	}

	@Override
	public Command createWaitCommand(String name, double duration) {
		return new MockWaitCommand(name, this, duration);
	}

	@Override
	protected RuntimeCommand createRuntimeCommandInternal(Actuator actuator, Action action,
			DeviceParameterBag parameters) throws RoboticsException {
		return null;
	}

	@Override
	public TransactionCommand createTransactionCommand(String name) {
		return null;
	}

	@Override
	public List<CommandHandle> getCommandHandles() {
		return null;
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
	}
}
