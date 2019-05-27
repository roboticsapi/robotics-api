/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.roboticsapi.runtime.core.primitives.BooleanOr;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;

public class OrFragment extends NetFragment {

	private DataflowOutPort orOut = null;

	public OrFragment(DataflowType type, DataflowOutPort... ports) throws MappingException {
		this(type, Arrays.asList(ports));
	}

	public OrFragment(DataflowType type, List<DataflowOutPort> ports) throws MappingException {
		this(type, ports, null);
	}

	public OrFragment(DataflowType type, List<DataflowOutPort> portList, String fragmentName) throws MappingException {
		super(fragmentName != null ? fragmentName : "Or");

		while (portList.contains(null)) {
			portList.remove(null);
		}

		Set<DataflowOutPort> ports = new HashSet<DataflowOutPort>(portList);
		Iterator<DataflowOutPort> iterator = ports.iterator();
		if (ports.size() == 0) {
			// false
			BooleanValue value = add(new BooleanValue(false));
			setOrOut(addOutPort(type, true, value.getOutValue()));

		} else if (ports.size() == 1) {
			// the one port
			setOrOut(iterator.next());

		} else {

			// or them
			DataflowInPort inFirst = null, inSecond = null;
			DataflowOutPort out, result = null;
			for (int i = 0; i < ports.size(); i++) {
				// child event
				final DataflowOutPort eventOut = iterator.next();

				if (i == ports.size() - 1) {
					connect(eventOut, inSecond);
				} else {
					// boolean or
					final BooleanOr or = add(new BooleanOr(false, false));
					out = addOutPort(type, true, or.getOutValue());
					if (inSecond != null) {
						connect(out, inSecond);
					} else {
						result = out;
					}

					inFirst = addInPort(type, true, or.getInFirst());
					inSecond = addInPort(type, false, or.getInSecond());

					// connect it all
					connect(eventOut, inFirst);
				}
			}
			setOrOut(result);
		}
	}

	private void setOrOut(DataflowOutPort orOut) {
		this.orOut = orOut;
	}

	public DataflowOutPort getOrOut() {
		return orOut;
	}

}
