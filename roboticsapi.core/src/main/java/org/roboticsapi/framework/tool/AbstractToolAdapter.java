/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.tool;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.util.RoboticsEntityUtils;
import org.roboticsapi.core.world.AbstractPhysicalObject;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

public abstract class AbstractToolAdapter extends AbstractPhysicalObject implements ToolAdapter {

	private final Dependency<Frame> toolFrame;

	public AbstractToolAdapter() {
		toolFrame = createDependency("toolFrame");

		createDependency("connection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(getBase(), toolFrame.get());
			}
		});
		createDependency("position", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(getBase(), toolFrame.get(), getBase2ToolTransformation());
			}
		});
	}

	@Override
	public final Frame getToolFrame() {
		return toolFrame.get();
	}

	@ConfigurationProperty
	public final void setToolFrame(Frame toolFrame) {
		this.toolFrame.set(toolFrame);
	}

	@Override
	public final Tool getTool() {
		if (toolFrame != null) {
			return RoboticsEntityUtils.getAncestor(toolFrame.get(), Tool.class);
		}
		return null;
	}

	@Override
	public ToolChanger getToolChanger() {
		// TODO Implement
		return null;
	}

	protected abstract Transformation getBase2ToolTransformation();

}
