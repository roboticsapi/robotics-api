/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.List;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;

/**
 * Factory class for certain frequently used {@link RtActivity} combinations.
 */
public class RtActivities {

	/**
	 * protected static wrapper to getAffectedDevices.
	 * 
	 * @param activity activity to get the affected devices for
	 * @return list of affected devices
	 */
	public static List<Device> getAffectedDevices(RtActivity activity) {
		return activity.getAffectedDevices();
	}

	/**
	 * protected static wrapper to getControlledDevices.
	 * 
	 * @param activity activity to get the controlled devices for
	 * @return list of controlled devices
	 */
	public static List<Device> getControlledDevices(RtActivity activity) {
		return activity.getControlledDevices();
	}

	/**
	 * Creates a combination of the given main @link{RtActivity} and the given
	 * dependent RtActivity. When executed, the main RtActivity is started. While
	 * this is running, the dependent RtActivity is started when the given condition
	 * becomes active.
	 * 
	 * @param mainActivity   the main RtActivity to run
	 * @param startCondition condition for starting the dependent RtActivity
	 * @param subActivity    the dependent RtActivity
	 * @return combination of main and dependent RtActivities
	 * @throws RoboticsException thrown if the RtActivities could not be combined
	 */
	public static RtActivityWithSubactivities subActivity(RtActivity mainActivity, State startCondition,
			RtActivity subActivity) throws RoboticsException {
		return subActivity(null, mainActivity, startCondition, subActivity);
	}

	/**
	 * Creates a combination of the given main @link{RtActivity} and the given
	 * dependent RtActivity. When executed, the main RtActivity is started. While
	 * this is running, the dependent RtActivity is started when the given condition
	 * becomes active.
	 * 
	 * @param name           the combined RtActivity's name
	 * @param mainActivity   the main RtActivity to run
	 * @param startCondition condition for starting the dependent RtActivity
	 * @param subActivity    the dependent RtActivity
	 * @return combination of main and dependent RtActivities
	 * @throws RoboticsException thrown if the RtActivities could not be combined
	 */
	public static RtActivityWithSubactivities subActivity(String name, RtActivity mainActivity, State startCondition,
			RtActivity subActivity) throws RoboticsException {
		RtActivityWithSubactivities act = null;
		if (name == null) {
			act = new RtActivityWithSubactivities(mainActivity);
		} else {
			act = new RtActivityWithSubactivities(name, mainActivity);
		}

		act.addSubActivity(startCondition, subActivity);

		return act;
	}

	/**
	 * Creates a conditional @link{RtActivity} that is only executed if the given
	 * condition is true at the moment the RtActivity is started.
	 * 
	 * @param condition  the condition to determine whether the RtActivity should be
	 *                   executed
	 * @param ifActivity the RtActivity executed if the condition is true
	 * @return the conditional RtActivity
	 * @throws RoboticsException thrown if the conditional RtActivity could not be
	 *                           created
	 */
	public static ConditionalRtActivity ifThen(BooleanSensor condition, RtActivity ifActivity)
			throws RoboticsException {
		return new ConditionalRtActivity(condition, ifActivity);
	}

	/**
	 * Creates a conditional @link{RtActivity} that is only executed if the given
	 * condition is true at the moment the RtActivity is started.
	 * 
	 * @param name       the RtActivity's name
	 * @param condition  the condition to determine whether the RtActivity should be
	 *                   executed
	 * @param ifActivity the RtActivity executed if the condition is true
	 * @return the conditional RtActivity
	 * @throws RoboticsException thrown if the conditional RtActivity could not be
	 *                           created
	 */
	public static ConditionalRtActivity ifElse(String name, BooleanSensor condition, RtActivity ifActivity)
			throws RoboticsException {
		return new ConditionalRtActivity(name, condition, ifActivity);
	}

	/**
	 * Creates a conditional @link{RtActivity} that can follow two distinct
	 * execution paths. A @link{BooleanSensor} decides which path to execute.
	 * 
	 * @param condition    BooleanSensor defining the execution path condition
	 * @param ifActivity   executed if condition evaluates to true on start
	 * @param elseActivity executed if condition evaluates to true on start
	 * @return the conditional RtActivity
	 * @throws RoboticsException thrown if the conditional RtActivity could not be
	 *                           created
	 */
	public static ConditionalRtActivity ifElse(BooleanSensor condition, RtActivity ifActivity, RtActivity elseActivity)
			throws RoboticsException {
		return new ConditionalRtActivity(condition, ifActivity, elseActivity);
	}

	/**
	 * Creates a conditional @link{RtActivity} that can follow two distinct
	 * execution paths. A @link{BooleanSensor} decides which path to execute.
	 * 
	 * @param name         the conditional RtActivity's name
	 * @param condition    BooleanSensor defining the execution path condition
	 * @param ifActivity   executed if condition evaluates to true on start
	 * @param elseActivity executed if condition evaluates to true on start
	 * @return the conditional RtActivity
	 * @throws RoboticsException thrown if the conditional RtActivity could not be
	 *                           created
	 */
	public static ConditionalRtActivity ifElse(String name, BooleanSensor condition, RtActivity ifActivity,
			RtActivity elseActivity) throws RoboticsException {
		return new ConditionalRtActivity(name, condition, ifActivity, elseActivity);
	}

	/**
	 * Creates a parallel combination of a given set of RtActivities. All given
	 * RtActivities are started in parallel upon start of the created RtActivity.
	 * 
	 * @param activities the RtActivities to be started in parallel
	 * @return RtActivity defining a parallel combination of the given RtActivities
	 * @throws RoboticsException thrown if the parallel combination could not be
	 *                           created
	 */
	public static ParallelRtActivity parallel(RtActivity... activities) throws RoboticsException {
		return new ParallelRtActivity(activities);
	}

