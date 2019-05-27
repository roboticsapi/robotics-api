/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.JointDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.CycleTime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.LinkBuilder;
import org.roboticsapi.runtime.mapping.LinkBuilderResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflow;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughInPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.JointVelDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;
import org.roboticsapi.runtime.rpi.RPIException;

final class SoftRobotMultijointLinkBuilder implements LinkBuilder {
	private final SoftRobotRuntime runtime;
	private final SoftRobotMultiJointDeviceDriver driver;
	private final SoftRobotMultiJointMapper mapper;

	SoftRobotMultijointLinkBuilder(SoftRobotRuntime runtime, SoftRobotMultiJointDeviceDriver driver,
			SoftRobotMultiJointMapper mapper) {
		this.runtime = runtime;
		this.driver = driver;
		this.mapper = mapper;
	}

	@Override
	public LinkBuilderResult buildLink(final DataflowType from, final DataflowType to) throws MappingException {

		try {
			if (from == null && to != null && to.providesValueFor(mapper.getJointsDataflowType(driver))) {
				NetFragment ret = new NetFragment("Robot monitor");
				DataflowOutPort result = mapper.addMultiJointMonitorFragment(runtime, ret, driver).jointValues;
				return new LinkBuilderResult(ret, null, result);
			}
			if (from == null && to != null && to.providesValueFor(mapper.getJointVelsDataflowType(driver))) {
				NetFragment ret = new NetFragment("Robot monitor");
				DataflowOutPort result = mapper.addMultiJointMonitorFragment(runtime, ret, driver).jointVelValues;
				return new LinkBuilderResult(ret, null, result);
			}

			for (Joint j : driver.getJoints()) {

				if (from != null && to != null && from.providesValueFor(mapper.getJointsDataflowType(driver))
						&& new JointDataflow(j.getDriver()).providesValueFor(to)) {

					NetFragment fragment = new NetFragment("Extract joint position");

					DataflowThroughInPort inPos = fragment
							.addInPort(new DataflowThroughInPort(mapper.getJointsDataflowType(driver)));
					ComposedDataflow composedType = new ComposedDataflow();
					for (int i = 0; i < driver.getJointCount(); i++) {
						composedType.addDataflow(new JointDataflow(driver.getJoint(i).getDriver()));
					}
					DataflowThroughOutPort outPos = fragment
							.addOutPort(new DataflowThroughOutPort(true, inPos, composedType));

					DataflowThroughOutPort jointOut = fragment.addThroughPort(true, new JointDataflow(j.getDriver()));

					fragment.connect(outPos, jointOut.getInPort());

					return new LinkBuilderResult(fragment, inPos, jointOut);

				}
			}

			for (Joint j : driver.getJoints()) {

				if (from != null && to != null && from.providesValueFor(mapper.getJointVelsDataflowType(driver))
						&& new JointVelDataflow(j.getDriver()).providesValueFor(to)) {

					NetFragment fragment = new NetFragment("Extract joint velocity");

					DataflowThroughInPort inPos = fragment
							.addInPort(new DataflowThroughInPort(mapper.getJointVelsDataflowType(driver)));
					ComposedDataflow composedType = new ComposedDataflow();
					for (int i = 0; i < driver.getJointCount(); i++) {
						composedType.addDataflow(new JointVelDataflow(driver.getJoint(i).getDriver()));
					}
					DataflowThroughOutPort outPos = fragment
							.addOutPort(new DataflowThroughOutPort(true, inPos, composedType));

					DataflowThroughOutPort jointOut = fragment.addThroughPort(true,
							new JointVelDataflow(j.getDriver()));

					fragment.connect(outPos, jointOut.getInPort());

					return new LinkBuilderResult(fragment, inPos, jointOut);

				}
			}

			if (from != null && to != null && from.providesValueFor(mapper.getJointsDataflowType(driver))
					&& to.providesValueFor(mapper.getJointVelsDataflowType(driver))) {
				NetFragment ret = new NetFragment("joint positions -> joint velocities");

				DataflowThroughOutPort cmdJoints = ret.addThroughPort(true, mapper.getJointsDataflowType(driver));

				ComposedDataflow cmdJointComp = new ComposedDataflow();
				for (Joint j : driver.getJoints()) {
					cmdJointComp.addDataflow(new JointDataflow(j.getDriver()));
				}

				DataflowOutPort jointCompOut = ret.reinterpret(cmdJoints, cmdJointComp);

				List<DataflowOutPort> jointVelOuts = new ArrayList<DataflowOutPort>();

				for (Joint j : driver.getJoints()) {
					DataflowThroughOutPort currJointOut = ret.addThroughPort(true, new JointDataflow(j.getDriver()));

					ret.connect(jointCompOut, currJointOut.getInPort());

					DataflowThroughOutPort currJointVelOut = ret.addThroughPort(true,
							new JointVelDataflow(j.getDriver()));

					ret.connect(currJointOut, currJointVelOut.getInPort());

					jointVelOuts.add(currJointVelOut);
				}

				ComposedDataflowOutPort cmdJointVelCompOut = ret.addOutPort(new ComposedDataflowOutPort(true,
						jointVelOuts.toArray(new DataflowOutPort[jointVelOuts.size()])));

				DataflowOutPort cmdJointVelsOut = ret.reinterpret(cmdJointVelCompOut,
						mapper.getJointVelsDataflowType(driver));

				return new LinkBuilderResult(ret, cmdJoints.getInPort(), cmdJointVelsOut);
			}
			if (from != null && to != null && from.providesValueFor(mapper.getJointVelsDataflowType(driver))
					&& to.providesValueFor(mapper.getJointsDataflowType(driver))) {
				NetFragment ret = new NetFragment("joint velocities -> joint positions");

				DataflowThroughOutPort cmdJointVels = ret.addThroughPort(true, mapper.getJointVelsDataflowType(driver));

				ComposedDataflow cmdJointVelComp = new ComposedDataflow();
				for (Joint j : driver.getJoints()) {
					cmdJointVelComp.addDataflow(new JointVelDataflow(j.getDriver()));
				}

				DataflowOutPort jointVelCompOut = ret.reinterpret(cmdJointVels, cmdJointVelComp);

				List<DataflowOutPort> jointOuts = new ArrayList<DataflowOutPort>();

				for (Joint j : driver.getJoints()) {
					DataflowThroughOutPort currJointVelOut = ret.addThroughPort(true,
							new JointVelDataflow(j.getDriver()));
					DataflowThroughOutPort currJointOut = ret.addThroughPort(true, new JointDataflow(j.getDriver()));

					ret.connect(jointVelCompOut, currJointVelOut.getInPort());

					ret.connect(currJointVelOut, currJointOut.getInPort());

					jointOuts.add(currJointOut);
				}

				ComposedDataflowOutPort cmdJointCompOut = ret.addOutPort(
						new ComposedDataflowOutPort(true, jointOuts.toArray(new DataflowOutPort[jointOuts.size()])));

				DataflowOutPort cmdJointsOut = ret.reinterpret(cmdJointCompOut, mapper.getJointsDataflowType(driver));

				return new LinkBuilderResult(ret, cmdJointVels.getInPort(), cmdJointsOut);
			}

			if (from == null && to != null && to.providesValueFor(new JointDataflow(null))) {
				JointDataflow toJ = (JointDataflow) to;

				if (toJ.getJointDriver() == null) {
					return null;
				}
				SensorMapperResult<Double> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
						toJ.getJointDriver().getCommandedPositionSensor(), null, null);

				return new LinkBuilderResult(mappedSensor.getNetFragment(), null, mappedSensor.getSensorPort());
			}

			if (from == null && to != null && to.providesValueFor(new JointVelDataflow(null))) {
				JointVelDataflow toJ = (JointVelDataflow) to;

				if (toJ.getJointDriver() == null) {
					return null;
				}
				SensorMapperResult<Double> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
						toJ.getJointDriver().getCommandedVelocitySensor(), null, null);

				return new LinkBuilderResult(mappedSensor.getNetFragment(), null, mappedSensor.getSensorPort());
			}

