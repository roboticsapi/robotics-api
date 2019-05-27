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

import org.roboticsapi.runtime.core.primitives.BooleanAnd;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;

public class AndFragment extends NetFragment {

	private DataflowOutPort andOut = null;

	public AndFragment(DataflowType type, DataflowOutPort... ports) throws MappingException {
		this(type, Arrays.asList(ports));
	}

	public AndFragment(DataflowType type, List<DataflowOutPort> ports) throws MappingException {
		this(type, ports, null);
	}

	public AndFragment(DataflowType type, List<DataflowOutPort> portList, String fragmentName) throws MappingException {
		super(fragmentName != null ? fragmentName : "And");

		while (portList.contains(null)) {
			portList.remove(null);
		}

		Set<DataflowOutPort> ports = new HashSet<DataflowOutPort>(portList);
		Iterator<DataflowOutPort> iterator = ports.iterator();
		if (ports.size() == 0) {
			// false
			BooleanValue value = add(new BooleanValue(true));
			setAndOut(addOutPort(type, true, value.getOutValue()));

		} else if (ports.size() == 1) {
			// the one port
			setAndOut(iterator.next());

		} else {

			// and them
			DataflowInPort inFirst = null, inSecond = null;
			DataflowOutPort out, result = null;
			for (int i = 0; i < ports.size(); i++) {
				// child event
				final DataflowOutPort eventOut = iterator.next();

				if (i == ports.size() - 1) {
					connect(eventOut, inSecond);
				} else {
					// boolean or
					final BooleanAnd or = add(new BooleanAnd(false, false));
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
			setAndOut(result);
		}
	}

	private void setAndOut(DataflowOutPort orOut) {
		this.andOut = orOut;
	}

	public DataflowOutPort getAndOut() {
		return andOut;
	}

}
