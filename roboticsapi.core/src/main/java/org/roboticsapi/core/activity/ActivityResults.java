/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.Set;
import java.util.function.Consumer;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;

public interface ActivityResults {

	void provide(RoboticsConsumer<ActivityResult> consumer, Consumer<RoboticsException> error);

	void provide(Consumer<ActivityResult> consumer);

	ActivityResults cross(ActivityResults other);

	ActivityResults union(ActivityResults other);

	ActivityResults without(ActivityResult result);

	ActivityResults withMetadataFor(Set<Device> list);

}