			if (from != null && to != null
			// && from.providesValueFor(new JointDataflow(null))
			// && to.providesValueFor(new
			// JointVelDataflow(null))
					&& (from instanceof JointDataflow) && (to instanceof JointVelDataflow)) {
				JointDataflow fromJ = (JointDataflow) from;
				JointVelDataflow toJ = (JointVelDataflow) to;

				JointDriver jointDriver = fromJ.getJointDriver();
				if (jointDriver != null && toJ.getJointDriver() != null && toJ.getJointDriver() != jointDriver) {
					return null;
				}

				if (jointDriver == null) {
					jointDriver = toJ.getJointDriver();
				}

				if (jointDriver == null) {
					return null;
				}

				NetFragment ret = new NetFragment("joint position -> joint velocity");

				DoubleMultiply invert = ret.add(new DoubleMultiply());
				invert.setFirst(-1d);
				DataflowInPort currJ = ret.addInPort(new JointDataflow(jointDriver), true, invert.getInSecond());

				ret.connect(null, currJ);

				DoubleAdd add = ret.add(new DoubleAdd());
				DataflowInPort inJ = ret.addInPort(from, true, add.getInFirst());

				add.getInSecond().connectTo(invert.getOutValue());

				CycleTime cycle = ret.add(new CycleTime());

				DoubleDivide div = ret.add(new DoubleDivide());

				div.getInFirst().connectTo(add.getOutValue());
				div.getInSecond().connectTo(cycle.getOutValue());

				DataflowOutPort outJVel = ret.addOutPort(toJ, true, div.getOutValue());

				return new LinkBuilderResult(ret, inJ, outJVel);

			}

			if (from != null && to != null
			// && from.providesValueFor(new
			// JointVelDataflow(null))
			// && to.providesValueFor(new JointDataflow(null))
					&& (from instanceof JointVelDataflow) && (to instanceof JointDataflow)) {
				JointVelDataflow fromJ = (JointVelDataflow) from;
				JointDataflow toJ = (JointDataflow) to;

				JointDriver jointDriver = fromJ.getJointDriver();
				if (jointDriver != null && toJ.getJointDriver() != null && toJ.getJointDriver() != jointDriver) {
					return null;
				}

				if (jointDriver == null) {
					jointDriver = toJ.getJointDriver();
				}

				if (jointDriver == null) {
					return null;
				}

				NetFragment ret = new NetFragment("joint velocity -> joint position");
				CycleTime cycle = ret.add(new CycleTime());

				DoubleMultiply mult = ret.add(new DoubleMultiply());
				mult.getInSecond().connectTo(cycle.getOutValue());
				DataflowInPort inJVel = ret.addInPort(from, true, mult.getInFirst());

				DoubleAdd add = ret.add(new DoubleAdd());
				add.getInFirst().connectTo(mult.getOutValue());

				DataflowInPort currJ = ret.addInPort(new JointDataflow(jointDriver), true, add.getInSecond());
				ret.connect(null, currJ);

				DataflowOutPort outJ = ret.addOutPort(toJ, true, add.getOutValue());

				return new LinkBuilderResult(ret, inJVel, outJ);

			}

		} catch (RPIException e) {
			throw new MappingException(e);
		}

		return null;
	}
}