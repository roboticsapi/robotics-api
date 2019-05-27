/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool;

import org.roboticsapi.core.entity.Property;

public class ToolDescriptionProperty implements Property {

	private String manufacturer;
	private String type;
	private String description;

	public ToolDescriptionProperty(String manufacturer, String type, String description) {
		super();
		this.manufacturer = manufacturer;
		this.type = type;
		this.description = description;
	}

	/**
	 * Gets the tool's manufacturer.
	 * 
	 * @return the manufacturer.
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * Sets the tool's manufacturer.
	 * 
	 * @param manufacturer the manufacturer.
	 */
	protected void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * Gets the tool's type information.
	 * 
	 * @return the type information.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the tool's type information.
	 * 
	 * @param type the type.
	 */
	protected void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the tool's description.
	 * 
	 * @return the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the tool's description.
	 * 
	 * @param description the description.
	 */
	protected void setDescription(String description) {
		this.description = description;
	}

}
