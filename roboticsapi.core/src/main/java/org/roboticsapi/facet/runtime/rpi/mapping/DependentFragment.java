/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;

public class DependentFragment extends RuntimeFragment {

	public class PortDependency {
		private FragmentInPort inPort;
		private OutPort source;

		public PortDependency(FragmentInPort inPort, OutPort source) {
			this.inPort = inPort;
			this.source = source;
		}

		public FragmentInPort getInPort() {
			return inPort;
		}

		public OutPort getSource() {
			return source;
		}
	}

	public class Dependency<T> {
		private RealtimeValue<T> value;
		private FragmentInPort valuePort;
		private FragmentInPort timePort;

		public Dependency(RealtimeValue<T> value, FragmentInPort valuePort, FragmentInPort timePort) {
			this.value = value;
			this.timePort = timePort;
			this.valuePort = valuePort;
		}

		public RealtimeValue<T> getValue() {
			return value;
		}

		public FragmentInPort getTimePort() {
			return timePort;
		}

		public FragmentInPort getValuePort() {
			return valuePort;
		}
	}

	private List<Dependency<?>> dependencies = new ArrayList<Dependency<?>>();
	private List<PortDependency> portDependencies = new ArrayList<PortDependency>();

	public <T> void addDependency(RealtimeValue<T> value, FragmentInPort valuePort) {
		dependencies.add(new Dependency<T>(value, valuePort, null));
	}

	public <T> void addDependency(RealtimeValue<T> value, String portName, InPort valuePort) {
		dependencies.add(new Dependency<T>(value, addInPort(portName, valuePort), null));
	}

	public <T> void addDependency(FragmentInPort fragmentPort, OutPort source) {
		portDependencies.add(new PortDependency(fragmentPort, source));
	}

	public <T> void addDependency(RealtimeValue<T> value, FragmentInPort valuePort, FragmentInPort timePort) {
		dependencies.add(new Dependency<T>(value, valuePort, timePort));
	}

	protected FragmentInPort requireInPort(InPort port, String portName) throws RoboticsException {
		FragmentInPort ret = addInPort(portName);
		connect(ret.getInternalOutPort(), port);
		return ret;
	}

	public List<Dependency<?>> getDependencies() {
		return dependencies;
	}

	public List<PortDependency> getPortDependencies() {
		return portDependencies;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
