/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.action.PlannedAction.TimeProgressState;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.EdgeDetection;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class PlannedActionMapperResult extends BaseActionMapperResult {

	private final class ProgressStatePortFactory extends AbstractStateSinglePortFactory<TimeProgressState> {

		@Override
		public DataflowOutPort createTypedStateSinglePort(TimeProgressState state) throws MappingException {
			// compare tick scaler output to percent
			try {
				final NetFragment ret = new NetFragment("Motion.Progress");
				getNetFragment().add(ret);
				final DoubleGreater cmp = new DoubleGreater();
				cmp.setSecond(state.getProgress());
				EdgeDetection edge = ret.add(new EdgeDetection(true));

				ret.add(cmp);
				ret.connect(progressPort, ret.addInPort(new DoubleDataflow(), true, cmp.getInFirst()));
				edge.getInValue().connectTo(cmp.getOutValue());
				return ret.addOutPort(new StateDataflow(), true, edge.getOutValue());
			} catch (MappingException e) {
				e.printStackTrace();
				return null;
			} catch (RPIException e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	private final DataflowOutPort progressPort;

	public PlannedActionMapperResult(Action action, NetFragment fragment, ActionResult result,
			DataflowOutPort completedPort, DataflowOutPort progressPort) {
		super(action, fragment, result, completedPort);
		this.progressPort = progressPort;

		addStatePortFactory(TimeProgressState.class, new ProgressStatePortFactory());
	}

	public DataflowOutPort getProgressPort() {
		return progressPort;
	}

}
