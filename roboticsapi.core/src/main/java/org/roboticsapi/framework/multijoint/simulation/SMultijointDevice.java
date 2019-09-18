/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.simulation;

import java.rmi.RemoteException;

import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.MultiDependency.Builder;
import org.roboticsapi.facet.javarcc.simulation.AbstractSimulationItem;
import org.roboticsapi.facet.javarcc.simulation.SimulationHelper;
import org.roboticsapi.facet.simulation.SEntity;
import org.roboticsapi.framework.multijoint.javarcc.devices.SIVelocityControlledMultijoint;

public abstract class SMultijointDevice extends SEntity {

	private final MultiDependency<SVelocityControlledAxis> axes;

	public SMultijointDevice(int jointCount) {
		axes = createMultiDependency("axis", jointCount, new Builder<SVelocityControlledAxis>() {
			@Override
			public SVelocityControlledAxis create(int index) {
				SVelocityControlledAxis axis = createAxis(index);
				axis.setWorld(getWorld());
				return axis;
			}
		});
	}

	public abstract SVelocityControlledAxis createAxis(int index);

	public final SVelocityControlledAxis getAxis(int index) {
		return axes.get(index);
	}

	@Override
	public final double getSimulationHz() {
		return 1;
	}

	@Override
	public final void simulateStep(Long time) {
	}

	private class VelocityControlledMultijoint extends AbstractSimulationItem
			implements SIVelocityControlledMultijoint {
		private static final long serialVersionUID = -2036416336197220227L;

		protected VelocityControlledMultijoint() throws RemoteException {
			super();
		}

		@Override
		public double getMeasuredJointPosition(int joint) throws RemoteException {
			return axes.get(joint).getMeasuredJointPosition();
		}

		@Override
		public double getMeasuredJointVelocity(int joint) throws RemoteException {
			return axes.get(joint).getMeasuredJointVelocity();
		}

		@Override
		public void setJointVelocity(int joint, double velocity) throws RemoteException {
			axes.get(joint).setJointVelocity(velocity);
		}

	}

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		SimulationHelper.registerSimuationItem(getIdentifier(), () -> new VelocityControlledMultijoint());
	}

	@Override
	protected void beforeUninitialization() {
		SimulationHelper.unregisterSimulationItem(getIdentifier());
		super.beforeUninitialization();
	}

}
