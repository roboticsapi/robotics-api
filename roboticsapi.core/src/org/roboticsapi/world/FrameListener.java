/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.extension.Extension;

public interface FrameListener extends Extension {

	public void onFrameAvailable(Frame frame);

	public void onFrameUnavailable(Frame frame);

	public void onFrameRenamed(String oldName, String newName);

}