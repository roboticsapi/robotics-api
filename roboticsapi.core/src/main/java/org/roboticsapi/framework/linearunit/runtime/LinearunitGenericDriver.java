/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.linearunit.runtime;

import org.roboticsapi.framework.linearunit.Linearunit;
import org.roboticsapi.framework.linearunit.LinearunitDriver;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public abstract class LinearunitGenericDriver extends MultiJointDeviceGenericDriver<Linearunit>
		implements LinearunitDriver {

}
