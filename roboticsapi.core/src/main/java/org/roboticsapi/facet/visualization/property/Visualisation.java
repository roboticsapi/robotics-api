/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.visualization.property;

import java.net.URL;

import org.roboticsapi.core.world.Transformation;

public class Visualisation {

	private final String type;
	private final URL mainFile;
	private final URL[] files;
	private final Transformation transformation;

	public Visualisation(String type, URL mainFile, URL... files) {
		this(type, Transformation.IDENTITY, mainFile, files);
	}

	public Visualisation(String type, Transformation transformation, URL mainFile, URL... files) {
		this.type = type;
		this.transformation = transformation;
		this.mainFile = mainFile;
		this.files = files;
	}

	public String getType() {
		return type;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	public URL getMainFile() {
		return mainFile;
	}

	public String getMainFileName() {
		String name = null;
		if (type.equals("COLLADA")) {
			name = mainFile.getFile().replace(".dae", "");
			String[] names = name.split("/");
			name = names[names.length - 1];
		}
		return name;
	}

	public URL[] getFiles() {
		return files;
	}

}
