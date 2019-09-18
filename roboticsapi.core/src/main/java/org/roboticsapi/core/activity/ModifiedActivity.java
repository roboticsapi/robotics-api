/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public abstract class ModifiedActivity<T extends Activity> extends AbstractActivity {

	protected final T instance;
	List<Command> additionalCommands = new ArrayList<Command>();

	public ModifiedActivity(T instance) {
		super(instance.toString(), instance.getDevices().toArray(new Device[0]));
		this.instance = instance;
	}

	public ModifiedActivity(String name, T instance) {
		super(name, instance.getDevices().toArray(new Device[0]));
		this.instance = instance;
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		ActivityHandle handle = instance.createHandle();
		return new ActivityHandle(this) {
			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				ActivitySchedule schedule = handle.prepare(result, errorStack);
				return modifySchedule(schedule);
			}
		};
	}

	protected abstract ActivitySchedule modifySchedule(ActivitySchedule schedule);

	@Override
	public void addCancelCondition(RealtimeBoolean state) {
		instance.addCancelCondition(state);
	}

	@Override
	public void addErrorCondition(CommandRealtimeException exception, Class<? extends CommandRealtimeException> cause) {
		instance.addErrorCondition(exception, cause);
	}

	@Override
	public void addErrorCondition(CommandRealtimeException exception, RealtimeBoolean cause) {
		instance.addErrorCondition(exception, cause);
	}

	@Override
	public void ignoreError(Class<? extends CommandRealtimeException> type) {
		instance.ignoreError(type);
	}

}
