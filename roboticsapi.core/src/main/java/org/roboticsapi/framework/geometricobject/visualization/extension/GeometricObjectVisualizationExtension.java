/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.geometricobject.visualization.extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.facet.visualization.property.Visualisation;
import org.roboticsapi.facet.visualization.property.VisualizationProperty;
import org.roboticsapi.framework.geometricobject.Box;

public class GeometricObjectVisualizationExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {

		if (object instanceof Box) {
			Box box = (Box) object;
			double x = box.getXExtent() / 2, y = box.getYExtent() / 2, z = box.getZExtent() / 2;
			String collada = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
					+ "<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.4.1\">\r\n"
					+ "  <asset>\r\n" + "    <unit name=\"meter\" meter=\"1\"/>\r\n" + "    <up_axis>Z_UP</up_axis>\r\n"
					+ "  </asset>\r\n" + "  <library_images/>\r\n" + "  <library_effects>\r\n"
					+ "    <effect id=\"me\">\r\n" + "      <profile_COMMON>\r\n"
					+ "        <technique sid=\"common\"><phong><emission><color sid=\"emission\">0 0 0 1</color></emission><ambient><color sid=\"ambient\">0 0 0 1</color></ambient><diffuse><color sid=\"diffuse\">0.64 0.64 0.64 1</color></diffuse><specular><color sid=\"specular\">0.5 0.5 0.5 1</color></specular><shininess><float sid=\"shininess\">50</float></shininess><index_of_refraction><float sid=\"index_of_refraction\">1</float></index_of_refraction></phong></technique>\r\n"
					+ "      </profile_COMMON>\r\n" + "    </effect>\r\n" + "  </library_effects>\r\n"
					+ "  <library_materials><material id=\"mm\" name=\"Material\"><instance_effect url=\"#me\"/></material></library_materials>\r\n"
					+ "  <library_geometries>\r\n" + "    <geometry id=\"Cube-mesh\" name=\"Cube\">\r\n"
					+ "      <mesh>\r\n" + "        <source id=\"cmp\">\r\n"
					+ "          <float_array id=\"cmpa\" count=\"24\">1 1 -1 1 -1 -1 -1 -1 -1 -1 1 -1 1 1 1 1 -1 1 -1 -1 1 -1 1 1</float_array>\r\n"
					+ "          <technique_common><accessor source=\"#cmpa\" count=\"8\" stride=\"3\"><param name=\"X\" type=\"float\"/><param name=\"Y\" type=\"float\"/><param name=\"Z\" type=\"float\"/></accessor></technique_common>\r\n"
					+ "        </source>\r\n" + "        <source id=\"cmn\">\r\n"
					+ "          <float_array id=\"cmna\" count=\"36\">0 0 -1 0 0 1 1 0 0 0 -1 0 -1 0 0 0 1 0 0 0 -1 0 0 1 1 0 0 0 -1 0 -1 0 0 0 1 0</float_array>\r\n"
					+ "          <technique_common><accessor source=\"#cmna\" count=\"12\" stride=\"3\"><param name=\"X\" type=\"float\"/><param name=\"Y\" type=\"float\"/><param name=\"Z\" type=\"float\"/></accessor></technique_common>\r\n"
					+ "        </source>\r\n"
					+ "        <vertices id=\"cmv\"><input semantic=\"POSITION\" source=\"#cmp\"/></vertices>\r\n"
					+ "        <polylist material=\"mm\" count=\"12\">\r\n"
					+ "		  <input semantic=\"VERTEX\" source=\"#cmv\" offset=\"0\"/>\r\n"
					+ "          <input semantic=\"NORMAL\" source=\"#cmn\" offset=\"1\"/>\r\n"
					+ "          <vcount>3 3 3 3 3 3 3 3 3 3 3 3 </vcount>\r\n"
					+ "          <p>0 0 1 0 2 0 7 1 6 1 5 1 4 2 5 2 1 2 5 3 6 3 2 3 2 4 6 4 7 4 4 5 0 5 3 5 3 6 0 6 2 6 4 7 7 7 5 7 0 8 4 8 1 8 1 9 5 9 2 9 3 10 2 10 7 10 7 11 4 11 3 11</p>\r\n"
					+ "        </polylist>\r\n" + "      </mesh>\r\n" + "    </geometry>\r\n"
					+ "  </library_geometries>\r\n" + "  <library_controllers/>\r\n" + "  <library_visual_scenes>\r\n"
					+ "    <visual_scene id=\"Scene\" name=\"Scene\">\r\n"
					+ "      <node id=\"Cube\" name=\"Cube\" type=\"NODE\">\r\n" + "        <matrix sid=\"transform\">"
					+ x + " 0 0 0 0 " + y + " 0 0 0 0 " + z + " 0 0 0 0 1</matrix>\r\n"
					+ "        <instance_geometry url=\"#Cube-mesh\"><bind_material><technique_common><instance_material symbol=\"mm\" target=\"#mm\"/></technique_common></bind_material></instance_geometry>\r\n"
					+ "      </node>\r\n" + "    </visual_scene>\r\n" + "  </library_visual_scenes>\r\n"
					+ "  <scene>\r\n" + "    <instance_visual_scene url=\"#Scene\"/>\r\n" + "  </scene>\r\n"
					+ "</COLLADA>";
			try {
				File model = File.createTempFile("box", ".dae");
				FileOutputStream fos = new FileOutputStream(model);
				fos.write(collada.getBytes());
				fos.close();
				model.deleteOnExit();
				box.addProperty(new VisualizationProperty(new Visualisation("COLLADA", model.toURI().toURL())));
			} catch (IOException e) {
				RAPILogger.logException(this, e);
			}
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO Auto-generated method stub

	}

}
