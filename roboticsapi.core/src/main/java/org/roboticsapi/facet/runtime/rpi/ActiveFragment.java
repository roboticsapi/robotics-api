/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

public class ActiveFragment extends Fragment {

	private final InPort inActive;

	public ActiveFragment() {
		inActive = new InPort("inActive");
		add(inActive);
	}

	public InPort getInActive() {
		return inActive;
	}
}
