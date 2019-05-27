/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.AliasState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.util.RAPILogger;

// TODO: Auto-generated Javadoc
/**
 * Abstract implementation of {@link RtActivity}. Relies on
 * {@link AbstractActivity} and also uses the singleton instance of
 * {@link ActivityScheduler} for scheduling execution.
 */
public abstract class AbstractRtActivity extends AbstractActivity implements RtActivity {

	/** The cancel states. */
	private final List<State> cancelStates = new Vector<State>();

	/** The done states. */
	private final List<State> doneStates = new Vector<State>();

	/** The activity states. */
	private final List<AliasCommandState> activityStates = new Vector<AbstractRtActivity.AliasCommandState>();

	/** The raised exceptions. */
	private final List<PropagatedException> raisedExceptions = new Vector<PropagatedException>();

	/** The declared exceptions. */
	private final List<DeclaredException> declaredExceptions = new Vector<DeclaredException>();

	/** The ignored exceptions. */
	private final List<Class<? extends CommandRealtimeException>> ignoredExceptions = new Vector<Class<? extends CommandRealtimeException>>();

	/** The triggers attached to this RtActivity. */
	private final List<TriggerActivity> triggers = new ArrayList<TriggerActivity>();

	/** The astate. */
	private AState astate = new NewState(this);

	/** The exception has been thrown. */
	boolean exceptionHasBeenThrown = false;

	/** The command. */
	private Command command;

	/** The maintaining state. */
	private State maintainingState;

	/** The properties. */
	private final Map<Device, List<ActivityProperty>> properties = new HashMap<Device, List<ActivityProperty>>();

	/** The state entered listeners. */
	protected final List<StateListening<StateEnteredListener>> stateEnteredListeners = new Vector<StateListening<StateEnteredListener>>();

	/** The state left listeners. */
	protected final List<StateListening<StateLeftListener>> stateLeftListeners = new Vector<StateListening<StateLeftListener>>();

	/**
	 * Creates a new AbstractRtActivity instance with the given name.
	 *
	 * @param name the Activity's name
	 */
	public AbstractRtActivity(String name) {
		super(name);
	}

	/**
	 * Creates a new AbstractRtActivity instance.
	 */
	public AbstractRtActivity() {
		this("Activity");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.Activity#addCancelConditions(org.roboticsapi
	 * .core.State, org.roboticsapi.core.State)
	 */
	@Override
	public final void addCancelConditions(State state, State... furtherStates) {
		cancelStates.add(state);

		if (furtherStates != null) {
			for (int i = 0; i < furtherStates.length; i++) {
				cancelStates.add(furtherStates[i]);
			}
		}
	}

