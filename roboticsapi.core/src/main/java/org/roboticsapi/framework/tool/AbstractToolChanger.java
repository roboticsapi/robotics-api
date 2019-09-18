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
import org.roboticsapi.core.world.AbstractPhysicalObject;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

public abstract class AbstractToolChanger extends AbstractPhysicalObject implements ToolChanger {

	private final Dependency<Frame> mountFrame, adapterFrame;

	public AbstractToolChanger() {
		mountFrame = createDependency("mountFrame");
		adapterFrame = createDependency("adapterFrame");
		createDependency("mountConnection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(mountFrame.get(), getBase());
			}
		});
		createDependency("mountPosition", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(mountFrame.get(), getBase(), getBase2MountTransformation());
			}
		});
		createDependency("adapterConnection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(getBase(), adapterFrame.get());
			}
		});
		createDependency("adapterPosition", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(getBase(), adapterFrame.get(), getBase2AdapterTransformation());
			}
		});
	}

	@Override
	public final Frame getAdapterFrame() {
		return adapterFrame.get();
	}

	@ConfigurationProperty
	public final void setAdapterFrame(Frame adapterFrame) {
		this.adapterFrame.set(adapterFrame);
	}

	@Override
	public final Frame getMountFrame() {
		return mountFrame.get();
	}

	@ConfigurationProperty
	public final void setMountFrame(Frame mountFrame) {
		this.mountFrame.set(mountFrame);
	}

	protected abstract Transformation getBase2MountTransformation();

	protected abstract Transformation getBase2AdapterTransformation();

}
