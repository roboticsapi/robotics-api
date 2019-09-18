/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public interface Plan {

	public double getTotalTime();

	public Double getTimeAtFirstOccurence(RealtimeBoolean state);
}
