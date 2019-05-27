/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.roboticsapi.core.exception.RoboticsException;

public class AggregatedRoboticsException extends RoboticsException {
	private static final long serialVersionUID = 1L;

	private final List<RoboticsException> aggregatedExceptions = new ArrayList<RoboticsException>();

	public AggregatedRoboticsException(String message, RoboticsException otherException,
			RoboticsException... furtherExceptions) {
		super(message);
		aggregatedExceptions.add(otherException);
		if (furtherExceptions != null) {
			aggregatedExceptions.addAll(Arrays.asList(furtherExceptions));
		}
	}

	public void aggregate(RoboticsException e) {
		aggregatedExceptions.add(e);
	}

	public List<RoboticsException> getAggregatedExceptions() {
		return Collections.unmodifiableList(aggregatedExceptions);
	}

}
