/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.driver;

import java.util.List;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.OnlineObject;

public interface DeviceBasedLoadable<T extends Device> {

	public boolean build(T t);

	public void delete();

	public List<OnlineObject> getDependentObjects();

	public boolean checkDependentObjects();

}
