/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.link;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

public abstract class Link extends AbstractComposedEntity implements PhysicalObject {

	private Transformation transformation;
	private StaticConnection relation;

	public Link() {
		super();
	}

	public Link(Transformation transformation) {
		this();
		setTransformation(transformation);
	}

	@Override
	public abstract Frame getBase();

	public abstract Frame getLinked();

	@ConfigurationProperty(Optional = false)
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
		if (isInitialized()) {
			try {
				resetup();
			} catch (InitializationException e) {
				RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL, "Error while setting Link transformation", e);
			} catch (EntityException e) {
				RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL, "Error while setting Link transformation", e);
			}
		}
	}

	protected void resetup() throws InitializationException, EntityException {
		if (relation != null) {
			getBase().removeRelation(relation);
			relation.setParent(null);
		}

		relation = new StaticConnection(transformation);
		relation.setParent(this);
		getBase().addRelation(relation, getLinked());
	}

	/**
	 * Returns the link's connection between base and linked frame.
	 *
	 * @return the link's connection between base and linked frame.
	 */
	public Connection getConnection() {
		return this.relation;
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		resetup();
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		relation.getFrom().removeRelation(relation);
		relation.setParent(null);
		relation = null;

		super.cleanupEntities();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getName() + "'";
	}

}
