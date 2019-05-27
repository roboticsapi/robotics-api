/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.startup.configuration.xml;

import org.roboticsapi.world.Transformation;

public class RelationData {
	private final String from;
	private final String to;
	private final Transformation trans;
	private final String className;

	public RelationData(String from, String to, Transformation trans, String className) {
		super();
		this.from = from;
		this.to = to;
		this.trans = trans;
		this.className = className;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public Transformation getTrans() {
		return trans;
	}

	public String getClassName() {
		return className;
	}

}
