/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import java.util.List;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.mapping.LinkBuilder;
import org.roboticsapi.runtime.mapping.LinkBuilderResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflow;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.robot.NullspaceJointsDataflow;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmMapper.InvKinematicsFragment;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmMapper.NullspaceInvKinematicsFragment;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.world.Relation;

public class SoftRobotRobotArmLinkBuilder implements LinkBuilder {
	private final DeviceParameterBag parameters;
	private final SoftRobotRobotArmDriver driver;
	private final SoftRobotRobotArmMapper mapper;

	public SoftRobotRobotArmLinkBuilder(DeviceParameterBag parameters, SoftRobotRobotArmDriver driver,
			SoftRobotRobotArmMapper mapper) {
		this.parameters = parameters;
		this.driver = driver;
		this.mapper = mapper;
	}

	@Override
	public LinkBuilderResult buildLink(final DataflowType from, final DataflowType to) throws MappingException {

		if (from != null && to != null && from instanceof RelationDataflow
				&& mapper.getJointsDataflowType(driver).providesValueFor(to)) {
			final RelationDataflow f = (RelationDataflow) from;

			List<Relation> relationsTo = driver.getBase().getRelationsTo(driver.getFlange());

			Relation[] baseFlange = new Relation[relationsTo.size()];

			relationsTo.toArray(baseFlange);

			if ((f.getFrom().getRelationsTo(driver.getBase(), baseFlange) != null
					&& f.getTo().getRelationsTo(driver.getFlange(), baseFlange) != null)
					|| (f.getFrom().getRelationsTo(driver.getFlange(), baseFlange) != null
							&& f.getTo().getRelationsTo(driver.getBase(), baseFlange) != null)) {

				NetFragment ret = new NetFragment("Inverse Kinematics");

				InvKinematicsFragment fragment = ret.add(mapper.createInvKinematicsFragment(driver, parameters));
				fragment.connect(null, fragment.getInHintJoints());
				try {
					DataflowInPort input;

					input = ret.addConverterLink(fragment.getInFrame(), from);
					return new LinkBuilderResult(ret, input, fragment.getOutJoints());
				} catch (MappingException e) {
				}
			}
		}

		if (from != null && to != null
				&& from.providesValueFor(new NullspaceJointsDataflow(driver.getJointCount(), driver))
				&& from.providesValueFor(new RelationDataflow(null, null))
				&& mapper.getJointsDataflowType(driver).providesValueFor(to)) {
			RelationDataflow f = null;
			if (from instanceof ComposedDataflow) {
				List<DataflowType> children = ((ComposedDataflow) from).getChildren();
				for (DataflowType child : children) {
					if (child instanceof RelationDataflow) {
						f = (RelationDataflow) child;
					}
				}
			}
			if (f != null) {
				List<Relation> relationsTo = driver.getBase().getRelationsTo(driver.getFlange());

				Relation[] baseFlange = new Relation[relationsTo.size()];

				relationsTo.toArray(baseFlange);

				if ((f.getFrom().getRelationsTo(driver.getBase(), baseFlange) != null
						&& f.getTo().getRelationsTo(driver.getFlange(), baseFlange) != null)
						|| (f.getFrom().getRelationsTo(driver.getFlange(), baseFlange) != null
								&& f.getTo().getRelationsTo(driver.getBase(), baseFlange) != null)) {

					NetFragment ret = new NetFragment("Kinematics");

					InvKinematicsFragment fragment = ret.add(mapper.createInvKinematicsFragment(driver, parameters));

					try {
						DataflowInPort inFrame = ret.addConverterLink(fragment.getInFrame(), f);
						ComposedDataflowInPort in = new ComposedDataflowInPort(true, inFrame);

						if (fragment instanceof NullspaceInvKinematicsFragment) {
							DataflowInPort inHintJoints = ret.reinterpret(
									((NullspaceInvKinematicsFragment) fragment).getInNullspaceJoints(),
									new NullspaceJointsDataflow(driver.getJointCount(), driver));
							in.addInPort(inHintJoints);
						}

						return new LinkBuilderResult(ret, in, fragment.getOutJoints());
					} catch (MappingException e) {
					}
				}
			}
		}

		if (from != null && to != null && from instanceof VelocityDataflow
				&& mapper.getJointsDataflowType(driver).providesValueFor(to)) {

			NetFragment fragment = new NetFragment("Velocity kinematics");

			DataflowThroughOutPort throughOutPort = fragment.addThroughPort(true,
					new RelationDataflow(driver.getBase(), driver.getFlange()));

			DataflowInPort input = fragment.addConverterLink(throughOutPort.getInPort(), from);
			DataflowOutPort output = fragment.addConverterLink(throughOutPort, to);

			return new LinkBuilderResult(fragment, input, output);

		}

		return null;
	}
}