	/**
	 * Creates a parallel combination of a given set of RtActivities. All given
	 * RtActivities are started in parallel upon start of the created RtActivity.
	 * 
	 * @param name       the parallel combination's name
	 * @param activities the RtActivities to be started in parallel
	 * @return RtActivity defining a parallel combination of the given RtActivities
	 * @throws RoboticsException thrown if the parallel combination could not be
	 *                           created
	 */
	public static ParallelRtActivity parallel(String name, RtActivity... activities) throws RoboticsException {
		return new ParallelRtActivity(name, activities);
	}

	/**
	 * Creates an empty real-time sequence of RtActivities. The sequence can be
	 * filled using the @link{SequentialRtActivity}'s methods addActivity(..) and
	 * addContActivity(..).
	 * 
	 * @return empty RtActivity sequence
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity sequential() {
		return new SequentialRtActivity();
	}

	/**
	 * Creates an empty real-time sequence of RtActivities. The sequence can be
	 * filled using the @link{SequentialRtActivity}'s methods addActivity(..) and
	 * addContActivity(..).
	 * 
	 * @param name the RtActivity sequence's name
	 * @return empty RtActivity sequence
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity sequential(String name) {
		return new SequentialRtActivity(name);
	}

	/**
	 * Creates a real-time sequence of RtActivities that are executed strictly to
	 * their end.
	 * 
	 * @param activities the RtActivities in this Sequence
	 * @return RtActivity sequence containing all the supplied RtActivities in the
	 *         order as supplied
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity strictlySequential(RtActivity... activities) throws RoboticsException {
		return strictlySequential("SequentialActivity", activities);
	}

	/**
	 * Creates a real-time sequence of RtActivities that are all executed strictly
	 * to their end.
	 * 
	 * @param name       the RtActivity sequence's name
	 * @param activities the RtActivities in this Sequence
	 * @return RtActivity sequence containing all the supplied RtActivities in the
	 *         order as supplied
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity strictlySequential(String name, RtActivity... activities)
			throws RoboticsException {
		SequentialRtActivity seq = new SequentialRtActivity(name);

		for (RtActivity t : activities) {
			seq.addActivity(t);
		}
		return seq;
	}

	/**
	 * Creates a real-time sequence of RtActivities that are all executed
	 * continuously.
	 * 
	 * @param activities the RtActivities in this Sequence
	 * @return RtActivity sequence containing all the supplied RtActivities in the
	 *         order as supplied
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity contSequential(RtActivity... activities) throws RoboticsException {
		return contSequential("SequentialActivity", activities);
	}

	/**
	 * Creates a real-time sequence of RtActivities that are executed continuously.
	 * 
	 * @param name       the sequence's name
	 * @param activities the RtActivities in this Sequence
	 * @return RtActivity sequence containing all the supplied RtActivities in the
	 *         order as supplied
	 * @throws RoboticsException thrown if sequence could not be created
	 */
	public static SequentialRtActivity contSequential(String name, RtActivity... activities) throws RoboticsException {
		SequentialRtActivity seq = new SequentialRtActivity(name);

		for (RtActivity t : activities) {
			seq.addContActivity(t);
		}
		return seq;
	}

	/**
	 * Creates a @link{RtActivity} that 'sleeps' (i.e. does nothing) until it is
	 * cancelled. During this time, it does not control any @link{Device}.
	 * 
	 * @param runtime the @link{RoboticsRuntime} this RtActivity runs on
	 * @return the created RtActivity
	 */
	public static SleepRtActivity sleep(RoboticsRuntime runtime) {
		return new SleepRtActivity(runtime);
	}

	/**
	 * Creates an @link{RtActivity} that 'sleeps' (i.e. does nothing) for the given
	 * duration or until it is cancelled. During this time, it does not control
	 * any @link{Device}.
	 * 
	 * @param runtime the @link{RoboticsRuntime} this RtActivity runs on
	 * @param seconds the duration of sleeping in [s]
	 * @return the created RtActivity
	 */
	public static SleepRtActivity sleep(RoboticsRuntime runtime, double seconds) {
		return new SleepRtActivity(runtime, seconds);
	}

	/**
	 * Creates a @link{RtActivity} that waits for a certain @link{State} to occur
	 * and then executes a given RtActivity.
	 * 
	 * @param trigger   the State triggering execution
	 * @param toExecute the RtActivity to execute
	 * @return the created RtActivity
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	public static RtActivity executeWhen(State trigger, RtActivity toExecute) throws RoboticsException {
		return executeWhen("ExecuteWhen", trigger, toExecute);
	}

	/**
	 * Creates a @link{RtActivity} that waits for a certain @link{State} to occur
	 * and then executes a given RtActivity.
	 * 
	 * @param name      the name of the RtActivity to create
	 * @param trigger   the State triggering execution
	 * @param toExecute the RtActivity to execute
	 * @return the created RtActivity
	 * @throws RoboticsException thrown if the RtActivity could not be created
	 */
	public static RtActivity executeWhen(String name, State trigger, RtActivity toExecute) throws RoboticsException {
		SleepRtActivity sleep = sleep(toExecute.getRuntime());
		RtActivityWithSubactivities executeWhen = subActivity(name, sleep, trigger, toExecute);
		executeWhen.setTerminateActivities(toExecute);

		return executeWhen;
	}

}
