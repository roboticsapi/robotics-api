/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.matrix;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;

public class MatrixJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("Core::MatrixApply", JMatrixApply.class);
		ep.registerPrimitive("Core::MatrixArrayGet", JMatrixArrayGet.class);
		ep.registerPrimitive("Core::MatrixArraySet", JMatrixArraySet.class);
		ep.registerPrimitive("Core::MatrixBlockGet", JMatrixBlockGet.class);
		ep.registerPrimitive("Core::MatrixBlockSet", JMatrixBlockSet.class);
		ep.registerPrimitive("Core::MatrixCreate", JMatrixCreate.class);
		ep.registerPrimitive("Core::MatrixGet", JMatrixGet.class);
		ep.registerPrimitive("Core::MatrixMultiply", JMatrixMultiply.class);
		ep.registerPrimitive("Core::MatrixScale", JMatrixScale.class);
		ep.registerPrimitive("Core::MatrixSelect", JMatrixSelect.class);
		ep.registerPrimitive("Core::MatrixSet", JMatrixSet.class);
		ep.registerPrimitive("Core::MatrixSolve", JMatrixSolve.class);
		ep.registerPrimitive("Core::MatrixTranspose", JMatrixTranspose.class);
	}

}