	// @Override
	// public final void addDoneConditions(State state, State... furtherStates)
	// {
	// doneStates.add(state);
	//
	// if (furtherStates != null) {
	// for (int i = 0; i < furtherStates.length; i++) {
	// doneStates.add(furtherStates[i]);
	// }
	// }
	//
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see RtActivity#raiseException(CommandRealtimeException, Class, Class...)
	 */
	@Override
	public final void raiseException(CommandRealtimeException raisedException,
			Class<? extends CommandRealtimeException> cause) {
		List<Class<? extends CommandRealtimeException>> exceptions = new ArrayList<Class<? extends CommandRealtimeException>>();
		exceptions.add(cause);
		raiseException(raisedException, exceptions);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RtActivity#raiseException(CommandRealtimeException, List)
	 */
	@Override
	public final void raiseException(CommandRealtimeException raisedException,
			List<Class<? extends CommandRealtimeException>> causes) {
		raisedExceptions.add(new PropagatedException(raisedException, causes));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RtActivity#declareException(CommandRealtimeException, State, State...)
	 */
	@Override
	public final void declareException(CommandRealtimeException thrownException, State cause, State... furtherCauses) {
		List<State> states = new ArrayList<State>();

		states.add(cause);

		if (furtherCauses != null) {
			for (State s : furtherCauses) {
				states.add(s);
			}
		}

		declareException(thrownException, states);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see RtActivity#declareException(CommandRealtimeException, List)
	 */
	@Override
	public final void declareException(CommandRealtimeException thrownException, List<State> causes) {
		declaredExceptions.add(new DeclaredException(thrownException, causes));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.activity.RtActivity#ignoreException(java.lang.Class)
	 */
	@Override
	public void ignoreException(Class<? extends CommandRealtimeException> type) {
		ignoredExceptions.add(type);

	}

	@Override
	public void addTrigger(State triggering, RtActivity toExecute) {
		triggers.add(new TriggerActivity(triggering, toExecute));
	}

	/**
	 * Sets the command.
	 *
	 * @param command the new command
	 * @throws RoboticsException the robotics exception
	 */
	protected final void setCommand(Command command, Map<Device, Activity> prevActivities) throws RoboticsException {
		setCommand(command, prevActivities, null);
	}

	/**
	 * Sets the command.
	 *
	 * @param command          the command
	 * @param maintainingState the maintaining state
	 * @throws RoboticsException the robotics exception
	 */
	protected final void setCommand(Command command, Map<Device, Activity> prevActivities, State maintainingState)
			throws RoboticsException {
		// astate.beginExecute();

		this.command = processTriggers(command, prevActivities);
		this.maintainingState = maintainingState;

		for (AliasCommandState s : activityStates) {
			s.setCommand(command);
		}

		// process declared exceptions
		for (DeclaredException e : declaredExceptions) {
			OrState causingState = new OrState();
			for (State cause : e.causes) {
				causingState.addState(cause);
			}
			if (causingState.getStates().size() > 0) {
				command.addStateFirstEnteredHandler(causingState, new ExceptionThrower(e.declaredException));
			}
		}

		// process propagated exceptions
		for (PropagatedException e : raisedExceptions) {
			for (Class<? extends CommandRealtimeException> exc : e.causes) {
				command.addExceptionHandler(exc, new ExceptionThrower(e.propagatedException), true);
			}
		}

		for (Class<? extends CommandRealtimeException> type : ignoredExceptions) {
			command.addExceptionHandler(type, new ExceptionIgnorer(), true);
		}

		OrState canceller = new OrState();

		for (State c : cancelStates) {
			canceller.addState(c);
		}
		if (canceller.getStates().size() > 0) {
			command.addStateFirstEnteredHandler(canceller, new CommandCanceller());
		}

		OrState aborter = new OrState();

		for (State c : doneStates) {
			aborter.addState(c);
		}
		if (aborter.getStates().size() > 0) {
			command.addStateFirstEnteredHandler(aborter, new CommandStopper());
		}

		command.addStateFirstEnteredHandler(command.getStartedState(), new JavaExecutor(new Runnable() {

			@Override
			public void run() {

				setAState(new RunningState(AbstractRtActivity.this));

			}
		}, false));

		// TODO: Should we do this?
		// command.addStateFirstEnteredHandler(command.getCancelState(),
		// new JavaExecutor(new Runnable() {
		//
		// @Override
		// public void run() {
		// setAState(new FailedState(AbstractRtActivity.this));
		//
		// }
		// }, false));

		if (maintainingState != null) {
			getCommand().addStateFirstEnteredHandler(maintainingState, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					// System.out.println(AbstractRtActivity.this
					// .getName() + ": Maintaining State entered");
					setAState(new MaintainingState(AbstractRtActivity.this));

				}
			}, false));
		}

		for (final StateListening<StateEnteredListener> l : stateEnteredListeners) {
			command.addStateEnteredHandler(l.state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					l.listener.stateEntered();

				}
			}));
		}

		for (final StateListening<StateLeftListener> l : stateLeftListeners) {

			command.addStateLeftHandler(l.state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					l.listener.stateLeft();

				}
			}));
		}

		beforeCommandSeal(command);

		command.seal();

	}

	private Command processTriggers(Command command, Map<Device, Activity> prevActivities) throws RoboticsException {

		if (triggers.size() == 0) {
			return command;
		} else {
			// if triggers have been attached to the Activity, we embed the
			// Activity's Command and all triggers' Commands in a
			// TransactionCommand
			TransactionCommand transaction = command.getRuntime().createTransactionCommand();

			// the main command is auto-started
			transaction.addStartCommand(command);

			// the lifecycle of the transaction depends on whether one of the
			// trigger commands is active
			OrState subactivityActive = new OrState();

			// process all triggers
			for (TriggerActivity t : triggers) {

				// this Activity affects the Actuators of the triggered
				// Activity as well, which will cause it to be started only
				// once all of them are available. However, the trigger's
				// Actuators do not count as 'controlled' by this Activity,
				// otherwise maybe takeover would be attempted, which is
				// unwanted
				addAdditionalAffectedDevices(t.activity.getAffectedDevices());

				// prepare trigger Activity, considering history
				t.activity.prepare(prevActivities);

				// add the trigger Activity's Command to the transaction
				Command triggerCommand = t.activity.getCommand();
				transaction.addCommand(triggerCommand);

				// start the trigger Activity when the specified State is first
				// entered
				if (t.condition != null) {
					transaction.addStateFirstEnteredHandler(t.condition, new CommandStarter(triggerCommand));
				}

				// contribute the trigger Activity's activeness to the
				// subactivityActive State
				subactivityActive.addState(triggerCommand.getActiveState().and(triggerCommand.getDoneState().not()));

				// trigger Activity should be cancelled with the main Activity
				transaction.addStateEnteredHandler(transaction.getCancelState(), new CommandCanceller(triggerCommand));
			}

			// takeover of this Activity is only allowed if no trigger Activity
			// is currently active
			State takeoverAllowed = command.getTakeoverAllowedState();
			if (subactivityActive.getStates().size() > 0) {
				takeoverAllowed = takeoverAllowed.and(subactivityActive.not());
			}

			transaction.addTakeoverAllowedCondition(takeoverAllowed);

			// the transaction is done when the main Activity is done and no
			// trigger Activity is active
			State done = command.getDoneState();
			if (subactivityActive.getStates().size() > 0) {
				done = done.and(subactivityActive.not());
			}
			transaction.addDoneStateCondition(done);

			// forward transaction cancel to main Activity
			transaction.addStateEnteredHandler(transaction.getCancelState(), new CommandCanceller(command));

			return transaction;
		}
	}

	/**
	 * Hook for subclasses that wish to modify the final Command before it is
	 * sealed.
	 *
	 * @param command
	 *
	 * @throws RoboticsException when something goes wrong in modifying the Command
	 */
	protected void beforeCommandSeal(Command command) throws RoboticsException {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.Activity#getCommand()
	 */
	@Override
	public final Command getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#getMaintainingState()
	 */
	@Override
	public final State getMaintainingState() {
		return maintainingState;
	}

	/**
	 * Prepares this activity for execution.
	 *
	 * @param prevActivities the preceding activities
	 * @return true, if this Activity can be scheduled after the preceding
	 *         Activities
	 * @throws RoboticsException             thrown if an error occurred during
	 *                                       preparation
	 * @throws ActivityNotCompletedException thrown if the previous Activity is not
	 *                                       yet completed though necessary
	 */
	@Override
	public abstract Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException;

	/**
	 * Adds the property.
	 *
	 * @param device   the device
	 * @param property the property
	 */
	protected final void addProperty(Device device, ActivityProperty property) {
		if (property == null) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL, "Someone tried to add null Property, ignoring it");
			return;
		}

		if (!properties.containsKey(device)) {
			properties.put(device, new Vector<ActivityProperty>());
		}
		properties.get(device).add(property);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.activity.Activity#getFutureProperty(org.roboticsapi
	 * .core. Device, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		if (!properties.containsKey(device)) {
			return null;
		}
		for (final ActivityProperty tp : properties.get(device)) {
			if (property.isAssignableFrom(tp.getClass())) {
				return new Future<T>() {

					@Override
					public boolean cancel(boolean mayInterruptIfRunning) {
						return false;
					}

					@Override
					public T get() throws InterruptedException, ExecutionException {
						return (T) tp;
					}

					@Override
					public T get(long timeout, TimeUnit unit)
							throws InterruptedException, ExecutionException, TimeoutException {
						return (T) tp;
					}

					@Override
					public boolean isCancelled() {
						return false;
					}

					@Override
					public boolean isDone() {
						return true;
					}
				};
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.activity.Activity#getProperty(org.roboticsapi.core.
	 * Device, java.lang.Class)
	 */
	@Override
	public final <T extends ActivityProperty> T getProperty(Device device, Class<T> property) {
		Future<T> futureProperty = getFutureProperty(device, property);
		if (futureProperty == null) {
			return null;
		}

		try {
			return futureProperty.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}

		return null;
	}

	/**
	 * Adds the activity state.
	 *
	 * @param state the state
	 */
	protected final void addActivityState(AliasCommandState state) {

		if (getCommand() != null) {
			state.setCommand(getCommand());
		} else {
			activityStates.add(state);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#getCompletedState()
	 */
	@Override
	public final State getCompletedState() {

		AliasCommandState state = new AliasCommandState() {

			@Override
			public void setCommand(Command command) {
				setOther(command.getCompletedState());
			}

		};

		addActivityState(state);

		return state;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#getStartedState()
	 */
	@Override
	public final State getStartedState() {
		AliasCommandState state = new AliasCommandState() {

			@Override
			public void setCommand(Command command) {
				setOther(command.getStartedState());
			}

		};

		addActivityState(state);

		return state;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#getCancelState()
	 */
	// @Override
	// public final State getCancelState() {
	// AliasCommandState state = new AliasCommandState() {
	//
	// @Override
	// public void setCommand(Command command) {
	// setOther(command.getCancelState());
	// }
	//
	// };
	//
	// addActivityState(state);
	//
	// return state;
	// }
	/**
	 * Sets the a state.
	 *
	 * @param state the new a state
	 */
	private final void setAState(AState state) {

		// prevent stepping after running
		if (this.astate.getStatus().ordinal() > ActivityStatus.RUNNING.ordinal()
				&& state.getStatus().ordinal() <= ActivityStatus.RUNNING.ordinal()) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
					"Tried to step from " + this.astate.getStatus() + " to " + state.getStatus());
			return;
		}
		this.astate = state;

		changeStatus(astate.getStatus());
	}

	/**
	 * Update a state.
	 *
	 * @param status the status
	 */
	private final void updateAState(CommandStatus status) {
		astate.processCommandStatus(status);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.AbstractActivity#beginExecute()
	 */
	@Override
	public RtActivity beginExecute() throws RoboticsException {

		super.beginExecute();

		getCommand().getCommandHandle().addStatusListener(new CommandStatusListener() {
			@Override
			public void statusChanged(CommandStatus newStatus) {

				updateAState(newStatus);
			}

		});

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.activity.AbstractActivity#scheduleActivity(java.util
	 * .Set)
	 */
	@Override
	protected final void scheduleActivity(Set<Device> controlledDevs) throws RoboticsException {
		astate.beginExecute();

		super.scheduleActivity(controlledDevs);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.Activity#endExecute()
	 */
	@Override
	public void endExecute() throws RoboticsException {

		if (exceptionHasBeenThrown) {
			return;
		}

		if (getStatus() == ActivityStatus.NEW) {
			throw new IllegalStateException("endExecute() may only be called after calling beginExecute()");
		}

		ActivityStatus status = getStatus();
		if (status == ActivityStatus.MAINTAINING || status == ActivityStatus.COMPLETED) {
			return;
		}

		if (getStatus() == ActivityStatus.FAILED) {
			if (getException() != null) {
				exceptionHasBeenThrown = true;
				throw getException();
			} else {
				throw new RoboticsException("Activity execution failed for unknown reason!");
			}
		}

		try {

			final Semaphore sem = new Semaphore(1);
			sem.acquire();

			addStatusListener(new ActivityStatusListener() {

				@Override
				public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
					if (newStatus == ActivityStatus.MAINTAINING || newStatus == ActivityStatus.COMPLETED
							|| newStatus == ActivityStatus.FAILED) {
						sem.release();
					}

				}
			});

			sem.acquire();

			if (getStatus() == ActivityStatus.FAILED) {
				if (getException() != null) {
					exceptionHasBeenThrown = true;
					throw getException();
				} else {
					throw new RoboticsException("Activity execution failed for unknown reason!");
				}
			}

		} catch (InterruptedException e) {
			throw new RoboticsException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.AbstractActivity#cancelExecuteInternal()
	 */
	@Override
	protected void cancelExecuteInternal() throws RoboticsException {

		Command cmd = getCommand();
		if (cmd != null) {
			CommandHandle handle = cmd.getCommandHandle();
			if (handle != null) {
				handle.cancel();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.Activity#getException()
	 */
	@Override
	public RoboticsException getException() {
		if (getCommand() == null) {
			return null;
		}

		if (getCommand().getCommandHandle() == null) {
			return null;
		} else {
			return getCommand().getCommandHandle().getOccurredException();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#addStateListener( State,
	 * StateEnteredListener)
	 */
	@Override
	public final void addStateListener(State state, StateEnteredListener listener) {
		stateEnteredListeners.add(new StateListening<StateEnteredListener>(state, listener));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.roboticsapi.activity.RtActivity#addStateListener( State,
	 * StateLeftListener)
	 */
	@Override
	public final void addStateListener(State state, StateLeftListener listener) {
		stateLeftListeners.add(new StateListening<StateLeftListener>(state, listener));
	}

	/**
	 * The Class AliasCommandState.
	 */
	private abstract class AliasCommandState extends AliasState {

		/**
		 * Sets the command.
		 *
		 * @param command the new command
		 */
		public abstract void setCommand(Command command);
	}

	/**
	 * The Class PropagatedException.
	 */
	private class PropagatedException {

		/** The propagated exception. */
		public final CommandRealtimeException propagatedException;

		/** The causes. */
		public final List<Class<? extends CommandRealtimeException>> causes;

		/**
		 * Instantiates a new propagated exception.
		 *
		 * @param propagatedException the propagated exception
		 * @param causes              the causes
		 */
		public PropagatedException(CommandRealtimeException propagatedException,
				List<Class<? extends CommandRealtimeException>> causes) {
			this.causes = causes;
			this.propagatedException = propagatedException;

		}
	}

	/**
	 * The Class DeclaredException.
	 */
	private class DeclaredException {

		/** The declared exception. */
		public final CommandRealtimeException declaredException;

		/** The causes. */
		public final List<State> causes;

		/**
		 * Instantiates a new declared exception.
		 *
		 * @param declaredException the declared exception
		 * @param causes            the causes
		 */
		public DeclaredException(CommandRealtimeException declaredException, List<State> causes) {
			this.declaredException = declaredException;
			this.causes = causes;

		}
	}

	/**
	 * The Class AState.
	 */
	private abstract class AState {

		/** The activity. */
		protected final AbstractRtActivity activity;

		/**
		 * Instantiates a new a state.
		 *
		 * @param activity the activity
		 */
		public AState(AbstractRtActivity activity) {
			this.activity = activity;

		}

		/**
		 * Step.
		 *
		 * @param state the state
		 */
		protected void step(AState state) {
			RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL, DateFormat.getDateTimeInstance().format(new Date()) + " "
					+ getName() + ": Stepping from " + getStatus() + " to " + state.getStatus());

			activity.setAState(state);
		}

		/**
		 * Auto step.
		 *
		 * @param state     the state
		 * @param newStatus the new status
		 */
		protected void autoStep(AState state, CommandStatus newStatus) {
			step(state);

			state.processCommandStatus(newStatus);
		}

		/**
		 * Begin execute.
		 *
		 * @throws RoboticsException the robotics exception
		 */
		public abstract void beginExecute() throws RoboticsException;

		/**
		 * Process command status.
		 *
		 * @param newStatus the new status
		 */
		public abstract void processCommandStatus(CommandStatus newStatus);

		/**
		 * Gets the status.
		 *
		 * @return the status
		 */
		public abstract ActivityStatus getStatus();

	}

	/**
	 * The Class NewState.
	 */
	private class NewState extends AState {

		/**
		 * Instantiates a new new state.
		 *
		 * @param activity the activity
		 */
		public NewState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() {
			step(new ScheduledState(activity));
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {

			switch (newStatus) {
			case READY:
				step(new ScheduledState(activity));
				break;
			case RUNNING:
				// autoStep(new ScheduledState(activity), newStatus);
				break;
			case TERMINATED:
				autoStep(new ScheduledState(activity), newStatus);
				break;
			case ERROR:
				autoStep(new ScheduledState(activity), newStatus);
				break;

			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.NEW;
		}
	}

	/**
	 * The Class ScheduledState.
	 */
	private class ScheduledState extends AState {

		/**
		 * Instantiates a new scheduled state.
		 *
		 * @param activity the activity
		 */
		public ScheduledState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() throws RoboticsException {
			throw new RoboticsException("Illegal beginExecute() in Scheduled state");

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {
			switch (newStatus) {
			case READY:
				// step(this);
				break;
			case RUNNING:
				// ignore CommandState here, this is done by listener for
				// Command.StartedState
				// step(new RunningState(activity));
				break;
			case TERMINATED:
				if (getException() != null) {
					step(new FailedState(activity));
				} else {
					autoStep(new RunningState(activity), newStatus);
				}
				break;
			case ERROR:
				step(new FailedState(activity));
				break;

			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.SCHEDULED;
		}
	}

	/**
	 * The Class RunningState.
	 */
	private class RunningState extends AState {

		/**
		 * Instantiates a new running state.
		 *
		 * @param activity the activity
		 */
		public RunningState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() throws RoboticsException {
			throw new RoboticsException("Illegal beginExecute() in Running state");

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {
			switch (newStatus) {
			case READY:
				break;
			case RUNNING:
				// step(this);
				break;
			case TERMINATED:
				if (getException() != null) {
					step(new FailedState(activity));
				} else {
					step(new CompletedState(activity));
				}
				break;
			case ERROR:
				step(new FailedState(activity));
				break;

			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.RUNNING;
		}
	}

	/**
	 * The Class MaintainingState.
	 */
	private class MaintainingState extends AState {

		/**
		 * Instantiates a new maintaining state.
		 *
		 * @param activity the activity
		 */
		public MaintainingState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() throws RoboticsException {
			throw new RoboticsException("Illegal beginExecute() in Maintaining state");

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {
			switch (newStatus) {
			case READY:
				break;
			case RUNNING:
				break;
			case TERMINATED:
				if (getException() != null) {
					step(new FailedState(activity));
				} else {
					step(new CompletedState(activity));
				}
				break;
			case ERROR:
				step(new FailedState(activity));
				break;

			}

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.MAINTAINING;
		}
	}

	/**
	 * The Class FailedState.
	 */
	private class FailedState extends AState {

		/**
		 * Instantiates a new failed state.
		 *
		 * @param activity the activity
		 */
		public FailedState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() throws RoboticsException {
			throw new RoboticsException("Illegal beginExecute() in Failed state");

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.FAILED;
		}
	}

	/**
	 * The Class CompletedState.
	 */
	private class CompletedState extends AState {

		/**
		 * Instantiates a new completed state.
		 *
		 * @param activity the activity
		 */
		public CompletedState(AbstractRtActivity activity) {
			super(activity);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#beginExecute()
		 */
		@Override
		public void beginExecute() throws RoboticsException {
			throw new RoboticsException("Illegal beginExecute() in Completed state");

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#processCommandStatus
		 * (org.roboticsapi.core.CommandStatus)
		 */
		@Override
		public void processCommandStatus(CommandStatus newStatus) {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.roboticsapi.activity.AbstractRtActivity.AState#getStatus()
		 */
		@Override
		public ActivityStatus getStatus() {
			return ActivityStatus.COMPLETED;
		}
	}
}
